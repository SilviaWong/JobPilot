package com.jobpilot.jobpilotserver.strategy.normalizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobpilot.jobpilotserver.model.entity.Job;
import com.jobpilot.jobpilotserver.model.vo.NormalizedJobDataVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobNormalizerTest {

    private ObjectMapper objectMapper;
    private JobNormalizerFactory factory;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        List<JobNormalizerStrategy> strategies = List.of(
                new BossJobNormalizer(),
                new Job51Normalizer(),
                new ZhilianJobNormalizer(),
                new LiepinJobNormalizer(),
                new DefaultJobNormalizer()
        );
        factory = new JobNormalizerFactory(strategies);
    }

    @Test
    @DisplayName("测试Boss直聘职位数据标准化清洗策略")
    void testBossJobNormalization() throws Exception {
        String bossJson = "{\n" +
                "  \"encryptJobId\": \"boss_123\",\n" +
                "  \"jobName\": \"Java高级开发专家\",\n" +
                "  \"salaryDesc\": \"25-40K\",\n" +
                "  \"cityName\": \"杭州\",\n" +
                "  \"brandName\": \"阿里巴巴\",\n" +
                "  \"brandScaleName\": \"10000人以上\",\n" +
                "  \"brandIndustry\": \"互联网\",\n" +
                "  \"skills\": [\"Java\", \"Spring Boot\", \"MySQL\"],\n" +
                "  \"welfareList\": [\"五险一金\", \"年终奖\"]\n" +
                "}";

        JsonNode rawData = objectMapper.readTree(bossJson);
        Job job = new Job();
        job.setJobId("boss_123");
        job.setTitle("Java高级开发专家");
        job.setCompanyName("阿里巴巴");
        job.setSalary("25-40K");
        job.setLocation("杭州");
        job.setPlatform("Boss直聘");

        NormalizedJobDataVO vo = factory.normalize(job, rawData, null, null, null);

        assertNotNull(vo);
        assertEquals("Java高级开发专家", vo.getJobName());
        assertEquals("阿里巴巴", vo.getBrandName());
        assertEquals("25-40K", vo.getSalaryRange());
        assertEquals("杭州", vo.getCity());
        assertTrue(vo.getSkills().contains("Java"));
        assertTrue(vo.getWelfareList().contains("年终奖"));
    }

    @Test
    @DisplayName("测试未知平台回退至 DefaultJobNormalizer")
    void testFallbackDefaultJobNormalization() {
        Job job = new Job();
        job.setJobId("unknown_999");
        job.setTitle("全栈工程师");
        job.setCompanyName("某创业公司");
        job.setSalary("15-20K");
        job.setLocation("北京");
        job.setPlatform("未知平台");

        NormalizedJobDataVO vo = factory.normalize(job, null, null, null, null);

        assertNotNull(vo);
        assertEquals("全栈工程师", vo.getJobName());
        assertEquals("某创业公司", vo.getBrandName());
        assertEquals("15-20K", vo.getSalaryRange());
    }
}
