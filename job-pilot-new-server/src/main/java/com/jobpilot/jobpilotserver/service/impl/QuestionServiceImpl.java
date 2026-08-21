package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobpilot.jobpilotserver.mapper.StandardQuestionMapper;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;
import com.jobpilot.jobpilotserver.service.AiSettingsService;
import com.jobpilot.jobpilotserver.service.QuestionService;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试题库业务实现类
 * <p>
 * 负责维护标准化面试题库的增删改查、多维度条件搜索，
 * 并提供基于大模型 (LLM) 为面试题生成专业、结构化参考答案的功能。
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<StandardQuestionMapper, StandardQuestion> implements QuestionService {

    private final StandardQuestionMapper questionMapper;
    private final AiSettingsService aiSettingsService;
    private final LlmClient llmClient;

    public QuestionServiceImpl(StandardQuestionMapper questionMapper,
                               AiSettingsService aiSettingsService,
                               LlmClient llmClient) {
        this.questionMapper = questionMapper;
        this.aiSettingsService = aiSettingsService;
        this.llmClient = llmClient;
    }

    /**
     * 分页多条件查询面试题库
     *
     * @param page 页码
     * @param pageSize 每页条数
     * @param keyword 搜索关键字（支持匹配题目、通用标签、分类标签）
     * @param domain 专业领域分类（如：Java、前端、架构、算法等，"全部"表示不限）
     * @return 包含 data (题目列表)、total (总数)、hasMore (是否有下一页) 的 Map
     */
    @Override
    public Map<String, Object> getQuestions(int page, int pageSize, String keyword, String domain) {
        Page<StandardQuestion> pageParam = new Page<>(page, pageSize);
        QueryWrapper<StandardQuestion> wrapper = new QueryWrapper<>();

        // 关键词模糊查询（跨标题和标签）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("title", keyword).or().like("tags", keyword).or().like("categoryTags", keyword));
        }
        // 领域维度精准筛选
        if (domain != null && !domain.trim().isEmpty() && !"全部".equals(domain)) {
            wrapper.eq("domain", domain);
        }

        // 按创建时间倒序排序
        wrapper.orderByDesc("createdAt");

        Page<StandardQuestion> resultPage = questionMapper.selectPage(pageParam, wrapper);

        Map<String, Object> res = new HashMap<>();
        res.put("data", resultPage.getRecords());
        res.put("total", resultPage.getTotal());
        res.put("hasMore", resultPage.getCurrent() < resultPage.getPages());
        return res;
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
     * 删除指定面试题
     *
     * @param id 题目 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteQuestion(String id) {
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
}