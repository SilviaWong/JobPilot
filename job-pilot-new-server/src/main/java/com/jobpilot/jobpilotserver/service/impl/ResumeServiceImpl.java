package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobpilot.jobpilotserver.mapper.ResumeMapper;
import com.jobpilot.jobpilotserver.model.entity.Resume;
import com.jobpilot.jobpilotserver.service.ResumeService;
import com.jobpilot.jobpilotserver.service.ResumeProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人简历管理业务实现类
 * 负责维护求职者的个人履历信息，为 AI 职位匹配诊断、打招呼语生成及面试题预测提供数据源。
 */
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final ResumeProfileService resumeProfileService;

    public ResumeServiceImpl(ResumeMapper resumeMapper, ResumeProfileService resumeProfileService) {
        this.resumeMapper = resumeMapper;
        this.resumeProfileService = resumeProfileService;
    }

    /**
     * 获取求职者个人简历信息
     * 若数据库暂无记录，则优先从多版本简历画像或 AiSettings 读取。
     */
    @Override
    public Map<String, Object> getResume() {
        String content = resumeProfileService.getDefaultResumeContent();
        Map<String, Object> res = new HashMap<>();
        res.put("id", "default");
        res.put("resume", content != null ? content : "");
        res.put("createdAt", LocalDateTime.now());
        res.put("updatedAt", LocalDateTime.now());
        return res;
    }

    /**
     * 保存或更新个人简历
     *
     * @param body 请求参数，包含 resume
     */
    @Override
    public Map<String, Object> updateResume(Map<String, Object> body) {
        String content = body != null ? (String) body.get("resume") : "";
        if (content == null) content = "";

        // 更新多版本简历中的默认简历
        List<Map<String, Object>> profiles = resumeProfileService.getProfiles();
        if (profiles.isEmpty()) {
            Map<String, Object> createBody = new HashMap<>();
            createBody.put("title", "通用主简历 (默认)");
            createBody.put("targetRole", "Java开发工程师");
            createBody.put("content", content);
            createBody.put("isDefault", true);
            resumeProfileService.createProfile(createBody);
        } else {
            String defaultId = (String) profiles.get(0).get("id");
            Map<String, Object> updateBody = new HashMap<>();
            updateBody.put("content", content);
            resumeProfileService.updateProfile(defaultId, updateBody);
        }

        try {
            Resume resume = resumeMapper.selectById("default");
            if (resume == null) {
                resume = new Resume();
                resume.setId("default");
                resume.setResume(content);
                resume.setCreatedAt(LocalDateTime.now());
                resume.setUpdatedAt(LocalDateTime.now());
                resumeMapper.insert(resume);
            } else {
                resume.setResume(content);
                resume.setUpdatedAt(LocalDateTime.now());
                resumeMapper.updateById(resume);
            }
        } catch (Exception ignored) {}

        return getResume();
    }

    /**
     * 获取纯文本简历内容
     *
     * @return 简历字符串，若无则返回空串
     */
    @Override
    public String getResumeText() {
        String content = resumeProfileService.getDefaultResumeContent();
        return content != null ? content : "";
    }
}
