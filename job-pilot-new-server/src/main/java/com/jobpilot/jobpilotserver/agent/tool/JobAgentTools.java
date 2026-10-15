package com.jobpilot.jobpilotserver.agent.tool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.agent.model.AgentContext;
import com.jobpilot.jobpilotserver.mapper.*;
import com.jobpilot.jobpilotserver.model.entity.*;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import com.jobpilot.jobpilotserver.util.AiResponseParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 求职智能体 (Job Copilot) 9大内置本地业务与 AI 工具集合
 */
@Slf4j
@Component
public class JobAgentTools {

    private final JobMapper jobMapper;
    private final JobDetailMapper jobDetailMapper;
    private final AiJobResultMapper aiJobResultMapper;
    private final JobStatusLogMapper jobStatusLogMapper;
    private final CompanyMapper companyMapper;
    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final InterviewMapper interviewMapper;
    private final StandardQuestionMapper standardQuestionMapper;
    private final QuestionVariantMapper questionVariantMapper;
    private final ResumeProfileMapper resumeProfileMapper;
    private final AiSettingsMapper aiSettingsMapper;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    private static final String AI_SCORING_PROMPT = """
你现在是一位严格的招聘评估专家。请对比求职者简历与职位JD，严格按照以下维度评分(0-100分)：
- 技能匹配度(25分)：核心技能完全匹配得25分，部分匹配按比例得分
- 经验相关性(25分)：工作年限符合且行业经验相关得25分，缺一项扣分
- 教育背景(20分)：学历达标得10分，专业相关得10分
- 成就与项目(20分)：有相关成功项目经验得高分，无相关项目得低分
- 软技能与文化(10分)：软技能与公司文化匹配度

无论评分结果如何，你必须列出至少3个简历与职位不匹配的地方。如果找不到明显不匹配点，也要指出潜在风险点。

###注意：一般情况下，大多数候选人应该落在60-80分区间，请谨慎给出80分以上的高分。###

在完成评分后，你需要把五项评分结果以列表形式列出：[25,25,20,20,10]
###不必输出总分###

评分后，请给出3个具体改进建议，帮助候选人提高与该职位的匹配度。

# 输出格式要求
1、五维岗位匹配度分数及评分理由
2、五维评分结果列表展示：[25,25,20,20,10]
3、3个具体改进建议
""";

    private static final String AI_GREETING_PROMPT = """
你现在是一位求职专家。请根据提供的【候选人简历】和【目标岗位 JD】，为候选人写一段用于 Boss直聘 等招聘软件的打招呼语/自我介绍。
要求：
1. 语言简练，直击痛点，重点突出候选人与岗位最匹配的核心优势，篇幅控制在 100-200 字左右。
2. 语气真诚、自信，表现出对岗位的强烈意愿，礼貌得体。
3. 直接输出打招呼语的正文，不需要任何多余的解释或开头语。
""";

    public record AiProfileConfig(String id, String name, String url, String key, String model) {}

    public JobAgentTools(JobMapper jobMapper,
                         JobDetailMapper jobDetailMapper,
                         AiJobResultMapper aiJobResultMapper,
                         JobStatusLogMapper jobStatusLogMapper,
                         CompanyMapper companyMapper,
                         BlacklistedCompanyMapper blacklistedCompanyMapper,
                         InterviewMapper interviewMapper,
                         StandardQuestionMapper standardQuestionMapper,
                         QuestionVariantMapper questionVariantMapper,
                         ResumeProfileMapper resumeProfileMapper,
                         AiSettingsMapper aiSettingsMapper,
                         LlmClient llmClient,
                         ObjectMapper objectMapper) {
        this.jobMapper = jobMapper;
        this.jobDetailMapper = jobDetailMapper;
        this.aiJobResultMapper = aiJobResultMapper;
        this.jobStatusLogMapper = jobStatusLogMapper;
        this.companyMapper = companyMapper;
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.interviewMapper = interviewMapper;
        this.standardQuestionMapper = standardQuestionMapper;
        this.questionVariantMapper = questionVariantMapper;
        this.resumeProfileMapper = resumeProfileMapper;
        this.aiSettingsMapper = aiSettingsMapper;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取所有可用工具列表
     */
    public List<AgentTool> getAllTools() {
        return List.of(
                createSearchJobsTool(),
                createGetJobDetailTool(),
                createAutoDiagnoseJobTool(),
                createGenerateGreetingTool(),
                createUpdateJobStatusTool(),
                createBatchFilterAndRecommendTool(),
                createGetFunnelSummaryTool(),
                createSearchInterviewQuestionsTool(),
                createGetMyResumesTool()
        );
    }

    public AgentTool getTool(String name) {
        return getAllTools().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public String getResumeContent(AgentContext context, String resumeProfileId) {
        if (resumeProfileId != null && !resumeProfileId.trim().isEmpty()) {
            ResumeProfile p = resumeProfileMapper.selectById(resumeProfileId);
            if (p != null && p.getContent() != null && !p.getContent().trim().isEmpty()) {
                return p.getContent();
            }
        }
        if (context != null && context.getResumes() != null) {
            for (ResumeProfile p : context.getResumes()) {
                if (resumeProfileId != null && resumeProfileId.equals(p.getId()) && p.getContent() != null && !p.getContent().trim().isEmpty()) {
                    return p.getContent();
                }
            }
        }
        ResumeProfile def = resumeProfileMapper.selectOne(
                new LambdaQueryWrapper<ResumeProfile>().eq(ResumeProfile::getIsDefault, true).last("LIMIT 1")
        );
        if (def != null && def.getContent() != null && !def.getContent().trim().isEmpty()) {
            return def.getContent();
        }
        if (context != null && context.getDefaultResume() != null && !context.getDefaultResume().trim().isEmpty()) {
            return context.getDefaultResume();
        }
        AiSettings settings = aiSettingsMapper.selectById("default");
        if (settings != null && settings.getResume() != null && !settings.getResume().trim().isEmpty()) {
            return settings.getResume();
        }
        return "";
    }

    public AiProfileConfig getActiveProfileConfig() {
        AiSettings settings = aiSettingsMapper.selectById("default");
        if (settings == null || settings.getActiveProfileId() == null || settings.getProfiles() == null) {
            throw new RuntimeException("系统尚未配置大模型，请在「大模型管理」中完成 AI 模型配置");
        }
        try {
            JsonNode profiles = objectMapper.readTree(settings.getProfiles());
            String activeId = settings.getActiveProfileId();
            if (profiles.isArray()) {
                for (JsonNode p : profiles) {
                    if (activeId.equals(p.path("id").asText())) {
                        String url = p.path("url").asText();
                        String key = p.path("key").asText();
                        String model = p.path("model").asText();
                        String name = p.path("name").asText();
                        if (key == null || key.trim().isEmpty()) {
                            throw new RuntimeException("当前激活的 AI 模型缺少 API Key，请检查配置");
                        }
                        return new AiProfileConfig(activeId, name, url, key, model);
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException re) throw re;
            throw new RuntimeException("解析 AI 模型配置失败: " + e.getMessage());
        }
        throw new RuntimeException("未找到当前激活的模型配置 (id: " + settings.getActiveProfileId() + ")");
    }

    public String getJobDesc(Job job) {
        if (job == null) return "";
        if (job.getJobDesc() != null && !job.getJobDesc().trim().isEmpty()) {
            return job.getJobDesc().trim();
        }
        if (job.getRawData() != null && !job.getRawData().trim().isEmpty()) {
            try {
                JsonNode raw = objectMapper.readTree(job.getRawData());
                if (raw.has("职位描述") && !raw.get("职位描述").asText().trim().isEmpty()) {
                    return raw.get("职位描述").asText().trim();
                }
                if (raw.has("jobDescription") && !raw.get("jobDescription").asText().trim().isEmpty()) {
                    return raw.get("jobDescription").asText().trim();
                }
                if (raw.has("desc") && !raw.get("desc").asText().trim().isEmpty()) {
                    return raw.get("desc").asText().trim();
                }
            } catch (Exception ignored) {}
        }
        JobDetail detail = jobDetailMapper.selectOne(
                new LambdaQueryWrapper<JobDetail>().eq(JobDetail::getJobId, job.getJobId()).last("LIMIT 1")
        );
        if (detail != null && detail.getRawData() != null && !detail.getRawData().trim().isEmpty()) {
            try {
                JsonNode raw = objectMapper.readTree(detail.getRawData());
                if (raw.has("职位描述") && !raw.get("职位描述").asText().trim().isEmpty()) {
                    return raw.get("职位描述").asText().trim();
                }
                if (raw.has("jobDescription") && !raw.get("jobDescription").asText().trim().isEmpty()) {
                    return raw.get("jobDescription").asText().trim();
                }
                if (raw.has("desc") && !raw.get("desc").asText().trim().isEmpty()) {
                    return raw.get("desc").asText().trim();
                }
            } catch (Exception ignored) {}
        }
        return job.getTitle() != null ? job.getTitle() : "";
    }

    // 1. search_jobs
    private AgentTool createSearchJobsTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "search_jobs";
            }

            @Override
            public String getDescription() {
                return "多条件检索本地职位库。可按关键词、城市、薪资范围、投递跟进状态(applyStatus)、最低匹配分等搜索，返回职位列表及匹配分。";
            }

            @Override
            public Map<String, Object> getParameters() {
                Map<String, Object> props = new LinkedHashMap<>();
                props.put("keyword", Map.of("type", "string", "description", "职位名称或公司名称关键词"));
                props.put("city", Map.of("type", "string", "description", "城市名或城市代码，如 上海、杭州、北京、深圳 等"));
                props.put("minSalary", Map.of("type", "number", "description", "最低月薪(k)，如 20"));
                props.put("maxSalary", Map.of("type", "number", "description", "最高月薪(k)，如 35"));
                props.put("applyStatus", Map.of("type", "string", "description", "投递进度状态", "enum", List.of("all", "none", "to-apply", "applied", "interview-1", "interview-2", "offer", "rejected")));
                props.put("minScore", Map.of("type", "number", "description", "最低AI匹配分数（0-100）"));
                props.put("onlyDiagnosed", Map.of("type", "boolean", "description", "是否只看已有AI诊断分数的职位"));
                props.put("limit", Map.of("type", "number", "description", "返回数量上限，默认 5，最大 20"));
                return Map.of("type", "object", "properties", props);
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                int limit = 5;
                if (params != null && params.get("limit") != null) {
                    limit = Math.min(((Number) params.get("limit")).intValue(), 20);
                }
                String keyword = params != null ? (String) params.get("keyword") : null;
                String city = params != null ? (String) params.get("city") : null;
                String applyStatus = params != null ? (String) params.get("applyStatus") : null;
                Double minSalary = params != null && params.get("minSalary") != null ? ((Number) params.get("minSalary")).doubleValue() : null;
                Double maxSalary = params != null && params.get("maxSalary") != null ? ((Number) params.get("maxSalary")).doubleValue() : null;
                Integer minScore = params != null && params.get("minScore") != null ? ((Number) params.get("minScore")).intValue() : null;
                Boolean onlyDiagnosed = params != null ? (Boolean) params.get("onlyDiagnosed") : null;

                LambdaQueryWrapper<Job> qw = new LambdaQueryWrapper<>();
                qw.eq(Job::getIsHidden, false);
                qw.eq(Job::getStatus, "normal");
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String kw = keyword.trim();
                    qw.and(w -> w.like(Job::getTitle, kw).or().like(Job::getCompanyName, kw));
                }
                if (city != null && !city.trim().isEmpty() && !"all".equalsIgnoreCase(city.trim())) {
                    qw.like(Job::getCity, city.trim());
                }
                if (applyStatus != null && !applyStatus.trim().isEmpty() && !"all".equalsIgnoreCase(applyStatus.trim())) {
                    qw.eq(Job::getApplyStatus, applyStatus.trim());
                }
                if (minSalary != null) {
                    qw.ge(Job::getSalaryMax, minSalary);
                }
                if (maxSalary != null) {
                    qw.le(Job::getSalaryMin, maxSalary);
                }
                qw.orderByDesc(Job::getUpdatedAt).last("LIMIT " + (limit * 2));
                List<Job> jobs = jobMapper.selectList(qw);

                List<String> jobIds = jobs.stream().map(Job::getJobId).filter(Objects::nonNull).toList();
                Map<String, AiJobResult> aiMap = new HashMap<>();
                if (!jobIds.isEmpty()) {
                    List<AiJobResult> aiList = aiJobResultMapper.selectList(
                            new LambdaQueryWrapper<AiJobResult>().in(AiJobResult::getJobId, jobIds)
                    );
                    for (AiJobResult r : aiList) {
                        aiMap.put(r.getJobId(), r);
                    }
                }

                List<Map<String, Object>> enriched = new ArrayList<>();
                for (Job j : jobs) {
                    AiJobResult ai = aiMap.get(j.getJobId());
                    Integer score = ai != null ? ai.getScore() : null;
                    String matchLevel = ai != null ? ai.getMatchLevel() : null;
                    boolean hasIntro = ai != null && ai.getIntro() != null && !ai.getIntro().trim().isEmpty();

                    if (Boolean.TRUE.equals(onlyDiagnosed) && score == null) {
                        continue;
                    }
                    if (minScore != null && (score == null || score < minScore)) {
                        continue;
                    }

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", j.getId());
                    item.put("jobId", j.getJobId());
                    item.put("title", j.getTitle());
                    item.put("companyName", j.getCompanyName());
                    item.put("salary", j.getSalary());
                    item.put("salaryMin", j.getSalaryMin());
                    item.put("salaryMax", j.getSalaryMax());
                    item.put("location", j.getLocation());
                    item.put("city", j.getCity());
                    item.put("experience", j.getExperience());
                    item.put("education", j.getEducation());
                    item.put("platform", j.getPlatform());
                    item.put("applyStatus", j.getApplyStatus());
                    item.put("hrActiveLevel", j.getHrActiveLevel());
                    item.put("isHeadhunter", j.getIsHeadhunter());
                    item.put("updatedAt", j.getUpdatedAt());
                    item.put("score", score);
                    item.put("matchLevel", matchLevel);
                    item.put("hasIntro", hasIntro);
                    enriched.add(item);
                }

                enriched.sort((a, b) -> {
                    Integer sa = (Integer) a.get("score");
                    Integer sb = (Integer) b.get("score");
                    if (sa == null && sb == null) return 0;
                    if (sa == null) return 1;
                    if (sb == null) return -1;
                    return Integer.compare(sb, sa);
                });

                List<Map<String, Object>> resultList = enriched.stream().limit(limit).toList();
                return Map.of("totalFound", resultList.size(), "jobs", resultList);
            }
        };
    }

    // 2. get_job_detail
    private AgentTool createGetJobDetailTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "get_job_detail";
            }

            @Override
            public String getDescription() {
                return "查看指定职位的详细信息，包括岗位职责描述(JD)、公司工商背景、已有AI评估分数与打招呼语、面试记录等。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "jobId", Map.of("type", "string", "description", "职位唯一标识符 (jobId)")
                        ),
                        "required", List.of("jobId")
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                String jobId = params != null ? (String) params.get("jobId") : null;
                if (jobId == null || jobId.trim().isEmpty()) {
                    return Map.of("error", "jobId 参数不能为空");
                }
                Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId));
                if (job == null) {
                    job = jobMapper.selectById(jobId);
                }
                if (job == null) {
                    return Map.of("error", "未找到 jobId 为 " + jobId + " 的职位");
                }
                String jobDesc = getJobDesc(job);
                AiJobResult aiResult = aiJobResultMapper.selectById(job.getJobId());
                Company company = companyMapper.selectOne(
                        new LambdaQueryWrapper<Company>().eq(Company::getCompanyName, job.getCompanyName()).last("LIMIT 1")
                );
                List<Interview> interviews = interviewMapper.selectList(
                        new LambdaQueryWrapper<Interview>().eq(Interview::getJobId, job.getJobId()).orderByDesc(Interview::getTime)
                );

                Map<String, Object> res = new HashMap<>();
                res.put("jobId", job.getJobId());
                res.put("title", job.getTitle());
                res.put("companyName", job.getCompanyName());
                res.put("salary", job.getSalary());
                res.put("location", job.getLocation());
                res.put("city", job.getCity());
                res.put("experience", job.getExperience());
                res.put("education", job.getEducation());
                res.put("platform", job.getPlatform());
                res.put("hrActiveStatus", job.getHrActiveStatus());
                res.put("hrActiveLevel", job.getHrActiveLevel());
                res.put("applyStatus", job.getApplyStatus());
                res.put("jobDescription", jobDesc);
                if (company != null) {
                    res.put("companyInfo", Map.of(
                            "industry", company.getIndustry() != null ? company.getIndustry() : "",
                            "scale", company.getScale() != null ? company.getScale() : "",
                            "stage", company.getStage() != null ? company.getStage() : "",
                            "companyType", company.getCompanyType() != null ? company.getCompanyType() : "",
                            "isAgency", Boolean.TRUE.equals(company.getIsAgency())
                    ));
                }
                if (aiResult != null) {
                    Map<String, Object> aiDiag = new HashMap<>();
                    aiDiag.put("score", aiResult.getScore());
                    aiDiag.put("matchLevel", aiResult.getMatchLevel());
                    aiDiag.put("resultText", aiResult.getResultText());
                    aiDiag.put("intro", aiResult.getIntro());
                    res.put("aiDiagnosis", aiDiag);
                }
                res.put("interviews", interviews);
                return res;
            }
        };
    }

    // 3. auto_diagnose_job
    private AgentTool createAutoDiagnoseJobTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "auto_diagnose_job";
            }

            @Override
            public String getDescription() {
                return "为指定职位运行五维契合度诊断（技能匹配、经验相关、教育、成就项目、软技能），生成扣分点与改进建议，并更新系统评分。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "jobId", Map.of("type", "string", "description", "职位唯一标识符 (jobId)"),
                                "resumeProfileId", Map.of("type", "string", "description", "可选：指定的简历画像 ID"),
                                "forceRerun", Map.of("type", "boolean", "description", "如果已有诊断是否强制重新评估，默认 false")
                        ),
                        "required", List.of("jobId")
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) throws Exception {
                String jobId = params != null ? (String) params.get("jobId") : null;
                Boolean forceRerun = params != null && Boolean.TRUE.equals(params.get("forceRerun"));
                String resumeProfileId = params != null ? (String) params.get("resumeProfileId") : null;

                if (jobId == null || jobId.trim().isEmpty()) {
                    return Map.of("error", "jobId 参数不能为空");
                }

                Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId));
                if (job == null) job = jobMapper.selectById(jobId);
                if (job == null) return Map.of("error", "职位 " + jobId + " 不存在");

                AiJobResult existing = aiJobResultMapper.selectById(job.getJobId());
                if (existing != null && existing.getScore() != null && !forceRerun) {
                    return Map.of(
                            "status", "cached",
                            "jobId", job.getJobId(),
                            "title", job.getTitle(),
                            "companyName", job.getCompanyName(),
                            "score", existing.getScore(),
                            "matchLevel", existing.getMatchLevel() != null ? existing.getMatchLevel() : "B",
                            "resultText", existing.getResultText() != null ? existing.getResultText() : ""
                    );
                }

                String resumeContent = getResumeContent(context, resumeProfileId);
                if (resumeContent.isEmpty()) {
                    return Map.of("error", "未找到有效简历，请先在简历管理中添加简历");
                }
                String jobDesc = getJobDesc(job);
                AiProfileConfig profile = getActiveProfileConfig();

                String prompt = """
=== 目标岗位 JD ===
职位名称：%s
公司名称：%s
岗位要求/描述：
%s

=== 候选人简历 ===
%s
""".formatted(job.getTitle(), job.getCompanyName(), jobDesc, resumeContent);

                AiResponseParser.ExtractedAiResponse response = llmClient.callAiWithReasoning(
                        AI_SCORING_PROMPT, prompt, profile.url(), profile.key(), profile.model()
                );

                AiResponseParser.ParsedAiScore parsedScore = AiResponseParser.parseAiScore(response.content());
                String fullResultText = response.content();
                if (!response.thinking().isEmpty()) {
                    fullResultText = "<details class=\"ai-thinking-block\"><summary>💭 查看 AI 深度思考推理过程</summary>\n\n"
                            + response.thinking() + "\n\n</details>\n\n---\n\n" + response.content();
                }

                if (existing == null) {
                    existing = new AiJobResult();
                    existing.setJobId(job.getJobId());
                    existing.setScore(parsedScore.totalScore());
                    existing.setMatchLevel(parsedScore.matchLevel());
                    existing.setResultText(fullResultText);
                    existing.setCreatedAt(LocalDateTime.now());
                    existing.setUpdatedAt(LocalDateTime.now());
                    aiJobResultMapper.insert(existing);
                } else {
                    existing.setScore(parsedScore.totalScore());
                    existing.setMatchLevel(parsedScore.matchLevel());
                    existing.setResultText(fullResultText);
                    existing.setUpdatedAt(LocalDateTime.now());
                    aiJobResultMapper.updateById(existing);
                }

                return Map.of(
                        "status", "diagnosed",
                        "jobId", job.getJobId(),
                        "title", job.getTitle(),
                        "companyName", job.getCompanyName(),
                        "score", parsedScore.totalScore(),
                        "matchLevel", parsedScore.matchLevel(),
                        "resultText", response.content()
                );
            }
        };
    }

    // 4. generate_greeting
    private AgentTool createGenerateGreetingTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "generate_greeting";
            }

            @Override
            public String getDescription() {
                return "为指定职位一键定制生成直击 HR 痛点的打招呼语/自我介绍文案，并自动保存到系统中。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "jobId", Map.of("type", "string", "description", "职位唯一标识符 (jobId)"),
                                "resumeProfileId", Map.of("type", "string", "description", "可选：指定的简历画像 ID")
                        ),
                        "required", List.of("jobId")
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) throws Exception {
                String jobId = params != null ? (String) params.get("jobId") : null;
                String resumeProfileId = params != null ? (String) params.get("resumeProfileId") : null;

                if (jobId == null || jobId.trim().isEmpty()) {
                    return Map.of("error", "jobId 参数不能为空");
                }

                Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId));
                if (job == null) job = jobMapper.selectById(jobId);
                if (job == null) return Map.of("error", "职位 " + jobId + " 不存在");

                String resumeContent = getResumeContent(context, resumeProfileId);
                if (resumeContent.isEmpty()) {
                    return Map.of("error", "未找到有效简历，请先添加简历");
                }
                String jobDesc = getJobDesc(job);
                AiProfileConfig profile = getActiveProfileConfig();

                String prompt = """
=== 目标岗位 JD ===
职位名称：%s
公司名称：%s
岗位职责与要求：
%s

=== 候选人简历 ===
%s

请生成打招呼语/自我介绍：
""".formatted(job.getTitle(), job.getCompanyName(), jobDesc, resumeContent);

                String greeting = llmClient.callAI(AI_GREETING_PROMPT, prompt, profile.url(), profile.key(), profile.model()).trim();

                AiJobResult existing = aiJobResultMapper.selectById(job.getJobId());
                if (existing == null) {
                    existing = new AiJobResult();
                    existing.setJobId(job.getJobId());
                    existing.setIntro(greeting);
                    existing.setCreatedAt(LocalDateTime.now());
                    existing.setUpdatedAt(LocalDateTime.now());
                    aiJobResultMapper.insert(existing);
                } else {
                    existing.setIntro(greeting);
                    existing.setUpdatedAt(LocalDateTime.now());
                    aiJobResultMapper.updateById(existing);
                }

                Map<String, Object> res = new HashMap<>();
                res.put("jobId", job.getJobId());
                res.put("title", job.getTitle());
                res.put("companyName", job.getCompanyName());
                res.put("greeting", greeting);
                return res;
            }
        };
    }

    // 5. update_job_status
    private AgentTool createUpdateJobStatusTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "update_job_status";
            }

            @Override
            public String getDescription() {
                return "更新目标职位的投递跟踪状态（如标记为待投递、已投递、约面试等），并自动记录流转日志。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "jobId", Map.of("type", "string", "description", "职位唯一标识符 (jobId)"),
                                "toStatus", Map.of("type", "string", "description", "目标状态", "enum", List.of("none", "to-apply", "applied", "interview-1", "interview-2", "offer", "rejected")),
                                "note", Map.of("type", "string", "description", "流转备注原因")
                        ),
                        "required", List.of("jobId", "toStatus")
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                String jobId = params != null ? (String) params.get("jobId") : null;
                String toStatus = params != null ? (String) params.get("toStatus") : null;
                String note = params != null ? (String) params.get("note") : null;

                if (jobId == null || toStatus == null) {
                    return Map.of("error", "jobId 和 toStatus 均为必填参数");
                }

                Job job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId));
                if (job == null) job = jobMapper.selectById(jobId);
                if (job == null) return Map.of("error", "职位 " + jobId + " 不存在");

                String fromStatus = job.getApplyStatus() != null ? job.getApplyStatus() : "none";
                LocalDateTime now = LocalDateTime.now();

                job.setApplyStatus(toStatus);
                job.setStatusUpdatedAt(now);
                if ("applied".equalsIgnoreCase(toStatus) && job.getAppliedAt() == null) {
                    job.setAppliedAt(now);
                }
                jobMapper.updateById(job);

                JobStatusLog log = new JobStatusLog();
                log.setJobRecordId(job.getId());
                log.setFromStatus(fromStatus);
                log.setToStatus(toStatus);
                log.setNote(note != null && !note.trim().isEmpty() ? note : "由求职智能体 (Job Agent) 自动流转");
                log.setCreatedAt(now);
                jobStatusLogMapper.insert(log);

                return Map.of(
                        "success", true,
                        "jobId", job.getJobId(),
                        "title", job.getTitle(),
                        "companyName", job.getCompanyName(),
                        "fromStatus", fromStatus,
                        "toStatus", toStatus
                );
            }
        };
    }

    // 6. batch_filter_and_recommend
    private AgentTool createBatchFilterAndRecommendTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "batch_filter_and_recommend";
            }

            @Override
            public String getDescription() {
                return "一键自动化智能巡检：扫描当前未处理岗位，自动过滤黑名单公司、外包中介与30天未上线僵尸岗，对合格岗位进行匹配度排名，并可选自动移入待投递列表。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "scanCount", Map.of("type", "number", "description", "本次巡检扫描的最大岗位数，默认 10，最大 20"),
                                "minScore", Map.of("type", "number", "description", "推荐阈值分数，默认 75"),
                                "autoMoveToApply", Map.of("type", "boolean", "description", "是否将高于阈值的岗位自动设为待投递(to-apply)，默认 false")
                        )
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                int scanCount = 10;
                if (params != null && params.get("scanCount") != null) {
                    scanCount = Math.min(((Number) params.get("scanCount")).intValue(), 20);
                }
                int minScore = 75;
                if (params != null && params.get("minScore") != null) {
                    minScore = ((Number) params.get("minScore")).intValue();
                }
                boolean autoMove = params != null && Boolean.TRUE.equals(params.get("autoMoveToApply"));

                List<BlacklistedCompany> blacklisted = blacklistedCompanyMapper.selectList(null);
                Set<String> blackSet = blacklisted.stream()
                        .map(BlacklistedCompany::getCompanyName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                List<Job> candidates = jobMapper.selectList(
                        new LambdaQueryWrapper<Job>()
                                .eq(Job::getStatus, "normal")
                                .eq(Job::getIsHidden, false)
                                .eq(Job::getApplyStatus, "none")
                                .orderByDesc(Job::getUpdatedAt)
                                .last("LIMIT " + (scanCount * 2))
                );

                int zombieCount = 0;
                int blacklistCount = 0;
                int agencyCount = 0;
                List<Job> validJobs = new ArrayList<>();

                for (Job job : candidates) {
                    if (blackSet.contains(job.getCompanyName())) {
                        blacklistCount++;
                        continue;
                    }
                    if (Boolean.TRUE.equals(job.getIsHeadhunter())) {
                        agencyCount++;
                        continue;
                    }
                    if ("zombie".equalsIgnoreCase(job.getHrActiveLevel())) {
                        zombieCount++;
                        continue;
                    }
                    validJobs.add(job);
                    if (validJobs.size() >= scanCount) break;
                }

                List<String> validJobIds = validJobs.stream().map(Job::getJobId).filter(Objects::nonNull).toList();
                Map<String, AiJobResult> aiMap = new HashMap<>();
                if (!validJobIds.isEmpty()) {
                    List<AiJobResult> aiList = aiJobResultMapper.selectList(
                            new LambdaQueryWrapper<AiJobResult>().in(AiJobResult::getJobId, validJobIds)
                    );
                    for (AiJobResult r : aiList) {
                        aiMap.put(r.getJobId(), r);
                    }
                }

                List<Map<String, Object>> results = new ArrayList<>();
                int recommendedCount = 0;

                for (Job job : validJobs) {
                    AiJobResult ai = aiMap.get(job.getJobId());
                    Integer score = ai != null ? ai.getScore() : null;
                    String matchLevel = ai != null ? ai.getMatchLevel() : null;

                    boolean isRecommended = (score != null && score >= minScore);
                    if (isRecommended) recommendedCount++;

                    boolean movedToApply = false;
                    if (isRecommended && autoMove) {
                        job.setApplyStatus("to-apply");
                        job.setStatusUpdatedAt(LocalDateTime.now());
                        jobMapper.updateById(job);

                        JobStatusLog log = new JobStatusLog();
                        log.setJobRecordId(job.getId());
                        log.setFromStatus("none");
                        log.setToStatus("to-apply");
                        log.setNote("智能巡检自动推荐 (匹配分: " + score + ")");
                        log.setCreatedAt(LocalDateTime.now());
                        jobStatusLogMapper.insert(log);

                        movedToApply = true;
                    }

                    Map<String, Object> item = new HashMap<>();
                    item.put("jobId", job.getJobId());
                    item.put("title", job.getTitle());
                    item.put("companyName", job.getCompanyName());
                    item.put("salary", job.getSalary());
                    item.put("location", job.getLocation());
                    item.put("score", score);
                    item.put("matchLevel", matchLevel);
                    item.put("isRecommended", isRecommended);
                    item.put("movedToApply", movedToApply);
                    results.add(item);
                }

                Map<String, Object> filteredOut = Map.of(
                        "zombieHr", zombieCount,
                        "blacklisted", blacklistCount,
                        "agencyOrHeadhunter", agencyCount
                );

                Map<String, Object> res = new HashMap<>();
                res.put("scanned", candidates.size());
                res.put("filteredOut", filteredOut);
                res.put("retainedCount", validJobs.size());
                res.put("recommendedCount", recommendedCount);
                res.put("jobs", results);
                return res;
            }
        };
    }

    // 7. get_funnel_summary
    private AgentTool createGetFunnelSummaryTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "get_funnel_summary";
            }

            @Override
            public String getDescription() {
                return "获取当前求职看板的整体漏斗统计数据，包括总岗位数、各状态分布（待投递、已投递、约面试、已拿Offer）、AI诊断覆盖率等。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of("type", "object", "properties", Map.of());
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                long totalJobs = jobMapper.selectCount(new LambdaQueryWrapper<Job>().eq(Job::getIsHidden, false));
                long diagnosedCount = aiJobResultMapper.selectCount(null);
                long activeInterviewCount = interviewMapper.selectCount(null);

                List<Job> allNonHidden = jobMapper.selectList(
                        new LambdaQueryWrapper<Job>().eq(Job::getIsHidden, false).select(Job::getApplyStatus)
                );
                Map<String, Long> statusMap = new HashMap<>();
                statusMap.put("none", 0L);
                statusMap.put("to-apply", 0L);
                statusMap.put("applied", 0L);
                statusMap.put("interview-1", 0L);
                statusMap.put("interview-2", 0L);
                statusMap.put("offer", 0L);
                statusMap.put("rejected", 0L);

                for (Job j : allNonHidden) {
                    String st = j.getApplyStatus();
                    if (st == null || !statusMap.containsKey(st)) {
                        st = "none";
                    }
                    statusMap.put(st, statusMap.get(st) + 1);
                }

                String diagnosedRatio = totalJobs > 0 ? Math.round((double) diagnosedCount / totalJobs * 100) + "%" : "0%";

                Map<String, Object> stages = new LinkedHashMap<>();
                stages.put("待筛选 (none)", statusMap.get("none"));
                stages.put("待投递 (to-apply)", statusMap.get("to-apply"));
                stages.put("已投递 (applied)", statusMap.get("applied"));
                stages.put("一面中 (interview-1)", statusMap.get("interview-1"));
                stages.put("二面/多面中 (interview-2)", statusMap.get("interview-2"));
                stages.put("已获Offer (offer)", statusMap.get("offer"));
                stages.put("不合适/已拒绝 (rejected)", statusMap.get("rejected"));

                Map<String, Object> res = new LinkedHashMap<>();
                res.put("totalJobs", totalJobs);
                res.put("diagnosedCount", diagnosedCount);
                res.put("diagnosedRatio", diagnosedRatio);
                res.put("stages", stages);
                res.put("activeInterviews", activeInterviewCount);
                return res;
            }
        };
    }

    // 8. search_interview_questions
    private AgentTool createSearchInterviewQuestionsTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "search_interview_questions";
            }

            @Override
            public String getDescription() {
                return "在系统的标准题库和变体题库中检索与指定技能、框架或技术点相关的真题及AI标准答案。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "keyword", Map.of("type", "string", "description", "检索关键词，如 Vue、React、分布式、JVM、Redis 等"),
                                "limit", Map.of("type", "number", "description", "返回题目数量上限，默认 5")
                        ),
                        "required", List.of("keyword")
                );
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                String keyword = params != null ? (String) params.get("keyword") : null;
                int limit = 5;
                if (params != null && params.get("limit") != null) {
                    limit = ((Number) params.get("limit")).intValue();
                }

                LambdaQueryWrapper<StandardQuestion> qw = new LambdaQueryWrapper<>();
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String kw = keyword.trim();
                    qw.and(w -> w.like(StandardQuestion::getTitle, kw)
                            .or().like(StandardQuestion::getDomain, kw)
                            .or().like(StandardQuestion::getCategoryTags, kw)
                            .or().like(StandardQuestion::getCorePoints, kw));
                }
                qw.last("LIMIT " + limit);
                List<StandardQuestion> questions = standardQuestionMapper.selectList(qw);

                List<Map<String, Object>> qList = new ArrayList<>();
                for (StandardQuestion q : questions) {
                    List<QuestionVariant> variants = questionVariantMapper.selectList(
                            new LambdaQueryWrapper<QuestionVariant>().eq(QuestionVariant::getStandardQuestionId, q.getId())
                    );
                    List<String> variantTitles = variants.stream().map(QuestionVariant::getTitle).toList();

                    Map<String, Object> qMap = new HashMap<>();
                    qMap.put("id", q.getId());
                    qMap.put("title", q.getTitle());
                    qMap.put("domain", q.getDomain());
                    qMap.put("corePoints", q.getCorePoints());
                    qMap.put("answerKey", q.getAnswerKey());
                    qMap.put("aiAnswer", q.getAiAnswer());
                    qMap.put("variants", variantTitles);
                    qList.add(qMap);
                }

                return Map.of(
                        "keyword", keyword != null ? keyword : "",
                        "count", qList.size(),
                        "questions", qList
                );
            }
        };
    }

    // 9. get_my_resumes
    private AgentTool createGetMyResumesTool() {
        return new AgentTool() {
            @Override
            public String getName() {
                return "get_my_resumes";
            }

            @Override
            public String getDescription() {
                return "获取求职者在系统中维护的所有简历画像（ResumeProfile），了解其当前擅长的技术栈和求职目标。";
            }

            @Override
            public Map<String, Object> getParameters() {
                return Map.of("type", "object", "properties", Map.of());
            }

            @Override
            public Object execute(Map<String, Object> params, AgentContext context) {
                List<ResumeProfile> profiles = resumeProfileMapper.selectList(
                        new LambdaQueryWrapper<ResumeProfile>()
                                .orderByDesc(ResumeProfile::getIsDefault)
                                .orderByDesc(ResumeProfile::getUpdatedAt)
                );

                List<Map<String, Object>> pList = new ArrayList<>();
                for (ResumeProfile p : profiles) {
                    String content = p.getContent() != null ? p.getContent() : "";
                    String excerpt = content.length() > 300 ? content.substring(0, 300) + "..." : content;

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", p.getId());
                    item.put("title", p.getTitle());
                    item.put("targetRole", p.getTargetRole());
                    item.put("isDefault", Boolean.TRUE.equals(p.getIsDefault()));
                    item.put("summaryExcerpt", excerpt);
                    pList.add(item);
                }

                return Map.of(
                        "count", pList.size(),
                        "profiles", pList
                );
            }
        };
    }
}
