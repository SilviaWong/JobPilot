package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.service.BlacklistService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/blacklist")
public class BlacklistController {

    private final BlacklistService blacklistService;

    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @GetMapping
    public Result<Object> getBlacklist(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "all") String source,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "50") int pageSize) {

        Map<String, Object> res = blacklistService.getBlacklist(keyword, source, page, pageSize);
        Result<Object> result = Result.success(res.get("data"), res.get("stats"));
        if (res.get("total") != null) {
            result.setTotal(((Number) res.get("total")).longValue());
        }
        return result;
    }

    @PostMapping
    public Result<Object> addBlacklist(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = blacklistService.addBlacklist(body);
        Result<Object> result = Result.success(res.get("data"));
        result.setMessage((String) res.get("message"));
        return result;
    }

    @DeleteMapping
    public Result<Boolean> removeBlacklist(
            @RequestParam(required = false) String companyName,
            @RequestBody(required = false) Map<String, Object> body) {

        String target = companyName;
        if (target == null || target.trim().isEmpty()) {
            if (body != null && body.get("companyName") != null) {
                target = (String) body.get("companyName");
            }
        }

        if (target == null || target.trim().isEmpty()) {
            return Result.error("请提供要移出黑名单的公司名称");
        }

        boolean success = blacklistService.removeBlacklist(target);
        Result<Boolean> result = Result.success(success);
        result.setMessage(String.format("已将【%s】移出黑名单", target));
        return result;
    }
}

