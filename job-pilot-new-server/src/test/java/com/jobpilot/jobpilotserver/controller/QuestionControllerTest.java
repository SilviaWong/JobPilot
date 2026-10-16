package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class QuestionControllerTest {

    @Autowired
    private QuestionController questionController;

    @Test
    public void testGetQuestionsWithMetaAndVariants() {
        Result<List<StandardQuestion>> result = questionController.getQuestions(1, 10, null, null, "all", null, "hotness");

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        assertFalse(result.getData().isEmpty(), "Question list should not be empty");
        assertTrue(result.getTotal() > 0, "Total questions should be > 0");

        // Verify meta
        assertNotNull(result.getMeta());
        assertTrue(result.getMeta() instanceof Map);
        Map<?, ?> meta = (Map<?, ?>) result.getMeta();
        assertTrue(meta.containsKey("tierStats"));
        assertTrue(meta.containsKey("domains"));

        Map<?, ?> tierStats = (Map<?, ?>) meta.get("tierStats");
        assertNotNull(tierStats.get("all"));
        assertNotNull(tierStats.get("t1"));
        assertNotNull(tierStats.get("t2"));
        assertNotNull(tierStats.get("t3"));

        List<?> domains = (List<?>) meta.get("domains");
        assertFalse(domains.isEmpty(), "Domains list should not be empty");

        // Verify variants on questions
        StandardQuestion first = result.getData().get(0);
        assertNotNull(first.getVariants(), "Variants list should be initialized on each question");
    }

    @Test
    public void testGetQuestionsWithTierFilter() {
        Result<List<StandardQuestion>> resultT1 = questionController.getQuestions(1, 10, null, null, "T1-必考", null, "hotness");
        assertNotNull(resultT1);
        assertTrue(resultT1.getSuccess());
        for (StandardQuestion q : resultT1.getData()) {
            assertTrue(q.getTags() != null && q.getTags().contains("T1-必考"));
        }
    }
}
