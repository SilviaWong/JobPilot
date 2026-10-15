package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.common.exception.BusinessException;
import com.jobpilot.jobpilotserver.common.result.ResultCode;
import com.jobpilot.jobpilotserver.mapper.AiSettingsMapper;
import com.jobpilot.jobpilotserver.mapper.ResumeMapper;
import com.jobpilot.jobpilotserver.mapper.ResumeProfileMapper;
import com.jobpilot.jobpilotserver.model.entity.AiSettings;
import com.jobpilot.jobpilotserver.model.entity.Resume;
import com.jobpilot.jobpilotserver.model.entity.ResumeProfile;
import com.jobpilot.jobpilotserver.service.ResumeProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ResumeProfileServiceImpl extends ServiceImpl<ResumeProfileMapper, ResumeProfile> implements ResumeProfileService {

    private static final Logger log = LoggerFactory.getLogger(ResumeProfileServiceImpl.class);

    private final ResumeProfileMapper resumeProfileMapper;
    private final AiSettingsMapper aiSettingsMapper;
    private final ResumeMapper resumeMapper;
    private final ObjectMapper objectMapper;

    public ResumeProfileServiceImpl(ResumeProfileMapper resumeProfileMapper,
                                    AiSettingsMapper aiSettingsMapper,
                                    ResumeMapper resumeMapper,
                                    ObjectMapper objectMapper) {
        this.resumeProfileMapper = resumeProfileMapper;
        this.aiSettingsMapper = aiSettingsMapper;
        this.resumeMapper = resumeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Map<String, Object>> getProfiles() {
        List<ResumeProfile> list = resumeProfileMapper.selectList(
                new LambdaQueryWrapper<ResumeProfile>()
                        .orderByDesc(ResumeProfile::getIsDefault)
                        .orderByDesc(ResumeProfile::getUpdatedAt)
        );

        // 平滑迁移：若无记录且 AiSettings/Resume 中有旧简历，自动生成初始主简历
        if (list.isEmpty()) {
            String legacyResume = null;
            AiSettings settings = aiSettingsMapper.selectById("default");
            if (settings != null && StringUtils.hasText(settings.getResume())) {
                legacyResume = settings.getResume();
            } else {
                try {
                    Resume r = resumeMapper.selectById("default");
                    if (r != null && StringUtils.hasText(r.getResume())) {
                        legacyResume = r.getResume();
                    }
                } catch (Exception ignored) {}
            }

            if (StringUtils.hasText(legacyResume)) {
                ResumeProfile initial = new ResumeProfile();
                initial.setTitle("通用主简历 (默认)");
                initial.setTargetRole("Java开发工程师");
                try {
                    initial.setTags(objectMapper.writeValueAsString(List.of("默认", "全能")));
                } catch (Exception e) {
                    initial.setTags("[\"默认\",\"全能\"]");
                }
                initial.setContent(legacyResume.trim());
                initial.setIsDefault(true);
                initial.setCreatedAt(LocalDateTime.now());
                initial.setUpdatedAt(LocalDateTime.now());
                resumeProfileMapper.insert(initial);
                list = List.of(initial);
            }
        }

        List<Map<String, Object>> formatted = new ArrayList<>();
        for (ResumeProfile p : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("targetRole", p.getTargetRole() != null ? p.getTargetRole() : "");
            List<String> tagsList = new ArrayList<>();
            if (StringUtils.hasText(p.getTags())) {
                try {
                    tagsList = objectMapper.readValue(p.getTags(), new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    tagsList = List.of(p.getTags());
                }
            }
            item.put("tags", tagsList);
            item.put("content", p.getContent() != null ? p.getContent() : "");
            item.put("contentLength", p.getContent() != null ? p.getContent().length() : 0);
            item.put("isDefault", Boolean.TRUE.equals(p.getIsDefault()));
            item.put("createdAt", p.getCreatedAt());
            item.put("updatedAt", p.getUpdatedAt());
            formatted.add(item);
        }

        return formatted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeProfile createProfile(Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String targetRole = (String) body.get("targetRole");
        Object tagsObj = body.get("tags");
        Boolean isDefault = body.get("isDefault") instanceof Boolean ? (Boolean) body.get("isDefault") : false;

        if (!StringUtils.hasText(title)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "简历画像名称不能为空");
        }
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "简历正文内容不能为空");
        }

        Long count = resumeProfileMapper.selectCount(null);
        boolean finalDefault = count == 0 || Boolean.TRUE.equals(isDefault);

        if (finalDefault) {
            resumeProfileMapper.update(null,
                    new LambdaUpdateWrapper<ResumeProfile>()
                            .set(ResumeProfile::getIsDefault, false)
                            .eq(ResumeProfile::getIsDefault, true)
            );
        }

        ResumeProfile profile = new ResumeProfile();
        profile.setTitle(title.trim());
        profile.setTargetRole(targetRole != null ? targetRole.trim() : "");
        if (tagsObj != null) {
            try {
                if (tagsObj instanceof String) {
                    profile.setTags((String) tagsObj);
                } else {
                    profile.setTags(objectMapper.writeValueAsString(tagsObj));
                }
            } catch (Exception e) {
                profile.setTags("[]");
            }
        } else {
            profile.setTags("[]");
        }
        profile.setContent(content.trim());
        profile.setIsDefault(finalDefault);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        resumeProfileMapper.insert(profile);

        if (finalDefault) {
            syncToLegacySettings(profile.getContent());
        }

        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeProfile updateProfile(String id, Map<String, Object> body) {
        ResumeProfile existing = resumeProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "未找到指定简历");
        }

        if (body.containsKey("title")) {
            String title = (String) body.get("title");
            if (StringUtils.hasText(title)) {
                existing.setTitle(title.trim());
            }
        }
        if (body.containsKey("targetRole")) {
            existing.setTargetRole((String) body.get("targetRole"));
        }
        if (body.containsKey("content")) {
            String content = (String) body.get("content");
            if (StringUtils.hasText(content)) {
                existing.setContent(content.trim());
            }
        }
        if (body.containsKey("tags")) {
            Object tagsObj = body.get("tags");
            try {
                if (tagsObj instanceof String) {
                    existing.setTags((String) tagsObj);
                } else {
                    existing.setTags(objectMapper.writeValueAsString(tagsObj));
                }
            } catch (Exception e) {
                existing.setTags("[]");
            }
        }

        if (Boolean.TRUE.equals(body.get("isDefault"))) {
            resumeProfileMapper.update(null,
                    new LambdaUpdateWrapper<ResumeProfile>()
                            .ne(ResumeProfile::getId, id)
                            .set(ResumeProfile::getIsDefault, false)
            );
            existing.setIsDefault(true);
        }

        existing.setUpdatedAt(LocalDateTime.now());
        resumeProfileMapper.updateById(existing);

        if (Boolean.TRUE.equals(existing.getIsDefault()) && existing.getContent() != null) {
            syncToLegacySettings(existing.getContent());
        }

        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProfile(String id) {
        ResumeProfile existing = resumeProfileMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "未找到指定简历");
        }

        Long totalCount = resumeProfileMapper.selectCount(null);
        if (totalCount <= 1) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "系统需至少保留一份简历画像，不可全部删除");
        }

        resumeProfileMapper.deleteById(id);

        if (Boolean.TRUE.equals(existing.getIsDefault())) {
            ResumeProfile nextDefault = resumeProfileMapper.selectOne(
                    new LambdaQueryWrapper<ResumeProfile>()
                            .orderByDesc(ResumeProfile::getUpdatedAt)
                            .last("LIMIT 1")
            );
            if (nextDefault != null) {
                nextDefault.setIsDefault(true);
                nextDefault.setUpdatedAt(LocalDateTime.now());
                resumeProfileMapper.updateById(nextDefault);
                syncToLegacySettings(nextDefault.getContent());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeProfile setDefaultProfile(String id) {
        ResumeProfile target = resumeProfileMapper.selectById(id);
        if (target == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "未找到指定简历");
        }

        resumeProfileMapper.update(null,
                new LambdaUpdateWrapper<ResumeProfile>()
                        .ne(ResumeProfile::getId, id)
                        .set(ResumeProfile::getIsDefault, false)
        );

        target.setIsDefault(true);
        target.setUpdatedAt(LocalDateTime.now());
        resumeProfileMapper.updateById(target);

        syncToLegacySettings(target.getContent());
        return target;
    }

    @Override
    public String getDefaultResumeContent() {
        ResumeProfile defaultProfile = resumeProfileMapper.selectOne(
                new LambdaQueryWrapper<ResumeProfile>()
                        .eq(ResumeProfile::getIsDefault, true)
                        .last("LIMIT 1")
        );
        if (defaultProfile != null && StringUtils.hasText(defaultProfile.getContent())) {
            return defaultProfile.getContent();
        }

        AiSettings settings = aiSettingsMapper.selectById("default");
        if (settings != null && StringUtils.hasText(settings.getResume())) {
            return settings.getResume();
        }

        try {
            Resume r = resumeMapper.selectById("default");
            if (r != null && StringUtils.hasText(r.getResume())) {
                return r.getResume();
            }
        } catch (Exception ignored) {}

        return "";
    }

    private void syncToLegacySettings(String content) {
        if (!StringUtils.hasText(content)) return;
        try {
            AiSettings settings = aiSettingsMapper.selectById("default");
            if (settings != null) {
                settings.setResume(content);
                settings.setUpdatedAt(LocalDateTime.now());
                aiSettingsMapper.updateById(settings);
            }
        } catch (Exception e) {
            log.warn("Failed to sync resume to AiSettings: {}", e.getMessage());
        }

        try {
            Resume r = resumeMapper.selectById("default");
            if (r != null) {
                r.setResume(content);
                r.setUpdatedAt(LocalDateTime.now());
                resumeMapper.updateById(r);
            }
        } catch (Exception ignored) {}
    }
}

