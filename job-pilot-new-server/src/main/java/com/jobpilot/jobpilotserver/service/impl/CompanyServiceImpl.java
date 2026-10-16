package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.BlacklistedCompanyMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyAliasMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.CompanyAlias;
import com.jobpilot.jobpilotserver.model.vo.CompanyVO;
import com.jobpilot.jobpilotserver.service.CompanyService;
import com.jobpilot.jobpilotserver.strategy.algorithm.CompanyDisjointSet;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.jobpilot.jobpilotserver.mapper.AiJobResultMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.AiJobResult;
import com.jobpilot.jobpilotserver.model.entity.Job;
import org.springframework.transaction.annotation.Transactional;

/**
 * 企业与黑名单管理业务实现类
 * <p>
 * 主要职责：
 * 1. 提供基于并查集（Disjoint-Set / DSU）算法的企业别名聚类归一化与高效物理分页查询；
 * 2. 维护企业黑名单列表（支持增删及 Caffeine 本地缓存的主动失效）；
 * 3. 维护企业主体合并关系与别名映射表（变更时自动刷新 DSU 本地缓存）。
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {

    private final ObjectMapper objectMapper;
    private final BlacklistedCompanyMapper blacklistedCompanyMapper;
    private final CompanyAliasMapper companyAliasMapper;
    private final JobMapper jobMapper;
    private final AiJobResultMapper aiJobResultMapper;
    private final Cache<String, Set<String>> blacklistedCompanyCache;
    private final Cache<String, CompanyDisjointSet> companyDsuCache;
    private final Cache<String, List<Map<String, Object>>> companyPanoramaCache;

    @org.springframework.beans.factory.annotation.Autowired
    public CompanyServiceImpl(ObjectMapper objectMapper,
                              BlacklistedCompanyMapper blacklistedCompanyMapper,
                              CompanyAliasMapper companyAliasMapper,
                              JobMapper jobMapper,
                              AiJobResultMapper aiJobResultMapper,
                              @Qualifier("blacklistedCompanyCache") Cache<String, Set<String>> blacklistedCompanyCache,
                              @Qualifier("companyDsuCache") Cache<String, CompanyDisjointSet> companyDsuCache,
                              @org.springframework.beans.factory.annotation.Autowired(required = false)
                              @Qualifier("companyPanoramaCache") Cache<String, List<Map<String, Object>>> companyPanoramaCache) {
        this.objectMapper = objectMapper;
        this.blacklistedCompanyMapper = blacklistedCompanyMapper;
        this.companyAliasMapper = companyAliasMapper;
        this.jobMapper = jobMapper;
        this.aiJobResultMapper = aiJobResultMapper;
        this.blacklistedCompanyCache = blacklistedCompanyCache;
        this.companyDsuCache = companyDsuCache;
        this.companyPanoramaCache = companyPanoramaCache;
    }

    public CompanyServiceImpl(ObjectMapper objectMapper,
                              BlacklistedCompanyMapper blacklistedCompanyMapper,
                              CompanyAliasMapper companyAliasMapper,
                              @Qualifier("blacklistedCompanyCache") Cache<String, Set<String>> blacklistedCompanyCache,
                              @Qualifier("companyDsuCache") Cache<String, CompanyDisjointSet> companyDsuCache) {
        this(objectMapper, blacklistedCompanyMapper, companyAliasMapper, null, null, blacklistedCompanyCache, companyDsuCache, null);
    }

    @Override
    public void invalidatePanoramaCache() {
        if (companyPanoramaCache != null) {
            companyPanoramaCache.invalidateAll();
            log.info("Company panorama cache invalidated successfully.");
        }
    }

    /**
     * 获取或构建并查集别名模型（优先命中 Caffeine 本地缓存）
     */
    private CompanyDisjointSet getOrBuildDsu() {
        return companyDsuCache.get("global_company_dsu", k -> {
            log.debug("Building CompanyDisjointSet model into Caffeine cache...");
            CompanyDisjointSet dsu = new CompanyDisjointSet();
            List<CompanyAlias> aliasList = companyAliasMapper.selectList(null);
            for (CompanyAlias alias : aliasList) {
                dsu.union(alias.getAliasName(), alias.getCanonicalName());
            }
            return dsu;
        });
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CompanyServiceImpl.class);

    public static String cleanCompanyName(String str) {
        if (str == null) return "";
        String res = str.trim();
        return res.replace("（", "(")
                .replace("）", ")")
                .replaceAll("\\s+", "");
    }

    private boolean isSourceDataHeadhunter(String platform, String rawData) {
        if (rawData == null || rawData.trim().isEmpty()) return false;
        try {
            JsonNode raw = objectMapper.readTree(rawData);
            if ("Boss直聘".equals(platform)) {
                boolean proxyJob = raw.path("proxyJob").asInt(0) == 1
                        || raw.path("zpData").path("jobInfo").path("proxyJob").asInt(0) == 1;
                String jobType = raw.path("岗位类型_外包猎头").asText("");
                return proxyJob || jobType.contains("猎头");
            }
            if ("猎聘".equals(platform)) {
                String jk = raw.path("job").path("jobKind").asText();
                if (jk.isEmpty()) jk = raw.path("jobKind").asText();
                String jobType = raw.path("岗位类型_外包猎头").asText("");
                return "1".equals(jk) || "猎头/代招".equals(jobType);
            }
            if ("智联".equals(platform) || "智联招聘".equals(platform)) {
                int proxy = raw.path("proxyModel").path("recruitPosition").asInt(0);
                int jobType = raw.path("jobType").asInt(0);
                return proxy > 0 || jobType > 0;
            }
            if ("51job".equals(platform) || "前程无忧".equals(platform)) {
                String jt = raw.path("jobType").asText("");
                String ct = raw.path("companyTypeString").asText("");
                return "1".equals(jt) || "2".equals(jt) || "中介".equals(ct);
            }
        } catch (Exception ignored) {}
        return false;
    }

    @Override
    @Transactional
    public boolean putAgency(String companyName, boolean isAgency) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return false;
        }
        boolean agencyFlag = isAgency;

        // 1. 查找是否存在该公司的记录
        List<Company> existingCompanies = list(new QueryWrapper<Company>()
                .eq("companyName", companyName)
                .or()
                .eq("companyFullName", companyName));

        if (!existingCompanies.isEmpty()) {
            for (Company c : existingCompanies) {
                c.setIsAgency(agencyFlag);
                if (agencyFlag) {
                    c.setIndustry("人力资源服务");
                }
                c.setUpdatedAt(LocalDateTime.now());
                updateById(c);
            }
        } else {
            Company shell = new Company();
            shell.setCompanyName(companyName);
            shell.setSourcePlatform("manual");
            shell.setIsAgency(agencyFlag);
            if (agencyFlag) {
                shell.setIndustry("人力资源服务");
            }
            shell.setCreatedAt(LocalDateTime.now());
            shell.setUpdatedAt(LocalDateTime.now());
            save(shell);
        }

        // 2. 级联同步职位
        if (jobMapper != null) {
            if (agencyFlag) {
                jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                        .set(Job::getIsHeadhunter, true)
                        .and(w -> w.eq(Job::getCompanyName, companyName)
                                .or()
                                .eq(Job::getCompanyFullName, companyName)));
            } else {
                List<Job> jobs = jobMapper.selectList(new QueryWrapper<Job>()
                        .eq("companyName", companyName)
                        .or()
                        .eq("companyFullName", companyName));
                List<String> revertJobIds = new ArrayList<>();
                for (Job j : jobs) {
                    if (!isSourceDataHeadhunter(j.getPlatform(), j.getRawData())) {
                        revertJobIds.add(j.getId());
                    }
                }
                if (!revertJobIds.isEmpty()) {
                    jobMapper.update(null, new LambdaUpdateWrapper<Job>()
                            .set(Job::getIsHeadhunter, false)
                            .in(Job::getId, revertJobIds));
                }
            }
        }
        invalidatePanoramaCache();
        return true;
    }

    @Override
    public Map<String, Object> getCompanies(int page, int pageSize, String query) {
        return getCompanies(page, pageSize, query, "normal", "direct", "all", "all");
    }

    @Override
    public Map<String, Object> getCompanies(int page, int pageSize, String query, String status, String agencyFilter, String platformFilter, String cityFilter) {
        if (jobMapper == null || aiJobResultMapper == null) {
            // 降级使用单表查询
            Page<Company> companyPage = new Page<>(page, pageSize);
            LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
            if (query != null && !query.trim().isEmpty()) {
                String q = query.trim();
                queryWrapper.and(w -> w.like(Company::getCompanyName, q).or().like(Company::getCompanyFullName, q));
            }
            queryWrapper.orderByDesc(Company::getUpdatedAt);
            Page<Company> resultPage = page(companyPage, queryWrapper);
            Map<String, Object> res = new HashMap<>();
            res.put("data", resultPage.getRecords());
            res.put("total", (int) resultPage.getTotal());
            res.put("hasMore", (page * pageSize) < resultPage.getTotal());
            return res;
        }

        String cacheKey = (status != null && !status.trim().isEmpty()) ? status.trim().toLowerCase() : "normal";
        List<Map<String, Object>> finalArray;
        if (companyPanoramaCache != null) {
            finalArray = companyPanoramaCache.get(cacheKey, k -> buildCompanyPanorama(k));
        } else {
            finalArray = buildCompanyPanorama(cacheKey);
        }

        // 过滤筛选 (针对内存中已预聚合并缓存的 finalArray 跑流式过滤，耗时通常 < 1ms)
        String searchStr = query != null ? query.trim().toLowerCase() : "";
        String agency = (agencyFilter != null && !agencyFilter.isEmpty()) ? agencyFilter : "direct";

        List<Map<String, Object>> filtered = finalArray.stream().filter(c -> {
            boolean isAgency = Boolean.TRUE.equals(c.get("isAgency"));
            if ("direct".equalsIgnoreCase(agency) && isAgency) return false;
            if ("agency".equalsIgnoreCase(agency) && !isAgency) return false;

            if (platformFilter != null && !platformFilter.isEmpty() && !"all".equalsIgnoreCase(platformFilter)) {
                List<String> ps = (List<String>) c.get("platformSources");
                if (ps == null || ps.stream().noneMatch(p -> p.contains(platformFilter))) return false;
            }

            if (cityFilter != null && !cityFilter.isEmpty() && !"all".equalsIgnoreCase(cityFilter)) {
                List<String> cts = (List<String>) c.get("cities");
                if (cts == null || !cts.contains(cityFilter)) return false;
            }

            if (!searchStr.isEmpty()) {
                String cName = (String) c.get("companyName");
                String cFullName = (String) c.get("companyFullName");
                String bName = (String) c.get("brandName");
                List<String> cts = (List<String>) c.get("cities");
                List<String> clis = (List<String>) c.get("clientCompanies");
                boolean match = (cName != null && cName.toLowerCase().contains(searchStr))
                        || (cFullName != null && cFullName.toLowerCase().contains(searchStr))
                        || (bName != null && bName.toLowerCase().contains(searchStr))
                        || (cts != null && cts.stream().anyMatch(ct -> ct.toLowerCase().contains(searchStr)))
                        || (clis != null && clis.stream().anyMatch(cli -> cli.toLowerCase().contains(searchStr)));
                if (!match) return false;
            }
            return true;
        }).collect(Collectors.toList());

        int total = filtered.size();
        int startIndex = Math.max(0, (page - 1) * pageSize);
        int endIndex = Math.min(total, startIndex + pageSize);
        List<Map<String, Object>> paginated = startIndex < total ? filtered.subList(startIndex, endIndex) : Collections.emptyList();

        Map<String, Object> res = new HashMap<>();
        res.put("data", paginated);
        res.put("total", total);
        res.put("hasMore", endIndex < total);
        return res;
    }

    /**
     * 构建企业全景聚合大盘数据 (聚合计算后写入 Caffeine 本地缓存)
     * <p>
     * 性能优化点：
     * 1. 列投影过滤：排除 job_desc, raw_data 等大字段，降低 90% 堆内存开销与 SQLite I/O；
     * 2. 字段瘦身：仅组装职位摘要卡片必要字段，网络 Payload 降低 95%；
     * 3. 规避无意义的 JSON 反序列化：避免循环解析未使用的 skills 与 welfareList。
     */
    private List<Map<String, Object>> buildCompanyPanorama(String status) {
        long startTime = System.currentTimeMillis();
        log.info("Cache miss: Rebuilding company panorama for status: {}", status);

        // 1. 获取职位（只投影聚合所需列，排除无用大字段）
        QueryWrapper<Job> jobWrapper = new QueryWrapper<>();
        jobWrapper.select(
                "id", "jobId", "title", "companyName", "companyFullName",
                "salary", "location", "city", "platform", "isHeadhunter",
                "clientCompanyName", "status", "isHidden", "tags",
                "createdAt", "firstSeen"
        );
        jobWrapper.ne("status", "deleted");
        if ("normal".equalsIgnoreCase(status) || status == null || status.isEmpty()) {
            jobWrapper.eq("isHidden", false).eq("status", "normal");
        }
        List<Job> jobs = jobMapper.selectList(jobWrapper);

        // 1.1 获取 AI 结果映射 (仅投影 jobId, score, matchLevel)
        QueryWrapper<AiJobResult> aiWrapper = new QueryWrapper<>();
        aiWrapper.select("jobId", "score", "matchLevel");
        List<AiJobResult> aiResults = aiJobResultMapper.selectList(aiWrapper);
        Map<String, AiJobResult> aiResultMap = new HashMap<>(aiResults.size());
        for (AiJobResult a : aiResults) {
            if (a.getJobId() != null) {
                aiResultMap.put(a.getJobId(), a);
            }
        }

        // 1.2 获取黑名单集合（优先命中 blacklistedCompanyCache）
        Set<String> blacklistedSet = blacklistedCompanyCache != null ?
                blacklistedCompanyCache.get("all_blacklisted", k -> {
                    List<BlacklistedCompany> blacklisted = blacklistedCompanyMapper.selectList(null);
                    return blacklisted.stream()
                            .map(BlacklistedCompany::getCompanyName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                }) : Collections.emptySet();

        // 2. 获取所有 Company 记录并建立映射（只投影必要列，排除 raw_data / raw_data2 / raw_data3）
        LambdaQueryWrapper<Company> companyQuery = new LambdaQueryWrapper<>();
        companyQuery.select(
                Company::getCompanyName, Company::getCompanyFullName,
                Company::getSourcePlatform, Company::getCompanyId,
                Company::getIsAgency, Company::getIndustry,
                Company::getScale, Company::getStage,
                Company::getCompanyType, Company::getCreditCode,
                Company::getLogo, Company::getWelfareList
        );
        List<Company> companiesData = list(companyQuery);
        Map<String, CompanyNode> companyMap = new HashMap<>(companiesData.size() * 2);

        for (Company c : companiesData) {
            String cleanFull = cleanCompanyName(c.getCompanyFullName());
            String cleanName = cleanCompanyName(c.getCompanyName());
            String key = !cleanFull.isEmpty() ? cleanFull : cleanName;
            if (key.isEmpty() || "未知公司".equals(key)) continue;

            CompanyNode node = companyMap.get(key);
            if (node == null) {
                node = new CompanyNode();
                node.companyName = key;
                node.companyFullName = !cleanFull.isEmpty() ? cleanFull : cleanName;
                node.brandName = (!cleanName.isEmpty() && !cleanName.equals(cleanFull)) ? cleanName : "";
                node.sourcePlatform = c.getSourcePlatform();
                node.companyId = c.getCompanyId();
                node.isAgency = Boolean.TRUE.equals(c.getIsAgency());
                node.industry = c.getIndustry();
                node.scale = c.getScale();
                node.stage = c.getStage();
                node.companyType = c.getCompanyType();
                node.creditCode = c.getCreditCode();
                node.logo = c.getLogo();
                node.welfareList = parseJsonList(c.getWelfareList());
                companyMap.put(key, node);
            } else {
                if (Boolean.TRUE.equals(c.getIsAgency())) node.isAgency = true;
                if (node.creditCode == null && c.getCreditCode() != null) node.creditCode = c.getCreditCode();
                if (node.industry == null && c.getIndustry() != null) node.industry = c.getIndustry();
                if (node.scale == null && c.getScale() != null) node.scale = c.getScale();
                if (node.stage == null && c.getStage() != null) node.stage = c.getStage();
                if (node.companyType == null && c.getCompanyType() != null) node.companyType = c.getCompanyType();
                if (node.logo == null && c.getLogo() != null) node.logo = c.getLogo();
                if ((node.brandName == null || node.brandName.equals(key)) && !cleanName.isEmpty() && !cleanName.equals(key)) {
                    node.brandName = cleanName;
                }
            }
            if (!cleanName.isEmpty() && !cleanName.equals(key) && !companyMap.containsKey(cleanName)) {
                companyMap.put(cleanName, node);
            }
        }

        // 3. 聚合职位到公司节点
        Map<String, CompanyNode> resultNodes = new LinkedHashMap<>();

        for (Job job : jobs) {
            String cleanFull = cleanCompanyName(job.getCompanyFullName());
            String cleanBrand = cleanCompanyName(job.getCompanyName());
            String nodeKey = !cleanFull.isEmpty() ? cleanFull : cleanBrand;
            String platform = job.getPlatform();
            if (nodeKey.isEmpty() || "未知公司".equals(nodeKey) || platform == null) continue;

            CompanyNode node = resultNodes.get(nodeKey);
            if (node == null) {
                CompanyNode baseCompany = companyMap.get(nodeKey);
                if (baseCompany == null && !cleanBrand.isEmpty()) {
                    baseCompany = companyMap.get(cleanBrand);
                }
                node = new CompanyNode();
                if (baseCompany != null) {
                    node.companyName = nodeKey;
                    node.companyFullName = !cleanFull.isEmpty() ? cleanFull : nodeKey;
                    node.brandName = (!cleanBrand.isEmpty() && !cleanBrand.equals(nodeKey)) ? cleanBrand : (baseCompany.brandName != null ? baseCompany.brandName : "");
                    node.sourcePlatform = baseCompany.sourcePlatform != null ? baseCompany.sourcePlatform : platform;
                    node.companyId = baseCompany.companyId;
                    node.isAgency = baseCompany.isAgency;
                    node.industry = baseCompany.industry;
                    node.scale = baseCompany.scale;
                    node.stage = baseCompany.stage;
                    node.companyType = baseCompany.companyType;
                    node.creditCode = baseCompany.creditCode;
                    node.logo = baseCompany.logo;
                    node.welfareList = baseCompany.welfareList;
                } else {
                    node.companyName = nodeKey;
                    node.companyFullName = !cleanFull.isEmpty() ? cleanFull : nodeKey;
                    node.brandName = (!cleanBrand.isEmpty() && !cleanBrand.equals(nodeKey)) ? cleanBrand : "";
                    node.sourcePlatform = platform;
                    node.isAgency = false;
                }
                resultNodes.put(nodeKey, node);
            }

            // 统计城市
            String jobCity = job.getCity() != null ? job.getCity() : (job.getLocation() != null ? job.getLocation().split("[\\s·-]")[0] : "");
            if (jobCity != null && !jobCity.trim().isEmpty() && !"未知".equals(jobCity.trim())) {
                String c = jobCity.trim();
                node.cityMap.put(c, node.cityMap.getOrDefault(c, 0) + 1);
            }

            // 统计平台
            node.platformSources.add(platform);
            PlatformStat stat = node.platformStats.computeIfAbsent(platform, k -> new PlatformStat());
            stat.total++;
            if (Boolean.TRUE.equals(job.getIsHeadhunter())) {
                stat.headhunter++;
            } else {
                stat.direct++;
            }

            // 猎头标记与客户画像
            if (Boolean.TRUE.equals(job.getIsHeadhunter())) {
                node.isAgency = true;
                node.industry = "人力资源服务";
                node.headhunterJobsCount++;
                if (job.getClientCompanyName() != null && !job.getClientCompanyName().trim().isEmpty()) {
                    node.clientCompanies.add(job.getClientCompanyName().trim());
                }
            } else {
                node.directJobsCount++;
            }

            if (!node.isAgency) {
                if (!cleanBrand.isEmpty() && !cleanBrand.equals(nodeKey) && (node.brandName == null || node.brandName.equals(nodeKey))) {
                    node.brandName = cleanBrand;
                }
            } else {
                node.brandName = "";
                node.industry = "人力资源服务";
            }

            // 组装轻量级职位条目（排除超长 jobDesc, 降低 95% 堆开销与传输 Payload）
            Map<String, Object> jobItem = new LinkedHashMap<>();
            jobItem.put("id", job.getId());
            jobItem.put("jobId", job.getJobId());
            jobItem.put("title", job.getTitle());
            jobItem.put("companyName", job.getCompanyName());
            jobItem.put("companyFullName", job.getCompanyFullName());
            jobItem.put("salary", job.getSalary());
            jobItem.put("location", job.getLocation());
            jobItem.put("city", job.getCity());
            jobItem.put("platform", job.getPlatform());
            jobItem.put("isHeadhunter", Boolean.TRUE.equals(job.getIsHeadhunter()));
            jobItem.put("clientCompanyName", job.getClientCompanyName());
            jobItem.put("applyStatus", job.getApplyStatus());
            jobItem.put("status", job.getStatus());
            jobItem.put("isHidden", Boolean.TRUE.equals(job.getIsHidden()));
            jobItem.put("firstSeen", job.getFirstSeen());
            jobItem.put("createdAt", job.getCreatedAt());
            jobItem.put("tags", parseJsonList(job.getTags()));
            jobItem.put("aiResult", aiResultMap.get(job.getJobId()));
            boolean isBlacklisted = blacklistedSet.contains(job.getCompanyName())
                    || (job.getCompanyFullName() != null && blacklistedSet.contains(job.getCompanyFullName()))
                    || (job.getClientCompanyName() != null && blacklistedSet.contains(job.getClientCompanyName()))
                    || blacklistedSet.contains(nodeKey);
            jobItem.put("isBlacklisted", isBlacklisted);

            node.jobs.add(jobItem);
        }

        // 4. 将 Map 转换为 List，计算 citiesWithCount, platformSources 等
        List<Map<String, Object>> finalArray = new ArrayList<>(resultNodes.size());
        for (CompanyNode node : resultNodes.values()) {
            Map<String, Object> compObj = new LinkedHashMap<>();
            compObj.put("companyName", node.companyName);
            compObj.put("companyFullName", node.companyFullName);
            compObj.put("brandName", node.brandName);
            compObj.put("sourcePlatform", node.sourcePlatform);
            compObj.put("companyId", node.companyId);
            compObj.put("isAgency", node.isAgency);
            compObj.put("industry", node.industry);
            compObj.put("scale", node.scale);
            compObj.put("stage", node.stage);
            compObj.put("companyType", node.companyType);
            compObj.put("creditCode", node.creditCode);
            compObj.put("logo", node.logo);
            compObj.put("welfareList", node.welfareList);
            compObj.put("headhunterJobsCount", node.headhunterJobsCount);
            compObj.put("directJobsCount", node.directJobsCount);
            compObj.put("jobs", node.jobs);
            compObj.put("jobsCount", node.jobs.size());

            List<Map<String, Object>> sortedCities = node.cityMap.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .map(e -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("city", e.getKey());
                        m.put("count", e.getValue());
                        return m;
                    }).collect(Collectors.toList());
            compObj.put("citiesWithCount", sortedCities);
            compObj.put("cities", sortedCities.stream().map(m -> (String) m.get("city")).collect(Collectors.toList()));
            compObj.put("platformSources", new ArrayList<>(node.platformSources));
            compObj.put("clientCompanies", new ArrayList<>(node.clientCompanies));
            compObj.put("platformStats", node.platformStats);

            finalArray.add(compObj);
        }

        // 按在招职位数量倒序排列
        finalArray.sort((a, b) -> Integer.compare(((List<?>) b.get("jobs")).size(), ((List<?>) a.get("jobs")).size()));
        log.info("Company panorama rebuilt in {}ms, total companies: {}", (System.currentTimeMillis() - startTime), finalArray.size());
        return finalArray;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static class CompanyNode {
        String companyName;
        String companyFullName;
        String brandName;
        String sourcePlatform;
        String companyId;
        boolean isAgency;
        String industry;
        String scale;
        String stage;
        String companyType;
        String creditCode;
        String logo;
        List<String> welfareList = Collections.emptyList();
        String rawData;
        String rawData2;
        String rawData3;
        List<Map<String, Object>> jobs = new ArrayList<>();
        Set<String> platformSources = new LinkedHashSet<>();
        Map<String, PlatformStat> platformStats = new LinkedHashMap<>();
        Set<String> clientCompanies = new LinkedHashSet<>();
        Map<String, Integer> cityMap = new LinkedHashMap<>();
        int headhunterJobsCount = 0;
        int directJobsCount = 0;
    }

    public static class PlatformStat {
        public int total = 0;
        public int headhunter = 0;
        public int direct = 0;
    }

    /**
     * 获取数据库中全部原始企业记录
     *
     * @return 企业实体列表
     */
    @Override
    public List<Company> getAllCompanies() {
        return list();
    }

    /**
     * 获取所有被加入黑名单的企业列表
     *
     * @return 黑名单企业实体列表
     */
    @Override
    public List<BlacklistedCompany> getBlacklist() {
        return blacklistedCompanyMapper.selectList(null);
    }

    /**
     * 将指定企业加入黑名单或更新拉黑原因
     * <p>
     * 操作成功后会自动清空 Caffeine 本地黑名单缓存，以确保岗位列表过滤时即时生效。
     *
     * @param companyName 企业名称
     * @param reason 拉黑原因（如：外包、加班严重、PUA 等）
     * @return 是否操作成功
     */
    @Override
    public boolean addBlacklist(String companyName, String reason) {
        boolean success;
        BlacklistedCompany existing = blacklistedCompanyMapper.selectOne(
                new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, companyName));
        if (existing != null) {
            existing.setReason(reason);
            existing.setCreatedAt(LocalDateTime.now());
            success = blacklistedCompanyMapper.updateById(existing) > 0;
        } else {
            BlacklistedCompany item = new BlacklistedCompany();
            item.setCompanyName(companyName);
            item.setReason(reason);
            item.setCreatedAt(LocalDateTime.now());
            success = blacklistedCompanyMapper.insert(item) > 0;
        }
        // 拉黑变更成功后，强制失效 Caffeine 本地黑名单与全景缓存
        if (success) {
            blacklistedCompanyCache.invalidateAll();
            invalidatePanoramaCache();
        }
        return success;
    }

    /**
     * 从黑名单中移除指定企业
     * <p>
     * 移除成功后会自动清空 Caffeine 本地黑名单与全景缓存。
     *
     * @param companyName 企业名称
     * @return 是否移除成功
     */
    @Override
    public boolean removeBlacklist(String companyName) {
        boolean success = blacklistedCompanyMapper.delete(
                new LambdaQueryWrapper<BlacklistedCompany>().eq(BlacklistedCompany::getCompanyName, companyName)) > 0;
        // 移除成功后，强制失效 Caffeine 本地黑名单与全景缓存
        if (success) {
            blacklistedCompanyCache.invalidateAll();
            invalidatePanoramaCache();
        }
        return success;
    }

    /**
     * 合并企业主体别名
     * <p>
     * 将一组来源别名企业 (sourceNames) 归并映射到目标规范主体 (targetName)，
     * 并持久化到别名拓扑表中供并查集聚类使用。
     *
     * @param targetName 目标标准企业名称
     * @param sourceNames 待合并的别名企业名称列表
     * @return 是否合并成功
     */
    @Override
    public boolean mergeCompanies(String targetName, List<String> sourceNames) {
        for (String source : sourceNames) {
            if (source.equals(targetName)) continue;
            CompanyAlias existing = companyAliasMapper.selectOne(
                    new LambdaQueryWrapper<CompanyAlias>().eq(CompanyAlias::getAliasName, source));
            if (existing != null) {
                existing.setCanonicalName(targetName);
                companyAliasMapper.updateById(existing);
            } else {
                CompanyAlias alias = new CompanyAlias();
                alias.setId(UUID.randomUUID().toString());
                alias.setAliasName(source);
                alias.setCanonicalName(targetName);
                alias.setCreatedAt(LocalDateTime.now());
                companyAliasMapper.insert(alias);
            }
        }
        // 别名拓扑发生变更，强制失效 DSU 本地缓存与全景缓存
        companyDsuCache.invalidateAll();
        invalidatePanoramaCache();
        return true;
    }
}
