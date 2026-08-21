package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobpilot.jobpilotserver.mapper.ResumeMapper;
import com.jobpilot.jobpilotserver.model.entity.Resume;
import com.jobpilot.jobpilotserver.service.ResumeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人简历管理业务实现类
 * 负责维护求职者的个人履历信息，为 AI 职位匹配诊断、打招呼语生成及面试题预测提供数据源。
 */
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    private final ResumeMapper resumeMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper) {
        this.resumeMapper = resumeMapper;
    }

    /**
     * 获取求职者个人简历信息
     * 若数据库暂无记录，则初始化一条空简历默认记录。
     */
    @Override
    public Map<String, Object> getResume() {
        Resume resume = resumeMapper.selectById("default");
        if (resume == null) {
            resume = new Resume();
            resume.setId("default");
            resume.setResume("");
            resume.setCreatedAt(LocalDateTime.now());
            resume.setUpdatedAt(LocalDateTime.now());
            resumeMapper.insert(resume);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("id", resume.getId());
        res.put("resume", resume.getResume() != null ? resume.getResume() : "");
        res.put("createdAt", resume.getCreatedAt());
        res.put("updatedAt", resume.getUpdatedAt());
        return res;
    }

    /**
     * 保存或更新个人简历
     *
     * @param body 请求参数，包含 resume
     */
    @Override
    public Map<String, Object> updateResume(Map<String, Object> body) {
        Resume resume = resumeMapper.selectById("default");
        if (resume == null) {
            resume = new Resume();
            resume.setId("default");
            resume.setCreatedAt(LocalDateTime.now());
        }

        if (body.containsKey("resume")) {
            resume.setResume((String) body.get("resume"));
        }
        resume.setUpdatedAt(LocalDateTime.now());

        if (resumeMapper.selectById("default") == null) {
            resumeMapper.insert(resume);
        } else {
            resumeMapper.updateById(resume);
        }

        return getResume();
    }

    /**
     * 获取纯文本简历内容
     *
     * @return 简历字符串，若无则返回空串
     */
    @Override
    public String getResumeText() {
        Resume resume = resumeMapper.selectById("default");
        return (resume != null && resume.getResume() != null) ? resume.getResume() : "";
    }
}
