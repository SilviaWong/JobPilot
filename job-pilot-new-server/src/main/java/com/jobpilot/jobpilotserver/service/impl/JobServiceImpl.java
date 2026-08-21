package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerStrategy;
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
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();

        // 1. 隐藏与状态过滤逻辑
        if (Boolean.TRUE.equals(filterShowHidden)) {
            // 查看回收站/隐藏职位：展示主动隐藏的或已过期的职位
            queryWrapper.and(w -> w.eq("is_hidden", true).or().eq("status", "expired"));
        } else if ("all".equalsIgnoreCase(status)) {
            // 查看全部，不加状态限制
        } else {
            // 默认只展示未隐藏且状态正常的职位
            queryWrapper.and(w -> w.eq("is_hidden", false).or().isNull("is_hidden"))
                        .eq("status", status != null ? status : "normal");
        }

        // 2. 仅看收藏过滤
        if (Boolean.TRUE.equals(filterFavoritesOnly)) {
            queryWrapper.eq("is_favorited", true);
        }

        // 3. 来源招聘平台过滤
        if (platform != null && !"all".equalsIgnoreCase(platform) && !platform.isEmpty()) {
            queryWrapper.eq("platform", platform);
        }

        // 4. 学历门槛过滤
        if (education != null && !"all".equalsIgnoreCase(education) && !education.isEmpty()) {
            queryWrapper.like("education", education);
        }

        // 5. 职位名称 / 公司名称关键词模糊检索
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.and(wrapper ->
                wrapper.like("title", keyword).or().like("company_name", keyword)
            );
        }

        // 6. 黑名单过滤 (利用 Caffeine 本地缓存，避免频繁全表扫描 blacklisted_company)
        Set<String> blacklistedNames = blacklistedCompanyCache.get("all_blacklisted", k -> {
            log.debug("Loading blacklisted company names into Caffeine cache...");
            return blacklistedCompanyMapper.selectList(null).stream()
                    .map(BlacklistedCompany::getCompanyName)
                    .collect(Collectors.toSet());
        });

        if (Boolean.FALSE.equals(filterShowBlacklisted) || filterShowBlacklisted == null) {
            if (blacklistedNames != null && !blacklistedNames.isEmpty()) {
                queryWrapper.notIn("company_name", blacklistedNames);
            }
        }

        // 按更新时间倒序排序
        queryWrapper.orderByDesc("updated_at");

        // 7. 执行分页查询
        Page<Job> jobPage = new Page<>(page, pageSize);
        Page<Job> resultPage = jobMapper.selectPage(jobPage, queryWrapper);

        List<Job> records = resultPage.getRecords();

        // 提取当前页所有 JobId 集合
        List<String> jobIds = records.stream().map(Job::getJobId).collect(Collectors.toList());

        // 8. 批量预查 AI 诊断结果 (批量 in 查询，O(1) 内存 Map 映射)
        Map<String, AiJobResult> aiResultMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            QueryWrapper<AiJobResult> aiWrapper = new QueryWrapper<>();
            aiWrapper.in("job_id", jobIds);
            aiJobResultMapper.selectList(aiWrapper).forEach(r -> aiResultMap.put(r.getJobId(), r));
        }

        // 9. 批量预查跨平台职位详情 JobDetail
        Map<String, JobDetail> jobDetailMap = new HashMap<>();
        if (!jobIds.isEmpty()) {
            QueryWrapper<JobDetail> detailWrapper = new QueryWrapper<>();
            detailWrapper.in("job_id", jobIds);
            jobDetailMapper.selectList(detailWrapper).forEach(d -> jobDetailMap.put(d.getPlatform() + "_" + d.getJobId(), d));
        }

        // 10. 批量预查企业详细信息 Company
        Map<String, Company> companyMap = new HashMap<>();
        if (!records.isEmpty()) {
            QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
            List<String> compNames = records.stream().map(Job::getCompanyName).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            if (!compNames.isEmpty()) {
                companyQueryWrapper.in("company_name", compNames);
                companyMapper.selectList(companyQueryWrapper).forEach(c -> companyMap.put(c.getSourcePlatform() + "_" + c.getCompanyName(), c));
            }
        }

        // 11. 遍历结果集，执行数据归一化与 VO 组装
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

        // 12. AI 诊断状态过滤（全部/已诊断/未诊断）
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
        Job job = jobMapper.selectById(id);
        if (job == null) throw new BusinessException(ResultCode.JOB_NOT_FOUND);
        job.setStatus(status);
        job.setUpdatedAt(LocalDateTime.now());
        jobMapper.updateById(job);
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
    @Override
    public int batchUpdate(List<String> ids, Boolean isFavorited, Boolean isHidden, List<String> tags) {
        if (ids == null || ids.isEmpty()) return 0;
        int count = 0;
        String tagsJson = null;
        if (tags != null) {
            try {
                tagsJson = objectMapper.writeValueAsString(tags);
            } catch (Exception ignored) {}
        }
        for (String id : ids) {
            Job job = jobMapper.selectById(id);
            if (job != null) {
                if (isFavorited != null) job.setIsFavorited(isFavorited);
                if (isHidden != null) job.setIsHidden(isHidden);
                if (tagsJson != null) job.setTags(tagsJson);
                job.setUpdatedAt(LocalDateTime.now());
                jobMapper.updateById(job);
                count++;
            }
        }
        return count;
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
     * 调用大模型对指定岗位进行简历匹配度智能诊断
     * <p>
     * 流程：
     * 1. 查询职位 JD 和求职者简历；
     * 2. 调用大模型分析匹配度，要求返回 JSON 包含 score(0-100), matchLevel(A/B/C), resultText；
     * 3. 解析模型响应并持久化到 ai_job_result 表。
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

            String systemPrompt = "You are a professional HR and technical interviewer. Analyze the job description against the user's resume. Return a JSON with score (0-100), matchLevel (A, B, C), and resultText.";
            String userPrompt = "Job Info: " + job.getTitle() + " - " + job.getRawData() + "\nResume: " + resume;

            String aiResponse = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));

            // 尝试解析大模型返回的 JSON 结构
            try {
                if (aiResponse.contains("```json")) {
                    aiResponse = aiResponse.substring(aiResponse.indexOf("```json") + 7);
                    if (aiResponse.contains("```")) {
                        aiResponse = aiResponse.substring(0, aiResponse.indexOf("```"));
                    }
                }
                JsonNode node = objectMapper.readTree(aiResponse.trim());
                if (node.has("score")) result.setScore(node.get("score").asInt());
                if (node.has("matchLevel")) result.setMatchLevel(node.get("matchLevel").asText());
                if (node.has("resultText")) result.setResultText(node.get("resultText").asText());
            } catch (Exception e) {
                // 若未按照标准 JSON 返回，则将全部响应作为诊断建议，并赋予默认评分
                result.setResultText(aiResponse);
                result.setScore(80);
                result.setMatchLevel("B");
            }

            result.setUpdatedAt(LocalDateTime.now());
            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }

        } catch (Exception e) {
            throw new RuntimeException("AI Analysis failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }

    /**
     * 调用大模型为指定岗位生成针对性的招聘平台打招呼话术
     *
     * @param jobId 职位 ID
     * @return 包含生成结果实体的 Map
     */
    @Override
    public Map<String, Object> generateGreeting(String jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) throw new RuntimeException("Job not found");

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

            String intro = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));

            result.setIntro(intro);
            result.setUpdatedAt(LocalDateTime.now());

            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }
        } catch (Exception e) {
            throw new RuntimeException("AI Greeting failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }

    /**
     * 调用大模型结合职位 JD 与候选人简历预测 3-5 道深度面试题
     *
     * @param jobId 职位 ID
     * @return 包含生成预测题目的 Map
     */
    @Override
    public Map<String, Object> predictQuestions(String jobId) {
        Job job = jobMapper.selectById(jobId);
        if (job == null) throw new RuntimeException("Job not found");

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

            String predictedQuestions = llmClient.callAI(systemPrompt, userPrompt, aiConfig.get("url"), aiConfig.get("key"), aiConfig.get("model"));

            result.setPredictedQuestions(predictedQuestions);
            result.setUpdatedAt(LocalDateTime.now());

            if (aiJobResultMapper.selectById(jobId) == null) {
                aiJobResultMapper.insert(result);
            } else {
                aiJobResultMapper.updateById(result);
            }
        } catch (Exception e) {
            throw new RuntimeException("AI Question Prediction failed: " + e.getMessage());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", result);
        return res;
    }
}
