package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.AiSettingsMapper;
import com.jobpilot.jobpilotserver.mapper.AiSkillMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;
import com.jobpilot.jobpilotserver.model.entity.AiSkill;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.service.AiSkillService;
import com.jobpilot.jobpilotserver.service.ResumeService;
import com.jobpilot.jobpilotserver.strategy.llm.LlmClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI 技能模板管理及执行业务实现类
 * <p>
 * 负责管理求职场景下的各种 Prompt 技能模板（如打招呼、匹配度诊断、面试题预测等），
 * 并提供动态占位符注入（{{JD_TEXT}}、{{RESUME_TEXT}}）与大模型执行调度能力。
 */
@Service
public class AiSkillServiceImpl extends ServiceImpl<AiSkillMapper, AiSkill> implements AiSkillService {

    private final AiSkillMapper skillMapper;
    private final JobMapper jobMapper;
    private final AiSettingsMapper aiSettingsMapper;
    private final ResumeService resumeService;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public AiSkillServiceImpl(AiSkillMapper skillMapper, JobMapper jobMapper, AiSettingsMapper aiSettingsMapper, ResumeService resumeService, LlmClient llmClient, ObjectMapper objectMapper) {
        this.skillMapper = skillMapper;
        this.jobMapper = jobMapper;
        this.aiSettingsMapper = aiSettingsMapper;
        this.resumeService = resumeService;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取所有可用的 AI 技能列表
     * <p>
     * 若数据库暂无技能数据（首次启动），则自动初始化系统预置的常用技能。
     *
     * @return 按创建时间升序排列的技能列表
     */
    @Override
    public List<AiSkill> getSkills() {
        List<AiSkill> skills = skillMapper.selectList(new LambdaQueryWrapper<AiSkill>().orderByAsc(AiSkill::getCreatedAt));
        // 如果当前没有任何技能，自动初始化系统预设的默认技能库
        if (skills.isEmpty()) {
            initDefaultSkills();
            skills = skillMapper.selectList(new LambdaQueryWrapper<AiSkill>().orderByAsc(AiSkill::getCreatedAt));
        }
        return skills;
    }

    /**
     * 创建自定义 AI 技能模板
     *
     * @param skill 技能实体信息
     * @return 创建成功后的实体
     */
    @Override
    public AiSkill createSkill(AiSkill skill) {
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        if (skill.isActive() == null) {
            skill.setIsActive(true);
        }
        skillMapper.insert(skill);
        return skill;
    }

    /**
     * 更新已有 AI 技能模板
     *
     * @param id 技能 ID
     * @param skill 待更新的技能内容
     * @return 更新后的技能实体，若不存在则返回 null
     */
    @Override
    public AiSkill updateSkill(String id, AiSkill skill) {
        AiSkill existing = skillMapper.selectById(id);
        if (existing != null) {
            skill.setId(id);
            skill.setUpdatedAt(LocalDateTime.now());
            skillMapper.updateById(skill);
            return skill;
        }
        return null;
    }

    /**
     * 删除指定的 AI 技能
     *
     * @param id 技能 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteSkill(String id) {
        return skillMapper.deleteById(id) > 0;
    }

    /**
     * 执行指定的 AI 技能
     * <p>
     * 核心执行流程：
     * 1. 根据 jobId 查询对应的岗位信息；
     * 2. 获取并校验系统设置中的求职者个人简历；
     * 3. 校验技能有效性与启用状态；
     * 4. 动态将岗位描述 (JD) 和个人简历替换到技能模板中的占位符 {{JD_TEXT}} 与 {{RESUME_TEXT}}；
     * 5. 获取当前激活的大模型配置并调用大模型接口执行。
     *
     * @param jobId 职位主键 ID 或业务 jobId
     * @param skillId 技能模板 ID
     * @return 大模型返回的生成结果文本
     */
    @Override
    public String executeSkill(String jobId, String skillId) {
        // 1. 查找目标岗位（先按主键查，未查到再按平台的 jobId 查）
        Job job = jobMapper.selectById(jobId);
        if (job == null) {
            job = jobMapper.selectOne(new LambdaQueryWrapper<Job>().eq(Job::getJobId, jobId));
        }
        if (job == null) {
            throw new RuntimeException("Job not found");
        }

        // 2. 获取并校验候选人简历
        String resumeText = resumeService.getResumeText();
        if (resumeText == null || resumeText.trim().isEmpty()) {
            throw new RuntimeException("请先在系统设置中配置您的个人简历");
        }

        // 3. 获取并校验技能配置
        AiSkill skill = skillMapper.selectById(skillId);
        if (skill == null) {
            throw new RuntimeException("Skill not found");
        }
        if (Boolean.FALSE.equals(skill.isActive())) {
            throw new RuntimeException("该技能已被禁用");
        }

        // 4. 提取 JD 文本与简历文本，动态替换 Prompt 占位符
        String jdText = job.getRawData() != null ? job.getRawData() : job.getTitle();

        String systemPrompt = skill.getSystemPrompt().replace("{{JD_TEXT}}", jdText).replace("{{RESUME_TEXT}}", resumeText);
        String userPrompt = skill.getUserPrompt().replace("{{JD_TEXT}}", jdText).replace("{{RESUME_TEXT}}", resumeText);

        // 5. 提取当前激活的 AI 模型连接配置并发送调用请求
        AiSettings settings = aiSettingsMapper.selectById("default");
        if (settings == null) {
            throw new RuntimeException("请先配置 AI 大模型参数");
        }
        Map<String, String> activeProfile = getActiveProfile(settings);
        try {
            return llmClient.callAI(systemPrompt, userPrompt, activeProfile.get("url"), activeProfile.get("key"), activeProfile.get("model"));
        } catch (Exception e) {
            throw new RuntimeException("执行技能异常: " + e.getMessage());
        }
    }

    /**
     * 初始化系统内置的默认求职 AI 技能库
     * 包括：打招呼开场白、职位匹配度诊断、面试题预测
     */
    private void initDefaultSkills() {
        List<AiSkill> defaultList = List.of(
                buildSkill("cover-letter", "打招呼开场白", "生成针对该岗位的 Boss 直聘打招呼开场白（50字内）",
                        "你是一个专业的求职助手。请根据下方提供的【职位描述】和【我的简历】，生成一段100字以内的 Boss直聘 开场白。要求：突出我和岗位的核心匹配点，态度自信且简练。",
                        "【职位描述】:\n{{JD_TEXT}}\n\n【我的简历】:\n{{RESUME_TEXT}}"),
                buildSkill("resume-match", "诊断匹配度", "分析当前职位与简历的匹配情况及劣势",
                        "你是一位资深技术面试官与HR。请根据【职位要求】与用户的【简历】，给出一个匹配度评分（百分制），并列出3个核心匹配优势与劣势。",
                        "【职位要求】:\n{{JD_TEXT}}\n\n【我的简历】:\n{{RESUME_TEXT}}"),
                buildSkill("interview-prep", "预测面试题", "预测该岗位可能提问的3-5个硬核面试题",
                        "你是一位严厉的资深技术面试官。预测你会向该候选人提问的 3 到 5 个最具深度的面试问题。",
                        "【职位要求】:\n{{JD_TEXT}}\n\n【我的简历】:\n{{RESUME_TEXT}}")
        );
        for (AiSkill s : defaultList) {
            skillMapper.insert(s);
        }
    }

    /**
     * 辅助构造 AiSkill 实体的便捷方法
     */
    private AiSkill buildSkill(String id, String name, String desc, String sysPrompt, String userPrompt) {
        AiSkill s = new AiSkill();
        s.setId(id);
        s.setName(name);
        s.setDescription(desc);
        s.setSystemPrompt(sysPrompt);
        s.setUserPrompt(userPrompt);
        s.setIsActive(true);
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        return s;
    }

    /**
     * 解析 AI 设置 JSON，提取当前激活模型的连接配置 (url, key, model)
     *
     * @param settings AI 设置实体
     * @return 包含 url、key、model 的 Map 映射
     */
    private Map<String, String> getActiveProfile(AiSettings settings) {
        try {
            JsonNode profiles = objectMapper.readTree(settings.getProfiles());
            String activeId = settings.getActiveProfileId();
            if (profiles.isArray()) {
                for (JsonNode p : profiles) {
                    if (activeId.equals(p.path("id").asText())) {
                        Map<String, String> res = new HashMap<>();
                        res.put("url", p.path("url").asText());
                        res.put("key", p.path("key").asText());
                        res.put("model", p.path("model").asText());
                        return res;
                    }
                }
            }
        } catch (Exception e) {
            // 解析异常
        }
        throw new RuntimeException("当前激活的 AI 模型配置不完整");
    }
}