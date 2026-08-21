package com.jobpilot.jobpilotserver.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SalaryParserTest {

    @Test
    @DisplayName("测试标准月薪解析 (如: 15-25K)")
    void testStandardMonthSalary() {
        SalaryParser.SalaryRange range = SalaryParser.parse("15-25K");
        assertNotNull(range);
        assertEquals(15.0, range.minK, 0.01);
        assertEquals(25.0, range.maxK, 0.01);
    }

    @Test
    @DisplayName("测试多薪制折算 (如: 20-30K·16薪 -> 折算为 16/12 月薪)")
    void testMultiMonthBonusSalary() {
        SalaryParser.SalaryRange range = SalaryParser.parse("20-30K·16薪");
        assertNotNull(range);
        // 20 * 16 / 12 = 26.67, 30 * 16 / 12 = 40.0
        assertEquals(26.67, range.minK, 0.05);
        assertEquals(40.0, range.maxK, 0.05);
    }

    @Test
    @DisplayName("测试万元单位折算 (如: 2-3万/月 -> 20-30K)")
    void testWanSalary() {
        SalaryParser.SalaryRange range = SalaryParser.parse("2-3万");
        assertNotNull(range);
        assertEquals(20.0, range.minK, 0.01);
        assertEquals(30.0, range.maxK, 0.01);
    }

    @Test
    @DisplayName("测试年薪折算 (如: 24-36万/年 -> 20-30K/月)")
    void testYearSalary() {
        SalaryParser.SalaryRange range = SalaryParser.parse("24-36万/年");
        assertNotNull(range);
        assertEquals(20.0, range.minK, 0.01);
        assertEquals(30.0, range.maxK, 0.01);
    }

    @Test
    @DisplayName("测试日薪折算 (如: 400-500元/天 -> 按21.75天换算为 8.7-10.88K)")
    void testDailySalary() {
        SalaryParser.SalaryRange range = SalaryParser.parse("400-500元/天");
        assertNotNull(range);
        assertEquals(8.70, range.minK, 0.05);
        assertEquals(10.88, range.maxK, 0.05);
    }

    @Test
    @DisplayName("测试面议/保密薪资")
    void testNegotiableSalary() {
        assertNull(SalaryParser.parse("面议"));
        assertNull(SalaryParser.parse("薪资面议"));
        assertNull(SalaryParser.parse("保密"));
        assertNull(SalaryParser.parse(null));
        assertNull(SalaryParser.parse(""));
    }

    @ParameterizedTest
    @CsvSource({
            "'15-25K', '10-20K', true",
            "'15-25K', '26-35K', false",
            "'2-3万', '15-25K', true",
            "'30-50万/年', '20-40K', true"
    })
    @DisplayName("测试薪资区间匹配度过滤")
    void testMatchesFilter(String jobSalary, String filter, boolean expected) {
        boolean matched = SalaryParser.matchesFilter(jobSalary, filter);
        assertEquals(expected, matched);
    }
}
