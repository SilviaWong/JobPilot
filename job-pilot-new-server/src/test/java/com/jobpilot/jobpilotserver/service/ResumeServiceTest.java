package com.jobpilot.jobpilotserver.service;

import com.jobpilot.jobpilotserver.mapper.ResumeMapper;
import com.jobpilot.jobpilotserver.model.entity.Resume;
import com.jobpilot.jobpilotserver.service.impl.ResumeServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeServiceTest {

    @Test
    public void testGetAndSaveResume() {
        AtomicReference<Resume> stored = new AtomicReference<>();

        ResumeMapper mapper = (ResumeMapper) Proxy.newProxyInstance(
                ResumeMapper.class.getClassLoader(),
                new Class<?>[]{ResumeMapper.class},
                (proxy, method, args) -> {
                    if ("selectById".equals(method.getName())) {
                        return stored.get();
                    } else if ("insert".equals(method.getName())) {
                        stored.set((Resume) args[0]);
                        return 1;
                    } else if ("updateById".equals(method.getName())) {
                        stored.set((Resume) args[0]);
                        return 1;
                    }
                    return null;
                }
        );

        ResumeServiceImpl service = new ResumeServiceImpl(mapper);

        // 1. Initial get creates default empty resume
        Map<String, Object> initial = service.getResume();
        assertNotNull(initial);
        assertEquals("default", initial.get("id"));
        assertEquals("", initial.get("resume"));

        // 2. Update resume
        Map<String, Object> updateResult = service.updateResume(Map.of("resume", "Java 开发工程师，5年经验"));
        assertEquals("Java 开发工程师，5年经验", updateResult.get("resume"));

        // 3. getResumeText
        String resumeText = service.getResumeText();
        assertEquals("Java 开发工程师，5年经验", resumeText);
    }
}
