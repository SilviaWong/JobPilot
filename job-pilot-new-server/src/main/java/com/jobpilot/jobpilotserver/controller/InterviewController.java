package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.service.InterviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public Result<Object> getInterviews(
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "50") int pageSize) {
        Map<String, Object> res = interviewService.getInterviews(jobId, status, search, page, pageSize);
        return Result.success(res.get("data"), res.get("stats"));
    }

    @PostMapping
    public Result<Interview> createInterview(@RequestBody Interview interview) {
        Interview created = interviewService.createInterview(interview);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Interview> updateInterview(@PathVariable String id, @RequestBody Interview interview) {
        Interview updated = interviewService.updateInterview(id, interview);
        return Result.success(updated);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable String id, @RequestParam String status) {
        boolean success = interviewService.updateStatus(id, status);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteInterview(@PathVariable String id) {
        interviewService.deleteInterview(id);
        return Result.success(null);
    }

    @GetMapping(value = "/{id}/ics", produces = "text/calendar; charset=utf-8")
    public ResponseEntity<String> getInterviewIcs(@PathVariable String id) {
        String icsContent = interviewService.generateIcs(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=utf-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"interview-" + id + ".ics\"")
                .body(icsContent);
    }

    @GetMapping(value = "/export-all-ics", produces = "text/calendar; charset=utf-8")
    public ResponseEntity<String> exportAllIcs() {
        String icsContent = interviewService.exportAllIcs();
        String filename = "all-upcoming-interviews-" + LocalDate.now() + ".ics";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=utf-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(icsContent);
    }

    @PostMapping("/{id}/sync-to-questions")
    public Result<Map<String, Object>> syncToQuestions(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> res = interviewService.syncToQuestions(id, body);
        return Result.success(res);
    }
}
