package com.jobpilot.jobpilotserver.controller;

import com.jobpilot.jobpilotserver.common.result.Result;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import com.jobpilot.jobpilotserver.model.entity.Company;
import com.jobpilot.jobpilotserver.service.CompanyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public Result<List<Object>> getCompanies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String query) {

        Map<String, Object> res = companyService.getCompanies(page, pageSize, query);
        List<Object> data = (List<Object>) res.get("data");
        Integer total = (Integer) res.get("total");
        return Result.success(data, total != null ? total.longValue() : 0L, null);
    }

    @GetMapping("/all")
    public Result<List<Company>> getAllCompanies() {
        return Result.success(companyService.getAllCompanies());
    }

    @GetMapping("/blacklist")
    public Result<List<BlacklistedCompany>> getBlacklist() {
        return Result.success(companyService.getBlacklist());
    }

    @PostMapping("/blacklist")
    public Result<Boolean> addBlacklist(@RequestBody Map<String, String> body) {
        String companyName = body.get("companyName");
        String reason = body.get("reason");
        return Result.success(companyService.addBlacklist(companyName, reason));
    }

    @DeleteMapping("/blacklist/{companyName}")
    public Result<Boolean> removeBlacklist(@PathVariable String companyName) {
        return Result.success(companyService.removeBlacklist(companyName));
    }

    @PostMapping("/merge")
    public Result<Boolean> mergeCompanies(@RequestBody Map<String, Object> body) {
        String targetName = (String) body.get("targetName");
        List<String> sourceNames = (List<String>) body.get("sourceNames");
        return Result.success(companyService.mergeCompanies(targetName, sourceNames));
    }
}
