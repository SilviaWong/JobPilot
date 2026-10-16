package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobpilot.jobpilotserver.common.exception.BusinessException;
import com.jobpilot.jobpilotserver.common.result.ResultCode;
import com.jobpilot.jobpilotserver.mapper.StandardQuestionMapper;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;
import com.jobpilot.jobpilotserver.service.AiSettingsService;
import com.jobpilot.jobpilotserver.service.QuestionService;
import com.jobpilot.jobpilotserver.service.ResumeService;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 面试题库业务实现类
 * <p>
 * 负责维护标准化面试题库的增删改查、多维度条件搜索，
 * 并提供基于大模型 (LLM) 为面试题生成专业、结构化参考答案的功能。
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<StandardQuestionMapper, StandardQuestion> implements QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);

    private final StandardQuestionMapper questionMapper;
    private final com.jobpilot.jobpilotserver.mapper.QuestionVariantMapper questionVariantMapper;
    private final AiSettingsService aiSettingsService;
    private final ResumeService resumeService;
    private final LlmClient llmClient;
    private final Executor aiTaskExecutor;

    @org.springframework.beans.factory.annotation.Autowired
    public QuestionServiceImpl(StandardQuestionMapper questionMapper,
                               com.jobpilot.jobpilotserver.mapper.QuestionVariantMapper questionVariantMapper,
                               AiSettingsService aiSettingsService,
                               ResumeService resumeService,
                               LlmClient llmClient,
                               @Qualifier("aiTaskExecutor") Executor aiTaskExecutor) {
        this.questionMapper = questionMapper;
        this.questionVariantMapper = questionVariantMapper;
        this.aiSettingsService = aiSettingsService;
        this.resumeService = resumeService;
        this.llmClient = llmClient;
        this.aiTaskExecutor = aiTaskExecutor;
    }

    public QuestionServiceImpl(StandardQuestionMapper questionMapper,
                               AiSettingsService aiSettingsService,
                               LlmClient llmClient) {
        this(questionMapper, null, aiSettingsService, null, llmClient, null);
    }

    @Override
    public Map<String, Object> getQuestions(int page, int pageSize, String keyword, String domain) {
        return getQuestions(page, pageSize, keyword, domain, "all", null, "hotness");
    }

    /**
     * 分页多维度条件查询面试题库 (支持热度排序、梯队分层、真题变体检索及关联预查)
     *
     * @param page 页码
     * @param pageSize 每页条数
     * @param keyword 搜索关键字（跨标题、核心考点、标签、以及面试变体真题）
     * @param domain 专业领域分类（如：Java基础、Redis、并发编程等）
     * @param tier 题库梯队（T1-必考、T2-进阶、T3-长尾、all）
     * @param tag 单独标签
     * @param sortBy 排序方式（hotness 考察频次 vs createdAt 最新发布）
     * @return 包含 data (题目列表+变体)、total (总数)、hasMore、meta (梯队统计与领域分类)
     */
    @Override
    public Map<String, Object> getQuestions(int page, int pageSize, String keyword, String domain, String tier, String tag, String sortBy) {
        Page<StandardQuestion> pageParam = new Page<>(page, pageSize);
        QueryWrapper<StandardQuestion> wrapper = new QueryWrapper<>();

        // 1. 深度关键词模糊检索（匹配标准题标题、核心考点、通用标签、分类标签，以及其关联的所有变体真题）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().replace("'", "''");
            wrapper.and(w -> w.like("title", kw)
                    .or().like("tags", kw)
                    .or().like("corePoints", kw)
                    .or().like("categoryTags", kw)
                    .or().inSql("id", "SELECT standardQuestionId FROM QuestionVariant WHERE title LIKE '%" + kw + "%'"));
        }

        // 2. 技术领域维度筛选
        if (domain != null && !domain.trim().isEmpty() && !"全部".equals(domain)) {
            wrapper.eq("domain", domain.trim());
        }

        // 3. 梯队 (Tier) 与标签筛选
        if (tier != null && !"all".equalsIgnoreCase(tier) && !tier.trim().isEmpty()) {
            wrapper.like("tags", tier.trim());
        } else if (tag != null && !tag.trim().isEmpty()) {
            wrapper.like("tags", tag.trim());
        }

        // 4. 排序方式 (hotness 考察频次/变体真题数倒序 vs createdAt 最新发布倒序)
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            wrapper.orderByDesc("createdAt");
        } else {
            // 默认 hotness: 按真实面试变体真题收集数量倒序排列
            wrapper.last("ORDER BY (SELECT COUNT(1) FROM QuestionVariant WHERE standardQuestionId = StandardQuestion.id) DESC, createdAt DESC");
        }

        Page<StandardQuestion> resultPage = questionMapper.selectPage(pageParam, wrapper);
        List<StandardQuestion> records = resultPage.getRecords();

        // 5. 批量预查当前页题目的变体真题列表 (QuestionVariant)，按序号升序排列
        if (records != null && !records.isEmpty() && questionVariantMapper != null) {
            List<String> questionIds = records.stream().map(StandardQuestion::getId).collect(java.util.stream.Collectors.toList());
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.jobpilot.jobpilotserver.model.entity.QuestionVariant> variantWrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            variantWrapper.in(com.jobpilot.jobpilotserver.model.entity.QuestionVariant::getStandardQuestionId, questionIds)
                    .orderByAsc(com.jobpilot.jobpilotserver.model.entity.QuestionVariant::getSerialNo);
            List<com.jobpilot.jobpilotserver.model.entity.QuestionVariant> allVariants = questionVariantMapper.selectList(variantWrapper);
            Map<String, List<com.jobpilot.jobpilotserver.model.entity.QuestionVariant>> variantMap = allVariants.stream()
                    .collect(java.util.stream.Collectors.groupingBy(com.jobpilot.jobpilotserver.model.entity.QuestionVariant::getStandardQuestionId));
            for (StandardQuestion q : records) {
                q.setVariants(variantMap.getOrDefault(q.getId(), java.util.Collections.emptyList()));
            }
        }

        // 6. 动态汇总元数据 (用于前端选项卡统计与动态技术领域下拉选项)
        Map<String, Object> meta = computeMetaStats();

        Map<String, Object> res = new HashMap<>();
        res.put("data", records);
        res.put("total", resultPage.getTotal());
        res.put("hasMore", (page * pageSize) < resultPage.getTotal());
        res.put("meta", meta);
        return res;
    }

    /**
     * 汇总题库元数据（各梯队数量统计与技术领域去重列表）
     */
    private Map<String, Object> computeMetaStats() {
        long allCount = questionMapper.selectCount(null);
        long t1Count = questionMapper.selectCount(new QueryWrapper<StandardQuestion>().like("tags", "T1-必考"));
        long t2Count = questionMapper.selectCount(new QueryWrapper<StandardQuestion>().like("tags", "T2-进阶"));
        long t3Count = questionMapper.selectCount(new QueryWrapper<StandardQuestion>().like("tags", "T3-长尾"));

        QueryWrapper<StandardQuestion> domainQuery = new QueryWrapper<>();
        domainQuery.select("DISTINCT domain").isNotNull("domain");
        List<StandardQuestion> domainRecords = questionMapper.selectList(domainQuery);
        List<String> domains = domainRecords.stream()
                .map(StandardQuestion::getDomain)
                .filter(d -> d != null && !d.trim().isEmpty())
                .sorted(java.text.Collator.getInstance(java.util.Locale.CHINESE))
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> tierStats = new HashMap<>();
        tierStats.put("all", allCount);
        tierStats.put("t1", t1Count);
        tierStats.put("t2", t2Count);
        tierStats.put("t3", t3Count);

        Map<String, Object> meta = new HashMap<>();
        meta.put("tierStats", tierStats);
        meta.put("domains", domains);
        return meta;
    }

    /**
     * 新增面试题
     *
     * @param question 题目实体
     * @return 创建成功后的题目实体
     */
    @Override
    public StandardQuestion createQuestion(StandardQuestion question) {
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        questionMapper.insert(question);
        return question;
    }

    /**
     * 更新已有面试题
     *
     * @param id 题目 ID
     * @param question 待更新的题目信息
     * @return 更新后的题目实体
     */
    @Override
    public StandardQuestion updateQuestion(String id, StandardQuestion question) {
        question.setId(id);
        question.setUpdatedAt(LocalDateTime.now());
        questionMapper.updateById(question);
        return questionMapper.selectById(id);
    }

    /**
     * 删除指定面试题（级联删除其关联的所有变体真题）
     *
     * @param id 题目 ID
     * @return 是否删除成功
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(String id) {
        if (questionVariantMapper != null) {
            questionVariantMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.jobpilot.jobpilotserver.model.entity.QuestionVariant>()
                    .eq(com.jobpilot.jobpilotserver.model.entity.QuestionVariant::getStandardQuestionId, id));
        }
        int rows = questionMapper.deleteById(id);
        return rows > 0;
    }

    /**
     * 调用大模型智能生成面试题参考答案与核心考点解析
     *
     * @param title 面试题题目
     * @param corePoints 核心考点或提示（可选）
     * @return 大模型生成的结构化标准参考答案
     */
    @Override
    @SuppressWarnings("unchecked")
    public String generateAiAnswer(String title, String corePoints) {
        // 1. 获取全局 AI 配置及当前激活的模型 Profile
        Map<String, Object> settings = aiSettingsService.getSettings();
        List<Map<String, Object>> profiles = (List<Map<String, Object>>) settings.get("profiles");
        String activeProfileId = (String) settings.get("activeProfileId");
        
        String url = "", key = "", model = "";
        if (profiles != null) {
            for (Map<String, Object> p : profiles) {
                if (activeProfileId != null && activeProfileId.equals(p.get("id"))) {
                    url = (String) p.get("url");
                    key = (String) p.get("key");
                    model = (String) p.get("model");
                    break;
                }
            }
        }

        // 校验模型配置是否完整
        if (url == null || url.isEmpty() || key == null || key.isEmpty() || model == null || model.isEmpty()) {
            throw new RuntimeException("请先在设置中配置并启用AI模型");
        }

        // 2. 构造资深技术面试官的系统 Prompt 与用户 Prompt
        String systemPrompt = "你是一位资深的面试官和技术专家。请根据提供的面试题及核心考察点，给出一个专业、完整、结构清晰的参考答案。要求：\n" +
                "1. 开门见山，给出清晰的结论。\n" +
                "2. 结构合理，分点阐述（可使用Markdown）。\n" +
                "3. 如果适用，可以结合实际业务场景或底层原理说明。\n" +
                "4. 字数适中，不要过于简略，也不要啰嗦。";
                
        String userPrompt = "面试题：\n" + title + "\n\n核心考察点（如果有提供，请重点覆盖）：\n" + (corePoints != null ? corePoints : "无额外提示");

        // 3. 发送大模型请求
        try {
            return llmClient.callAI(systemPrompt, userPrompt, url, key, model);
        } catch (Exception e) {
            throw new RuntimeException("生成AI答案失败: " + e.getMessage());
        }
    }

    /**
     * 批量为缺失 AI 解答的面试题生成标准参考答案
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> batchAiAnswer() {
        // 1. 获取全局 AI 配置及当前激活的模型 Profile
        Map<String, Object> settings = aiSettingsService.getSettings();
        List<Map<String, Object>> profiles = (List<Map<String, Object>>) settings.get("profiles");
        String activeProfileId = (String) settings.get("activeProfileId");
        String resume = resumeService != null ? resumeService.getResumeText() : "";

        if (resume == null || resume.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请先在系统设置中完成个人简历设置");
        }

        String url = "", key = "", model = "";
        if (profiles != null) {
            for (Map<String, Object> p : profiles) {
                if (activeProfileId != null && activeProfileId.equals(p.get("id"))) {
                    url = (String) p.get("url");
                    key = (String) p.get("key");
                    model = (String) p.get("model");
                    break;
                }
            }
        }

        if (url == null || url.isEmpty() || key == null || key.isEmpty() || model == null || model.isEmpty()) {
            throw new BusinessException(ResultCode.AI_CONFIG_MISSING, "当前激活的 AI 模型配置不完整");
        }

        // 2. 查询所有目标题目（aiAnswer 为空或 null）
        List<StandardQuestion> questions = questionMapper.selectList(new QueryWrapper<StandardQuestion>()
                .isNull("aiAnswer")
                .or()
                .eq("aiAnswer", ""));

        if (questions == null || questions.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "未找到需要补充 AI 解答的题目");
        }

        final String finalUrl = url;
        final String finalKey = key;
        final String finalModel = model;
        final String finalResume = resume;

        String systemPrompt = "你现在是一位资深的面试官和技术专家。用户正在准备技术面试，请你基于提供的【面试题目】和【核心考点】，并结合用户的【个人简历】，生成一份高质量的面试答案解析。\n\n" +
                "要求：\n" +
                "1. 答案必须紧密围绕【核心考点】展开，不要偏题。\n" +
                "2. 尽可能结合候选人【个人简历】中的项目经验或技术栈，提供具有个人特色的回答思路。如果简历中完全没有相关内容，则给出通用且专业的标准回答。\n" +
                "3. 回答要条理清晰，结构化输出（可以使用 Markdown 的列表、加粗等格式）。\n" +
                "4. 语气专业、自信，直接以求职者的口吻输出回答，不要废话，也不需要做自我介绍。";

        AtomicInteger successCount = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (StandardQuestion q : questions) {
            Runnable task = () -> {
                try {
                    String userPrompt = String.format(
                            "=== 面试题目 ===\n%s\n\n=== 核心考点 ===\n%s\n\n=== 个人简历 ===\n%s\n\n请开始你的回答：",
                            q.getTitle(),
                            q.getCorePoints() != null && !q.getCorePoints().isEmpty() ? q.getCorePoints() : "无特别提示，请基于题目给出全面解答",
                            finalResume
                    );
                    String resultText = llmClient.callAI(systemPrompt, userPrompt, finalUrl, finalKey, finalModel);
                    if (resultText != null && !resultText.trim().isEmpty()) {
                        StandardQuestion updateItem = new StandardQuestion();
                        updateItem.setId(q.getId());
                        updateItem.setAiAnswer(resultText.trim());
                        updateItem.setUpdatedAt(LocalDateTime.now());
                        questionMapper.updateById(updateItem);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.error("Failed to generate AI answer for question {}: {}", q.getId(), e.getMessage());
                }
            };

            CompletableFuture<Void> future = aiTaskExecutor != null
                    ? CompletableFuture.runAsync(task, aiTaskExecutor)
                    : CompletableFuture.runAsync(task);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("Batch AI answering completed with errors: {}", e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", successCount.get());
        return result;
    }
}