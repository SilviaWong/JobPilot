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
    public Result<List<Object>> getQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String domain) {

        Map<String, Object> res = questionService.getQuestions(page, pageSize, keyword, domain);
        List<Object> data = (List<Object>) res.get("data");
        Long total = (Long) res.get("total");
        Boolean hasMore = (Boolean) res.get("hasMore");

        return Result.success(data, total, hasMore);
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
}