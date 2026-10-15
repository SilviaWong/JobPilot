package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.*;
import com.jobpilot.jobpilotserver.model.entity.*;
import com.jobpilot.jobpilotserver.model.vo.JobDetailVO;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import com.jobpilot.jobpilotserver.service.AiSettingsService;
import com.jobpilot.jobpilotserver.service.JobService;
import com.jobpilot.jobpilotserver.service.ResumeService;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerFactory;
import com.jobpilot.jobpilotserver.util.LlmResponseParser;
import com.github.benmanes.caffeine.cache.Cache;
import com.jobpilot.jobpilotserver.common.exception.BusinessException;
import com.jobpilot.jobpilotserver.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 岗位核心业务实现类
 * <p>
 * 主要职责：
 * 1. 提供复杂多条件筛选的岗位分页查询（集成隐藏/过期状态、收藏、黑名单过滤、平台、学历、关键词等）；
 * 2. 批量关联查询关联数据（AI 评估结果、跨平台详情 JobDetail、企业信息 Company），避免 N+1 性能问题；
 * 3. 结合 JobNormalizerFactory 策略模式对多源异构岗位数据进行字段清洗与归一化；
 * 4. 岗位属性流转维护与批量更新/删除操作；
 * 5. 集成大模型 (LLM) 提供岗位 AI 匹配度诊断、Boss打招呼语智能生成及高频面试题预测。
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobMapper jobMapper;
    private final AiJobResultMapper aiJobResultMapper;
    private final CompanyMapper companyMapper;
    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final JobDetailMapper jobDetailMapper;
    private final JobStatusLogMapper jobStatusLogMapper;
    private final ResumeProfileMapper resumeProfileMapper;
    private final AiSettingsService aiSettingsService;
    private final ResumeService resumeService;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;
    private final JobNormalizerFactory jobNormalizerFactory;
    private final Cache<String, Set<String>> blacklistedCompanyCache;

    public JobServiceImpl(JobMapper jobMapper,
                          AiJobResultMapper aiJobResultMapper,
                          CompanyMapper companyMapper,
                          BlacklistedCompanyMapper blacklistedCompanyMapper,
                          JobDetailMapper jobDetailMapper,
                          JobStatusLogMapper jobStatusLogMapper,
                          ResumeProfileMapper resumeProfileMapper,
                          AiSettingsService aiSettingsService,
                          ResumeService resumeService,
                          LlmClient llmClient,
                          ObjectMapper objectMapper,
                          JobNormalizerFactory jobNormalizerFactory,
                          Cache<String, Set<String>> blacklistedCompanyCache) {
        this.jobMapper = jobMapper;
        this.aiJobResultMapper = aiJobResultMapper;
        this.companyMapper = companyMapper;
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.jobDetailMapper = jobDetailMapper;
        this.jobStatusLogMapper = jobStatusLogMapper;
        this.resumeProfileMapper = resumeProfileMapper;
        this.aiSettingsService = aiSettingsService;
        this.resumeService = resumeService;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
        this.jobNormalizerFactory = jobNormalizerFactory;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
    }

    /**
     * 多条件综合分页查询岗位列表
     * <p>
     * 处理步骤：
     * 1. 构建 MyBatis-Plus 查询条件（隐藏、状态、收藏、平台、学历、关键词模糊查询）；
     * 2. 利用 Caffeine 本地缓存快速获取所有黑名单企业名称，并在 SQL 层面执行 notIn 过滤；
     * 3. 执行物理分页查询；
     * 4. 批量预查当前页职位对应的 AI 诊断结果、JobDetail 详情、Company 企业信息，组装内存 Map 避免循环查库；
     * 5. 遍历分页记录，使用 JobNormalizerFactory 对多平台原始数据进行统一清洗并组装 VO；
     * 6. 支持根据 AI 诊断状态 (已诊断/未诊断) 进行内存二次过滤。
     *
     * @param page 页码
     * @param pageSize 每页条数
     * @param status 状态筛选（normal、expired、all 等）
     * @param filterFavoritesOnly 是否仅查看收藏
     * @param filterShowHidden 是否查看已隐藏/已失效职位
     * @param filterShowBlacklisted 是否显示黑名单企业职位
     * @param platform 来源平台筛选（Boss直聘、前程无忧、智联招聘、猎聘等）
     * @param education 学历要求筛选
     * @param keyword 职位名称或企业名称搜索关键字
     * @param aiDiagnosisFilter AI 诊断状态过滤（all, diagnosed, undiagnosed）
     * @return 包含 data、total、hasMore 的分页数据 Map
     */
    @Override
    public Map<String, Object> getJobs(int page, int pageSize, String status, Boolean filterFavoritesOnly,
                                       Boolean filterShowHidden, Boolean filterShowBlacklisted,
                                       String platform, String education, String keyword, String aiDiagnosisFilter) {
        return getJobs(page, pageSize, status, filterFavoritesOnly, filterShowHidden, filterShowBlacklisted,
                platform, education, keyword, aiDiagnosisFilter, "all", "all", "all", "all", "all", "updatedAt");
    }

    @Override
    public Map<String, Object> getJobs(int page, int pageSize, String status, Boolean filterFavoritesOnly,
                                       Boolean filterShowHidden, Boolean filterShowBlacklisted,
                                       String platform, String education, String keyword, String aiDiagnosisFilter,
                                       String headhunterFilter, String city, String salaryFilter,
                                       String lifecycleFilter, String hrActiveFilter, String sortBy) {
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();

        // 1. 隐藏与状态过滤逻辑
        if (Boolean.TRUE.equals(filterShowHidden)) {
            // 查看回收站/隐藏职位：展示主动隐藏的或已过期的职位
            queryWrapper.and(w -> w.eq("isHidden", true).or().eq("status", "expired"));
        } else if ("all".equalsIgnoreCase(status)) {
            // 查看全部，不加状态限制
        } else {
            // 默认只展示未隐藏且状态正常的职位
            queryWrapper.and(w -> w.eq("isHidden", false).or().isNull("isHidden"))
                        .eq("status", status != null ? status : "normal");
        }

        // 2. 仅看收藏过滤
        if (Boolean.TRUE.equals(filterFavoritesOnly)) {
            queryWrapper.eq("isFavorited", true);
        }

        // 3. 猎头/代招标记过滤
        if ("direct".equalsIgnoreCase(headhunterFilter)) {
            queryWrapper.and(w -> w.eq("isHeadhunter", false).or().isNull("isHeadhunter"));
        } else if ("headhunter".equalsIgnoreCase(headhunterFilter)) {
            queryWrapper.eq("isHeadhunter", true);
        }

        // 4. 来源招聘平台过滤
        if (platform != null && !"all".equalsIgnoreCase(platform) && !platform.isEmpty()) {
            queryWrapper.eq("platform", platform);
        }

        // 5. 城市与地区过滤
        if (city != null && !"all".equalsIgnoreCase(city) && !city.isEmpty()) {
            queryWrapper.and(w -> w.like("city", city)
                    .or().eq("cityCode", city)
                    .or().like("location", city));
        }

        // 6. 学历门槛过滤
        if (education != null && !"all".equalsIgnoreCase(education) && !education.isEmpty()) {
            queryWrapper.like("education", education);
        }

        // 7. 职位名称 / 公司名称关键词模糊检索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper ->
                wrapper.like("title", keyword)
                       .or().like("companyName", keyword)
                       .or().like("companyFullName", keyword)
                       .or().like("clientCompanyName", keyword)
            );
        }

        // 8. 薪资区间过滤
        if (salaryFilter != null && !"all".equalsIgnoreCase(salaryFilter)) {
            if ("under_15k".equalsIgnoreCase(salaryFilter)) {
                queryWrapper.and(w -> w.le("salaryAvg", 15.0).or().le("salaryMin", 15.0));
            } else if ("15k_25k".equalsIgnoreCase(salaryFilter)) {
                queryWrapper.ge("salaryAvg", 15.0).le("salaryAvg", 25.0);
            } else if ("25k_35k".equalsIgnoreCase(salaryFilter)) {
                queryWrapper.ge("salaryAvg", 25.0).le("salaryAvg", 35.0);
            } else if ("above_35k".equalsIgnoreCase(salaryFilter)) {
                queryWrapper.ge("salaryAvg", 35.0);
            }
        }

        // 9. 职位生命周期过滤
        if (lifecycleFilter != null && !"all".equalsIgnoreCase(lifecycleFilter)) {
            LocalDateTime now = LocalDateTime.now();
            if ("new_7d".equalsIgnoreCase(lifecycleFilter)) {
                queryWrapper.ge("firstSeen", now.minusDays(7));
            } else if ("active_30d".equalsIgnoreCase(lifecycleFilter)) {
                queryWrapper.ge("lastSeen", now.minusDays(30));
            } else if ("refresh_3d".equalsIgnoreCase(lifecycleFilter)) {
                queryWrapper.ge("lastSeen", now.minusDays(3));
            } else if ("long_term_60d".equalsIgnoreCase(lifecycleFilter)) {
                queryWrapper.le("firstSeen", now.minusDays(60));
            }
        }

        // 10. HR 活跃度过滤
        if (hrActiveFilter != null && !"all".equalsIgnoreCase(hrActiveFilter) && !hrActiveFilter.isEmpty()) {
            queryWrapper.eq("hrActiveLevel", hrActiveFilter);
        }

        // 11. 黑名单过滤 (利用 Caffeine 本地缓存，避免频繁全表扫描 blacklisted_company)
        Set<String> blacklistedNames = blacklistedCompanyCache.get("all_blacklisted", k -> {
            log.debug("Loading blacklisted company names into Caffeine cache...");
            return blacklistedCompanyMapper.selectList(null).stream()
                    .map(BlacklistedCompany::getCompanyName)
                    .collect(Collectors.toSet());
        });

        if (Boolean.FALSE.equals(filterShowBlacklisted) || filterShowBlacklisted == null) {
            if (blacklistedNames != null && !blacklistedNames.isEmpty()) {
                queryWrapper.notIn("companyName", blacklistedNames);
            }
        }

        // 12. 排序逻辑
        if ("salaryAvgDesc".equalsIgnoreCase(sortBy)) {
            queryWrapper.orderByDesc("salaryAvg");
        } else if ("salaryAvgAsc".equalsIgnoreCase(sortBy)) {
            queryWrapper.orderByAsc("salaryAvg");
        } else if ("publishTime".equalsIgnoreCase(sortBy)) {
            queryWrapper.orderByDesc("firstSeen");
        } else {
            queryWrapper.orderByDesc("updatedAt");
        }

        // 13. 执行分页查询
        Page<Job> jobPage = new Page<>(page, pageSize);
        Page<Job> resultPage = jobMapper.selectPage(jobPage, queryWrapper);

        List<Job> records = resultPage.getRecords();

        // 提取当前页所有 JobId 集合
        List<String> jobIds = records.stream().map(Job::getJobId).collect(Collectors.toList());

        // 14. 批量预查 AI 诊断结果 (批量 in 查询，O(1) 内存 Map 映射)
        Map<String, AiJobResult> aiResultMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            QueryWrapper<AiJobResult> aiWrapper = new QueryWrapper<>();
            aiWrapper.in("jobId", jobIds);
            aiJobResultMapper.selectList(aiWrapper).forEach(r -> aiResultMap.put(r.getJobId(), r));
        }

        // 15. 批量预查跨平台职位详情 JobDetail
        Map<String, JobDetail> jobDetailMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            QueryWrapper<JobDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.in("jobId", jobIds);
            jobDetailMapper.selectList(detailWrapper).forEach(d -> jobDetailMap.put(d.getPlatform() + "_" + d.getJobId(), d));
        }

        // 16. 批量预查企业详细信息 Company
        Map<String, Company> companyMap = new HashMap<>();
        if (!records.isEmpty()) {
            QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
            List<String> compNames = records.stream().map(Job::getCompanyName).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (!compNames.isEmpty()) {
                companyQueryWrapper.in("companyName", compNames);
                companyMapper.selectList(companyQueryWrapper).forEach(c -> companyMap.put(c.getSourcePlatform() + "_" + c.getCompanyName(), c));
            }
        }

        // 17. 遍历结果集，执行数据归一化与 VO 组装
        List<JobDetailVO> detailVOList = records.stream().map(job -> {
            JobDetailVO vo = new JobDetailVO();
            vo.setId(job.getId());
            vo.setJobId(job.getJobId());
            vo.setTitle(job.getTitle());
            vo.setCompanyName(job.getCompanyName());
            vo.setSalary(job.getSalary());
            vo.setLocation(job.getLocation());
            vo.setEducation(job.getEducation());
            vo.setPlatform(job.getPlatform());
            vo.setStatus(job.getStatus());
            vo.setIsFavorited(job.getIsFavorited());
            vo.setIsHidden(job.getIsHidden());
            vo.setTags(job.getTags());
            vo.setRawData(job.getRawData());
            vo.setRawData2(job.getRawData2());
            vo.setCreatedAt(job.getCreatedAt());
            vo.setUpdatedAt(job.getUpdatedAt());

            JsonNode raw1 = parseJson(job.getRawData());
            JsonNode raw2 = parseJson(job.getRawData2());

            JobDetail singleDetail = jobDetailMap.get(job.getPlatform() + "_" + job.getJobId());
            JsonNode singleDetailNode = singleDetail != null ? parseJson(singleDetail.getRawData()) : null;

            Company comp = companyMap.get(job.getPlatform() + "_" + job.getCompanyName());
            JsonNode compRaw = comp != null ? parseJson(comp.getRawData()) : null;

            // 调用统一策略工厂，将多平台特有数据结构标准化为 NormalizedJobDataVO
            NormalizedJobDataVO normalizedData = jobNormalizerFactory.normalize(job, raw1, raw2, compRaw, singleDetailNode);

            vo.setNormalizedData(normalizedData);
            vo.setCompany(comp);
            vo.setAiResult(aiResultMap.get(job.getJobId()));
            vo.setIsBlacklisted(blacklistedNames.contains(job.getCompanyName()));

            return vo;
        }).collect(Collectors.toList());

        // 18. AI 诊断状态过滤（全部/已诊断/未诊断）
        if (aiDiagnosisFilter != null && !"all".equalsIgnoreCase(aiDiagnosisFilter)) {
            detailVOList = detailVOList.stream().filter(item -> {
                AiJobResult r = item.getAiResult();
                if ("diagnosed".equalsIgnoreCase(aiDiagnosisFilter)) {
                    return r != null;
                } else if ("undiagnosed".equalsIgnoreCase(aiDiagnosisFilter)) {
                    return r == null;
                }
                return true;
            }).collect(Collectors.toList());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", detailVOList);
        res.put("total", resultPage.getTotal());
        res.put("hasMore", (page * pageSize) < resultPage.getTotal());
        return res;
    }

    /**
     * 辅助解析 JSON 字符串为 JsonNode
     */
    private JsonNode parseJson(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            return objectMapper.readTree(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新单个职位的状态（例如 normal, expired 等）
     *
     * @param id 职位主键 ID
     * @param status 目标状态
     * @return 更新后的 Job 实体
     */
    @Override
    public Job updateJobStatus(String id, String status) {
        return updateJobStatus(id, status, null);
    }

    @Override
    public Job updateJobStatus(String id, String status, String note) {
        Job job = jobMapper.selectById(id);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        String fromStatus = job.getApplyStatus() != null ? job.getApplyStatus() : (job.getStatus() != null ? job.getStatus() : "normal");

        job.setStatus(status);
        job.setApplyStatus(status);
        job.setStatusUpdatedAt(LocalDateTime.now());
        if ("applied".equalsIgnoreCase(status) && job.getAppliedAt() == null) {
            job.setAppliedAt(LocalDateTime.now());
        }
        job.setUpdatedAt(LocalDateTime.now());
        jobMapper.updateById(job);

        if (!Objects.equals(fromStatus, status)) {
            JobStatusLog statusLog = new JobStatusLog();
            statusLog.setJobRecordId(id);
            statusLog.setFromStatus(fromStatus);
            statusLog.setToStatus(status);
            statusLog.setNote(note);
            statusLog.setCreatedAt(LocalDateTime.now());
            jobStatusLogMapper.insert(statusLog);
        }

        return job;
    }

    /**
     * 更新职位的自定义属性（收藏、隐藏、自定义标签）
     *
     * @param id 职位主键 ID
     * @param isFavorited 收藏状态
     * @param isHidden 隐藏状态
     * @param tags 自定义标签列表
     * @return 更新后的 Job 实体
     */
    @Override
    public Job updateJobAttributes(String id, Boolean isFavorited, Boolean isHidden, List<String> tags) {
        Job job = jobMapper.selectById(id);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        if (isFavorited != null) job.setIsFavorited(isFavorited);
        if (isHidden != null) job.setIsHidden(isHidden);
        if (tags != null) {
            try {
                job.setTags(objectMapper.writeValueAsString(tags));
            } catch (Exception ignored) {}
        }
        job.setUpdatedAt(LocalDateTime.now());
        jobMapper.updateById(job);
        return job;
    }

    /**
     * 删除指定职位
     *
     * @param id 职位主键 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteJob(String id) {
        return jobMapper.deleteById(id) > 0;
    }

    /**
     * 批量更新多个职位的属性（收藏、隐藏、标签）
     *
     * @param ids 职位主键列表
     * @param isFavorited 收藏状态
     * @param isHidden 隐藏状态
     * @param tags 自定义标签列表
     * @return 成功更新的记录条数
     */
    /**
     * 批量更新多个职位的属性（收藏、隐藏、标签）- 单条批量 SQL 优化
     *
     * @param ids 职位主键列表
     * @param isFavorited 收藏状态
     * @param isHidden 隐藏状态
     * @param tags 自定义标签列表
     * @return 成功更新的记录条数
     */
    @Override
    public int batchUpdate(List<String> ids, Boolean isFavorited, Boolean isHidden, List<String> tags) {
        if (ids == null || ids.isEmpty()) return 0;
        String tagsJson = null;
        if (tags != null) {
            try {
                tagsJson = objectMapper.writeValueAsString(tags);
            } catch (Exception ignored) {}
        }

        LambdaUpdateWrapper<Job> updateWrapper = new LambdaUpdateWrapper<Job>()
                .in(Job::getId, ids)
                .set(Job::getUpdatedAt, LocalDateTime.now());

        if (isFavorited != null) updateWrapper.set(Job::getIsFavorited, isFavorited);
        if (isHidden != null) updateWrapper.set(Job::getIsHidden, isHidden);
        if (tagsJson != null) updateWrapper.set(Job::getTags, tagsJson);

        return jobMapper.update(null, updateWrapper);
    }

    /**
     * 批量删除职位
     *
     * @param ids 待删除的职位主键列表
     * @return 删除的条数
     */
    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return jobMapper.deleteBatchIds(ids);
    }

    /**
     * 获取当前系统激活的 AI 模型连接配置
     */
    private Map<String, String> getActiveAiProfile() {
        Map<String, Object> settings = aiSettingsService.getSettings();
        String activeId = (String) settings.get("activeProfileId");
        List<Map<String, String>> profiles = (List<Map<String, String>>) settings.get("profiles");

        if (profiles != null) {
            for (Map<String, String> p : profiles) {
                if (Objects.equals(p.get("id"), activeId)) {
                    return p;
                }
            }
        }
        throw new BusinessException(ResultCode.AI_CONFIG_MISSING, "未找到激活的 AI Profile 配置");
    }

    /**
     * 调用大模型对指定岗位进行简历匹配度智能诊断（使用 LlmResponseParser 鲁棒解析）
     *
     * @param jobId 职位 ID
     * @return 包含诊断结果实体的 Map
     */
    @Override
    public Map<String, Object> analyzeJob(String jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        AiJobResult result = aiJobResultMapper.selectById(jobId);
        if (result == null) {
            result = new AiJobResult();
            result.setJobId(jobId);
            result.setCreatedAt(LocalDateTime.now());
        }

        try {
            Map<String, String> aiConfig = getActiveAiProfile();
            String resume = resumeService.getResumeText();

            String systemPrompt = "You are a professional HR and technical interviewer. Analyze the job description against the user's resume. Return a JSON with score (0-100), matchLevel (A, B, C, D), and resultText.";
            String userPrompt = "Job Info: " + job.getTitle() + " - " + job.getRawData() + "\nResume: " + resume;

            String aiResponse = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));

            // 使用 LlmResponseParser 鲁棒解析（支持 markdown 剥离、裸 JSON 匹配与语义正则兜底）
            LlmResponseParser.DiagnosisResult parsed = LlmResponseParser.parseDiagnosis(aiResponse, objectMapper);
            result.setScore(parsed.score());
            result.setMatchLevel(parsed.matchLevel());
            result.setResultText(parsed.resultText());
            result.setUpdatedAt(LocalDateTime.now());

            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }

        } catch (Exception e) {
            log.error("AI Analysis failed for jobId: {}", jobId, e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI Analysis failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }

    /**
     * 调用大模型为指定岗位生成针对性的招聘平台打招呼话术（使用 LlmResponseParser 纯净化文本）
     *
     * @param jobId 职位 ID
     * @return 包含生成结果实体的 Map
     */
    @Override
    public Map<String, Object> generateGreeting(String jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        AiJobResult result = aiJobResultMapper.selectById(jobId);
        if (result == null) {
            result = new AiJobResult();
            result.setJobId(jobId);
            result.setCreatedAt(LocalDateTime.now());
        }

        try {
            Map<String, String> aiConfig = getActiveAiProfile();
            String resume = resumeService.getResumeText();

            String systemPrompt = "You are helping a candidate write a short, professional greeting message to a recruiter on a job platform. Keep it under 50 words.";
            String userPrompt = "Job: " + job.getTitle() + "\nResume: " + resume;

            String rawIntro = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));
            String intro = LlmResponseParser.cleanText(rawIntro);

            result.setIntro(intro);
            result.setUpdatedAt(LocalDateTime.now());

            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }
        } catch (Exception e) {
            log.error("AI Greeting failed for jobId: {}", jobId, e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI Greeting failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }

    /**
     * 调用大模型结合职位 JD 与候选人简历预测 3-5 道深度面试题（使用 LlmResponseParser 纯净化文本）
     *
     * @param jobId 职位 ID
     * @return 包含生成预测题目的 Map
     */
    @Override
    public Map<String, Object> predictQuestions(String jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        AiJobResult result = aiJobResultMapper.selectById(jobId);
        if (result == null) {
            result = new AiJobResult();
            result.setJobId(jobId);
            result.setCreatedAt(LocalDateTime.now());
        }

        try {
            Map<String, String> aiConfig = getActiveAiProfile();
            String resume = resumeService.getResumeText();

            String systemPrompt = "You are an expert interviewer. Based on the job description and the candidate's resume, predict 3-5 technical or behavioral questions that are likely to be asked.";
            String userPrompt = "Job: " + job.getTitle() + " - " + job.getRawData() + "\nResume: " + resume;

            String rawQuestions = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));
            String predictedQuestions = LlmResponseParser.cleanText(rawQuestions);

            result.setPredictedQuestions(predictedQuestions);
            result.setUpdatedAt(LocalDateTime.now());

            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }
        } catch (Exception e) {
            log.error("AI Question Prediction failed for jobId: {}", jobId, e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "AI Question Prediction failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }

    /**
     * 职位与多版本简历深度对齐差距分析 (Gap Analysis)
     */
    @Override
    public Map<String, Object> gapAnalysis(String jobId, String resumeProfileId) {
        Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId).or().eq(Job::getId, jobId));
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);

        // 1. 获取简历内容 (优先 resumeProfileId -> 默认简历画像 -> AiSettings)
        String resumeContent = "";
        String profileTitle = "默认简历";

        if (org.springframework.util.StringUtils.hasText(resumeProfileId)) {
            ResumeProfile profile = resumeProfileMapper.selectById(resumeProfileId);
            if (profile != null && org.springframework.util.StringUtils.hasText(profile.getContent())) {
                resumeContent = profile.getContent();
                profileTitle = profile.getTitle();
            }
        }

        if (!org.springframework.util.StringUtils.hasText(resumeContent)) {
            ResumeProfile defaultProfile = resumeProfileMapper.selectOne(
                    new LambdaQueryWrapper<ResumeProfile>().eq(ResumeProfile::getIsDefault, true).last("LIMIT 1"));
            if (defaultProfile != null && org.springframework.util.StringUtils.hasText(defaultProfile.getContent())) {
                resumeContent = defaultProfile.getContent();
                profileTitle = defaultProfile.getTitle();
            }
        }

        if (!org.springframework.util.StringUtils.hasText(resumeContent)) {
            resumeContent = resumeService.getResumeText();
        }

        if (!org.springframework.util.StringUtils.hasText(resumeContent)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "未找到简历内容，请先在简历管理中添加简历");
        }

        // 2. 提取岗位描述 (优先 Job.jobDesc -> JobDetail -> rawData)
        String jobDescription = job.getJobDesc();
        if (!org.springframework.util.StringUtils.hasText(jobDescription)) {
            JobDetail detail = jobDetailMapper.selectOne(
                    new LambdaQueryWrapper<JobDetail>().eq(JobDetail::getJobId, job.getJobId()).last("LIMIT 1"));
            if (detail != null && org.springframework.util.StringUtils.hasText(detail.getRawData())) {
                try {
                    JsonNode detailNode = objectMapper.readTree(detail.getRawData());
                    jobDescription = extractJobDescription(detailNode, job.getPlatform());
                } catch (Exception ignored) {}
            }
        }
        if (!org.springframework.util.StringUtils.hasText(jobDescription) && org.springframework.util.StringUtils.hasText(job.getRawData())) {
            try {
                JsonNode rawNode = objectMapper.readTree(job.getRawData());
                jobDescription = extractJobDescription(rawNode, job.getPlatform());
            } catch (Exception ignored) {}
        }
        if (!org.springframework.util.StringUtils.hasText(jobDescription)) {
            jobDescription = job.getTitle();
        }

        String systemPrompt = """
            你是一位资深技术专家和严格的招聘面试官。请对比候选人简历与目标岗位JD，进行客观、深度的技能与经验差距分析(Gap Analysis)。
            请务必返回合法的 JSON 对象，不要包含 markdown 代码块包裹，也不要有任何其他文字说明。

            输出 JSON 格式要求：
            {
              "matchScore": 80,
              "matchLevel": "A",
              "summary": "100字以内的总体契合度评估，指出核心胜任力与主要风险点",
              "matchedSkills": [
                { "skill": "技能/经验项", "detail": "简历中的契合证明与亮点说明", "level": "高度匹配" }
              ],
              "missingSkills": [
                { "skill": "缺失或薄弱技能", "detail": "JD中的具体要求与当前简历的差距", "impact": "高/中/低" }
              ],
              "interviewRisks": [
                "面试官可能会深入挑刺追问的技术点或疑点1",
                "技术点或疑点2"
              ],
              "tailoredAdvice": [
                "针对该岗位，简历具体应如何修改或突出亮点的建议1",
                "建议2",
                "建议3"
              ]
            }
            """;

        String userPrompt = String.format("""
            === 目标岗位 JD ===
            职位名称：%s
            公司名称：%s
            薪资待遇：%s
            工作地点：%s
            岗位描述与技能要求：
            %s

            === 候选人简历 [版本: %s] ===
            %s
            """,
            job.getTitle(), job.getCompanyName(), job.getSalary(), job.getLocation(),
            jobDescription, profileTitle, resumeContent);

        try {
            Map<String, String> aiConfig = getActiveAiProfile();
            String aiResponse = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));

            Map<String, Object> parsed = parseGapAnalysisJson(aiResponse);
            parsed.put("profileTitle", profileTitle);
            parsed.put("analyzedAt", LocalDateTime.now().toString());

            Map<String, Object> res = new HashMap<>();
            res.put("data", parsed);
            return res;
        } catch (Exception e) {
            log.error("AI Gap Analysis failed for jobId: {}", jobId, e);
            throw new BusinessException(ResultCode.AI_SERVICE_ERROR, "JD 深度对齐差距分析失败: " + e.getMessage());
        }
    }

    private Map<String, Object> parseGapAnalysisJson(String raw) {
        if (!org.springframework.util.StringUtils.hasText(raw)) {
            return defaultGapResult("AI 响应为空");
        }
        try {
            String json = raw.trim();
            if (json.startsWith("```json")) {
                json = json.substring(7);
            } else if (json.startsWith("```")) {
                json = json.substring(3);
            }
            if (json.endsWith("```")) {
                json = json.substring(0, json.length() - 3);
            }
            json = json.trim();

            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start >= 0 && end > start) {
                json = json.substring(start, end + 1);
            }
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse gap analysis JSON from AI: {}", raw, e);
            return defaultGapResult(raw.length() > 200 ? raw.substring(0, 200) : raw);
        }
    }

    private Map<String, Object> defaultGapResult(String summary) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("matchScore", 70);
        fallback.put("matchLevel", "B");
        fallback.put("summary", summary);
        fallback.put("matchedSkills", Collections.emptyList());
        fallback.put("missingSkills", Collections.emptyList());
        fallback.put("interviewRisks", Collections.emptyList());
        fallback.put("tailoredAdvice", Collections.emptyList());
        return fallback;
    }

    private String extractJobDescription(JsonNode node, String platform) {
        if (node == null) return "";
        String p = platform != null ? platform.toLowerCase() : "";
        if (p.contains("boss")) {
            String desc = node.path("jobDetail").path("zpData").path("jobInfo").path("postDescription").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
            desc = node.path("zpData").path("jobInfo").path("postDescription").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
        } else if (p.contains("猎聘") || p.contains("liepin")) {
            String desc = node.path("jobDetailJson").path("description").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
            desc = node.path("job").path("description").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
        } else if (p.contains("智联") || p.contains("zhilian")) {
            String desc = node.path("jobDetailData").path("position").path("desc").path("description").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
            desc = node.path("detailedPosition").path("description").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
        } else if (p.contains("51job") || p.contains("前程")) {
            String desc = node.path("jobDescribe").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
            desc = node.path("raw_detail_json").path("detailJobInfo").path("jobDescribe").asText();
            if (org.springframework.util.StringUtils.hasText(desc)) return desc;
        }
        String desc = node.path("jobDesc").asText();
        if (org.springframework.util.StringUtils.hasText(desc)) return desc;
        desc = node.path("jobDescription").asText();
        return desc != null ? desc : "";
    }
}
