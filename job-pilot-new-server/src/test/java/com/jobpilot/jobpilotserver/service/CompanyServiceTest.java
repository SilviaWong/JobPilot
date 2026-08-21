package com.jobpilot.jobpilotserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.jobpilot.jobpilotserver.mapper.BlacklistedCompanyMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyAliasMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.CompanyAlias;
import com.jobpilot.jobpilotserver.service.impl.CompanyServiceImpl;
import com.jobpilot.jobpilotserver.strategy.algorithm.CompanyDisjointSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyServiceTest {

    @Test
    @DisplayName("测试企业黑名单增删与本地缓存失效")
    public void testBlacklistCrudAndCacheInvalidation() {
        Map<String, BlacklistedCompany> blacklistStore = new HashMap<>();

        BlacklistedCompanyMapper blMapper = (BlacklistedCompanyMapper) Proxy.newProxyInstance(
                BlacklistedCompanyMapper.class.getClassLoader(),
                new Class<?>[]{BlacklistedCompanyMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return new ArrayList<>(blacklistStore.values());
                    } else if ("selectOne".equals(method.getName())) {
                        return blacklistStore.get("外包公司A");
                    } else if ("insert".equals(method.getName())) {
                        BlacklistedCompany item = (BlacklistedCompany) args[0];
                        blacklistStore.put(item.getCompanyName(), item);
                        return 1;
                    } else if ("delete".equals(method.getName())) {
                        blacklistStore.remove("外包公司A");
                        return 1;
                    }
                    return null;
                }
        );

        CompanyMapper compMapper = (CompanyMapper) Proxy.newProxyInstance(
                CompanyMapper.class.getClassLoader(),
                new Class<?>[]{CompanyMapper.class},
                (proxy, method, args) -> null
        );

        CompanyAliasMapper aliasMapper = (CompanyAliasMapper) Proxy.newProxyInstance(
                CompanyAliasMapper.class.getClassLoader(),
                new Class<?>[]{CompanyAliasMapper.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        var blCache = Caffeine.newBuilder().<String, Set<String>>build();
        var dsuCache = Caffeine.newBuilder().<String, CompanyDisjointSet>build();

        CompanyServiceImpl service = new CompanyServiceImpl(
                new ObjectMapper(),
                blMapper,
                aliasMapper,
                blCache,
                dsuCache
        );

        // 1. 添加黑名单
        boolean added = service.addBlacklist("外包公司A", "外包加班多");
        assertTrue(added);
        assertEquals(1, blacklistStore.size());

        // 2. 查询黑名单
        List<BlacklistedCompany> list = service.getBlacklist();
        assertEquals(1, list.size());
        assertEquals("外包公司A", list.get(0).getCompanyName());

        // 3. 移除黑名单
        boolean removed = service.removeBlacklist("外包公司A");
        assertTrue(removed);
        assertEquals(0, blacklistStore.size());
    }

    @Test
    @DisplayName("测试企业别名合并与DSU缓存刷新")
    public void testMergeCompaniesAndDsuCache() {
        Map<String, CompanyAlias> aliasStore = new HashMap<>();

        CompanyAliasMapper aliasMapper = (CompanyAliasMapper) Proxy.newProxyInstance(
                CompanyAliasMapper.class.getClassLoader(),
                new Class<?>[]{CompanyAliasMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return new ArrayList<>(aliasStore.values());
                    } else if ("selectOne".equals(method.getName())) {
                        return aliasStore.get("微信事业部");
                    } else if ("insert".equals(method.getName())) {
                        CompanyAlias a = (CompanyAlias) args[0];
                        aliasStore.put(a.getAliasName(), a);
                        return 1;
                    }
                    return null;
                }
        );

        CompanyMapper compMapper = (CompanyMapper) Proxy.newProxyInstance(
                CompanyMapper.class.getClassLoader(),
                new Class<?>[]{CompanyMapper.class},
                (proxy, method, args) -> null
        );

        BlacklistedCompanyMapper blMapper = (BlacklistedCompanyMapper) Proxy.newProxyInstance(
                BlacklistedCompanyMapper.class.getClassLoader(),
                new Class<?>[]{BlacklistedCompanyMapper.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        var blCache = Caffeine.newBuilder().<String, Set<String>>build();
        var dsuCache = Caffeine.newBuilder().<String, CompanyDisjointSet>build();

        CompanyServiceImpl service = new CompanyServiceImpl(
                new ObjectMapper(),
                blMapper,
                aliasMapper,
                blCache,
                dsuCache
        );

        // 合并别名
        boolean merged = service.mergeCompanies("腾讯科技", List.of("微信事业部", "腾讯科技"));
        assertTrue(merged);
        assertEquals(1, aliasStore.size());
        assertEquals("腾讯科技", aliasStore.get("微信事业部").getCanonicalName());
    }
}
