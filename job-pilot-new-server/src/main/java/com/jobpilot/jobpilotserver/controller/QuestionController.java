package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.StandardQuestion;
import com.jobpilot.jobpilotserver.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    private final QuestionService questionService;

    @GetMapping
    public Result<List<StandardQuestion>> getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false, defaultValue = "all") String tier,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "hotness") String sortBy) {

        Map<String, Object> res = questionService.getQuestions(page, pageSize, keyword, domain, tier, tag, sortBy);
        List<StandardQuestion> data = (List<StandardQuestion>) res.get("data");
        Long total = (Long) res.get("total");
        Boolean hasMore = (Boolean) res.get("hasMore");
        Object meta = res.get("meta");

        return Result.success(data, total, hasMore, meta);
    }

    public Result<List<StandardQuestion>> getQuestions(int page, int pageSize, String keyword, String domain) {
        return getQuestions(page, pageSize, keyword, domain, "all", null, "hotness");
    }

    @PostMapping
    public Result<StandardQuestion> createQuestion(@RequestBody StandardQuestion question) {
        StandardQuestion created = questionService.createQuestion(question);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<StandardQuestion> updateQuestion(@PathVariable String id, @RequestBody StandardQuestion question) {
        StandardQuestion updated = questionService.updateQuestion(id, question);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable String id) {
        boolean ok = questionService.deleteQuestion(id);
        if (ok) return Result.success(null);
        return Result.error("删除失败");
    }

    @PostMapping("/ai-answer")
    public Result<String> generateAiAnswer(@RequestBody Map<String, String> body) {
        String title = body.get("title");
        String corePoints = body.get("corePoints");
        String answer = questionService.generateAiAnswer(title, corePoints);
        return Result.success(answer);
    }

    @PostMapping("/batch-ai-answer")
    public Result<Map<String, Object>> batchAiAnswer() {
        Map<String, Object> res = questionService.batchAiAnswer();
        Result<Map<String, Object>> result = Result.success(res);
        if (res.get("count") instanceof Integer) {
            result.setCount((Integer) res.get("count"));
        }
        return result;
    }
}