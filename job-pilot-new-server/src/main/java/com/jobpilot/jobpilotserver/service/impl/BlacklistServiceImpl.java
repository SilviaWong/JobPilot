package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.jobpilot.jobpilotserver.mapper.BlacklistedCompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.service.BlacklistService;
import com.jobpilot.jobpilotserver.util.BlacklistParserUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BlacklistServiceImpl implements BlacklistService {

    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final JobMapper jobMapper;
    private final Cache<String, Set<String>> blacklistedCompanyCache;

    public BlacklistServiceImpl(BlacklistedCompanyMapper blacklistedCompanyMapper,
                                JobMapper jobMapper,
                                @Qualifier("blacklistedCompanyCache") Cache<String, Set<String>> blacklistedCompanyCache) {
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.jobMapper = jobMapper;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
    }

    @Override
    public Map<String, Object> getBlacklist(String keyword, String source, int page, int pageSize) {
        LambdaQueryWrapper<BlacklistedCompany> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(BlacklistedCompany::getCompanyName, keyword.trim());
        }
        if (source != null && !source.trim().isEmpty() && !"all".equalsIgnoreCase(source.trim())) {
            wrapper.eq(BlacklistedCompany::getSource, source.trim());
        }
        wrapper.orderByDesc(BlacklistedCompany::getCreatedAt);

        Page<BlacklistedCompany> pageResult = blacklistedCompanyMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<BlacklistedCompany> items = pageResult.getRecords();

        List<String> companyNames = new ArrayList<>();
        for (BlacklistedCompany item : items) {
            if (item.getCompanyName() != null) {
                companyNames.add(item.getCompanyName());
            }
        }

        Map<String, Integer> jobCountMap = new HashMap<>();
        if (!companyNames.isEmpty() && jobMapper != null) {
            List<Job> matchedJobs = jobMapper.selectList(new QueryWrapper<Job>()
                    .in("companyName", companyNames)
                    .or()
                    .in("companyFullName", companyNames)
                    .or()
                    .in("clientCompanyName", companyNames));

            for (Job j : matchedJobs) {
                for (String name : companyNames) {
                    if (name.equals(j.getCompanyName()) || name.equals(j.getCompanyFullName()) || name.equals(j.getClientCompanyName())) {
                        jobCountMap.put(name, jobCountMap.getOrDefault(name, 0) + 1);
                    }
                }
            }
        }

        List<Map<String, Object>> enrichedItems = new ArrayList<>();
        for (BlacklistedCompany item : items) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("companyName", item.getCompanyName());
            map.put("reason", item.getReason());
            map.put("source", item.getSource());
            map.put("createdAt", item.getCreatedAt());
            map.put("updatedAt", item.getUpdatedAt());
            map.put("jobCount", jobCountMap.getOrDefault(item.getCompanyName(), 0));
            enrichedItems.add(map);
        }

        long totalCount = blacklistedCompanyMapper.selectCount(null);
        long chatCount = blacklistedCompanyMapper.selectCount(new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getSource, "chat_rejection"));
        long manualCount = blacklistedCompanyMapper.selectCount(new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getSource, "manual"));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", totalCount);
        stats.put("chatRejection", chatCount);
        stats.put("manual", manualCount);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("data", enrichedItems);
        res.put("total", (int) pageResult.getTotal());
        res.put("stats", stats);
        res.put("page", page);
        res.put("pageSize", pageSize);
        return res;
    }

    @Override
    @Transactional
    public Map<String, Object> addBlacklist(Map<String, Object> body) {
        if (body == null) {
            throw new IllegalArgumentException("未提供有效的企业黑名单数据");
        }

        List<Map<String, String>> companiesToAdd = new ArrayList<>();

        if (body.get("rawText") instanceof String rawText && !rawText.trim().isEmpty()) {
            List<BlacklistParserUtil.ParsedRejectionItem> parsed = BlacklistParserUtil.parseChatTextLines(rawText);
            for (BlacklistParserUtil.ParsedRejectionItem p : parsed) {
                Map<String, String> item = new HashMap<>();
                item.put("companyName", p.companyName);
                item.put("reason", p.reason);
                item.put("source", "chat_rejection");
                companiesToAdd.add(item);
            }
        } else if (body.get("companies") instanceof List<?> list) {
            for (Object obj : list) {
                if (obj instanceof Map<?, ?> m) {
                    Map<String, String> item = new HashMap<>();
                    item.put("companyName", (String) m.get("companyName"));
                    item.put("reason", (String) m.get("reason"));
                    item.put("source", m.get("source") != null ? (String) m.get("source") : "manual");
                    companiesToAdd.add(item);
                }
            }
        } else if (body.get("companyName") != null) {
            Map<String, String> item = new HashMap<>();
            item.put("companyName", (String) body.get("companyName"));
            item.put("reason", (String) body.get("reason"));
            item.put("source", body.get("source") != null ? (String) body.get("source") : "manual");
            companiesToAdd.add(item);
        }

        if (companiesToAdd.isEmpty()) {
            throw new IllegalArgumentException("未提供有效的企业黑名单数据");
        }

        int addedCount = 0;
        int updatedCount = 0;
        List<String> processedNames = new ArrayList<>();

        for (Map<String, String> item : companiesToAdd) {
            String rawName = item.get("companyName");
            String cleanName = BlacklistParserUtil.cleanCompanyNameForBlacklist(rawName);
            if (cleanName == null || cleanName.length() < 2) continue;

            String reason = item.get("reason") != null ? item.get("reason") : "人工拉黑";
            String source = item.get("source") != null ? item.get("source") : "manual";

            BlacklistedCompany existing = blacklistedCompanyMapper.selectOne(
                    new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, cleanName));

            if (existing != null) {
                existing.setReason(reason);
                existing.setSource(source);
                existing.setUpdatedAt(LocalDateTime.now());
                blacklistedCompanyMapper.updateById(existing);
                updatedCount++;
            } else {
                BlacklistedCompany record = new BlacklistedCompany();
                record.setCompanyName(cleanName);
                record.setReason(reason);
                record.setSource(source);
                record.setCreatedAt(LocalDateTime.now());
                record.setUpdatedAt(LocalDateTime.now());
                blacklistedCompanyMapper.insert(record);
                addedCount++;
            }
            processedNames.add(cleanName);
        }

        if (blacklistedCompanyCache != null) {
            blacklistedCompanyCache.invalidateAll();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("addedCount", addedCount);
        data.put("updatedCount", updatedCount);
        data.put("processedCompanies", processedNames);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("message", String.format("成功处理 %d 家企业 (新增 %d 家, 更新 %d 家)", processedNames.size(), addedCount, updatedCount));
        res.put("data", data);
        return res;
    }

    @Override
    @Transactional
    public boolean removeBlacklist(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return false;
        }
        boolean success = blacklistedCompanyMapper.delete(
                new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, companyName.trim())) > 0;
        if (blacklistedCompanyCache != null) {
            blacklistedCompanyCache.invalidateAll();
        }
        return success;
    }
}

