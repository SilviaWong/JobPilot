package com.jobpilot.jobpilotserver.strategy.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyDisjointSetTest {

    @Test
    @DisplayName("测试并查集连通性与别名合并")
    void testDisjointSetUnionAndFind() {
        CompanyDisjointSet dsu = new CompanyDisjointSet();

        // 初始查找自身
        assertEquals("腾讯科技", dsu.find("腾讯科技"));
        assertEquals("腾讯微信事业部", dsu.find("腾讯微信事业部"));

        // 合并别名与集团
        dsu.union("腾讯微信事业部", "腾讯科技");
        dsu.union("腾讯云", "腾讯科技");

        // 验证传递性合并 (腾讯云 和 腾讯微信事业部 拥有相同根节点)
        assertEquals(dsu.find("腾讯微信事业部"), dsu.find("腾讯云"));
        assertEquals(dsu.find("腾讯科技"), dsu.find("腾讯云"));

        // 未连通的企业
        assertNotEquals(dsu.find("阿里巴巴"), dsu.find("腾讯科技"));
    }
}
