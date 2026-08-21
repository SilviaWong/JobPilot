package com.jobpilot.jobpilotserver.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.mapper.CompanyMapper;
import com.jobpilot.jobpilotserver.mapper.JobMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import com.jobpilot.jobpilotserver.service.DataBackfillService;
import com.jobpilot.jobpilotserver.strategy.normalizer.JobNormalizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史数据清洗与回填业务实现类
 * <p>
 * 读取现有 Job / Company 数据库记录，使用 Normalizer 重新解析 rawData，
 * 将清洗后的 14 个 Job 字段和 5 个 Company 字段写回数据库的新物理列中。
 */
@Service
public class DataBackfillServiceImpl implements DataBackfillService {

    private static final Logger log = LoggerFactory.getLogger(DataBackfillServiceImpl.class);

    private final JobMapper jobMapper;
    private final CompanyMapper companyMapper;
    private final JobNormalizerFactory jobNormalizerFactory;
    private final ObjectMapper objectMapper;

    public DataBackfillServiceImpl(JobMapper jobMapper,
                                  CompanyMapper companyMapper,
                                  JobNormalizerFactory jobNormalizerFactory,
                                  ObjectMapper objectMapper) {
        this.jobMapper = jobMapper;
        this.companyMapper = companyMapper;
        this.jobNormalizerFactory = jobNormalizerFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> backfillAll() {
        log.info("Starting historical data backfill for Job and Company records...");

        int processedJobs = 0;
        int updatedJobs = 0;
        int processedCompanies = 0;
        int updatedCompanies = 0;

        LocalDateTime now = LocalDateTime.now();

        // 1. 批量遍历回填 Job 记录
        List<Job> allJobs = jobMapper.selectList(null);
        for (Job job : allJobs) {
            processedJobs++;
            JsonNode raw1 = parseJson(job.getRawData());
            JsonNode raw2 = parseJson(job.getRawData2());

            NormalizedJobDataVO vo = jobNormalizerFactory.normalize(job, raw1, raw2, null, null);
            if (vo == null) continue;

            boolean changed = false;

            if (job.getExperience() == null && isNotBlank(vo.getExperience())) {
                job.setExperience(vo.getExperience());
                changed = true;
            }
            if (job.getCity() == null && isNotBlank(vo.getCity())) {
                job.setCity(vo.getCity());
                changed = true;
            }
            if (job.getArea() == null && isNotBlank(vo.getArea())) {
                job.setArea(vo.getArea());
                changed = true;
            }
            if (job.getBusinessDistrict() == null && isNotBlank(vo.getBusinessDistrict())) {
                job.setBusinessDistrict(vo.getBusinessDistrict());
                changed = true;
            }
            if (job.getJobUrl() == null && isNotBlank(vo.getJobUrl())) {
                job.setJobUrl(vo.getJobUrl());
                changed = true;
            }
            if (job.getJobDesc() == null && isNotBlank(vo.getJobDesc())) {
                job.setJobDesc(vo.getJobDesc());
                changed = true;
            }
            if (job.getSkills() == null && vo.getSkills() != null && !vo.getSkills().isEmpty()) {
                job.setSkills(toJsonArray(vo.getSkills()));
                changed = true;
            }
            if (job.getWelfareList() == null && vo.getWelfareList() != null && !vo.getWelfareList().isEmpty()) {
                job.setWelfareList(toJsonArray(vo.getWelfareList()));
                changed = true;
            }
            if (job.getHrName() == null && isNotBlank(vo.getHrName())) {
                job.setHrName(vo.getHrName());
                changed = true;
            }
            if (job.getHrPosition() == null && isNotBlank(vo.getHrPosition())) {
                job.setHrPosition(vo.getHrPosition());
                changed = true;
            }
            if (job.getPublishDate() == null && isNotBlank(vo.getPublishDate())) {
                job.setPublishDate(vo.getPublishDate());
                changed = true;
            }
            if (job.getUpdateDate() == null && isNotBlank(vo.getUpdateDate())) {
                job.setUpdateDate(vo.getUpdateDate());
                changed = true;
            }
            if ((job.getIsHeadhunter() == null || !job.getIsHeadhunter()) && vo.isHeadhunter()) {
                job.setIsHeadhunter(true);
                changed = true;
            }
            if (job.getClientCompanyName() == null && isNotBlank(vo.getClientCompanyName())) {
                job.setClientCompanyName(vo.getClientCompanyName());
                changed = true;
            }

            if (changed) {
                job.setUpdatedAt(now);
                jobMapper.updateById(job);
                updatedJobs++;
            }
        }

        // 2. 批量遍历回填 Company 记录
        List<Company> allCompanies = companyMapper.selectList(null);
        for (Company company : allCompanies) {
            processedCompanies++;
            JsonNode compRaw = parseJson(company.getRawData());
            if (compRaw == null) continue;

            boolean changed = false;

            String ind = getText(compRaw, "industry", "brandIndustry", "industryName");
            if (company.getIndustry() == null && isNotBlank(ind)) {
                company.setIndustry(ind);
                changed = true;
            }

            String scale = getText(compRaw, "scale", "brandScaleName", "scaleName", "companySize");
            if (company.getScale() == null && isNotBlank(scale)) {
                company.setScale(scale);
                changed = true;
            }

            String stage = getText(compRaw, "stage", "brandStageName", "stageName", "customerBrandStageName");
            if (company.getStage() == null && isNotBlank(stage)) {
                company.setStage(stage);
                changed = true;
            }

            String desc = getText(compRaw, "companyDesc", "companyDescription", "intro");
            if (company.getCompanyDesc() == null && isNotBlank(desc)) {
                company.setCompanyDesc(desc);
                changed = true;
            }

            if (changed) {
                company.setUpdatedAt(now);
                companyMapper.updateById(company);
                updatedCompanies++;
            }
        }

        log.info("Backfill completed. Jobs: {}/{} updated. Companies: {}/{} updated.",
                updatedJobs, processedJobs, updatedCompanies, processedCompanies);

        Map<String, Object> res = new HashMap<>();
        res.put("processedJobs", processedJobs);
        res.put("updatedJobs", updatedJobs);
        res.put("processedCompanies", processedCompanies);
        res.put("updatedCompanies", updatedCompanies);
        return res;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private JsonNode parseJson(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            return objectMapper.readTree(str);
        } catch (Exception e) {
            return null;
        }
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    private String getText(JsonNode node, String... keys) {
        if (node == null) return null;
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                String val = node.get(key).asText();
                if (isNotBlank(val)) return val.trim();
            }
        }
        return null;
    }
}
