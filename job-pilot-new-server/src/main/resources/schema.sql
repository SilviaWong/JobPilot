-- =================================================================
-- JobPilot 数据库初始化 DDL 脚本 (SQLite)
-- 专属于 JobPilot 的独立数据库表结构与高性能全景索引定义
-- =================================================================

PRAGMA foreign_keys = ON;

-- 1. 职位核心主表
CREATE TABLE IF NOT EXISTS "Job" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "companyName" TEXT NOT NULL,
    "companyFullName" TEXT,
    "companyId" TEXT,
    "salary" TEXT NOT NULL,
    "salaryMin" REAL,
    "salaryMax" REAL,
    "salaryAvg" REAL,
    "salaryMonths" INTEGER DEFAULT 12,
    "location" TEXT NOT NULL,
    "province" TEXT,
    "city" TEXT,
    "cityCode" TEXT,
    "area" TEXT,
    "businessDistrict" TEXT,
    "address" TEXT,
    "education" TEXT,
    "experience" TEXT,
    "expMinYears" INTEGER DEFAULT 0,
    "expMaxYears" INTEGER,
    "jobUrl" TEXT,
    "jobDesc" TEXT,
    "skills" TEXT,
    "welfareList" TEXT,
    "hrName" TEXT,
    "hrPosition" TEXT,
    "hrActiveStatus" TEXT,
    "hrActiveLevel" TEXT,
    "platformPublishTime" TEXT,
    "platformUpdateTime" TEXT,
    "isHeadhunter" BOOLEAN NOT NULL DEFAULT 0,
    "clientCompanyName" TEXT,
    "clientIndustry" TEXT,
    "clientScale" TEXT,
    "clientStage" TEXT,
    "platform" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'normal',
    "applyStatus" TEXT DEFAULT 'none',
    "appliedAt" DATETIME,
    "statusUpdatedAt" DATETIME,
    "firstSeen" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lastSeen" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "descHash" TEXT,
    "rawData" TEXT NOT NULL,
    "rawData2" TEXT,
    "dataSource" TEXT NOT NULL DEFAULT 'unknown',
    "isFavorited" BOOLEAN NOT NULL DEFAULT 0,
    "isHidden" BOOLEAN NOT NULL DEFAULT 0,
    "tags" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- 职位主表高性能优化索引
CREATE UNIQUE INDEX IF NOT EXISTS "Job_jobId_platform_key" ON "Job"("jobId", "platform");
CREATE INDEX IF NOT EXISTS "Job_companyName_idx" ON "Job"("companyName");
CREATE INDEX IF NOT EXISTS "Job_title_idx" ON "Job"("title");
CREATE INDEX IF NOT EXISTS "Job_city_idx" ON "Job"("city");
CREATE INDEX IF NOT EXISTS "Job_cityCode_idx" ON "Job"("cityCode");
CREATE INDEX IF NOT EXISTS "Job_salaryAvg_idx" ON "Job"("salaryAvg");
CREATE INDEX IF NOT EXISTS "Job_status_isHidden_isFavorited_idx" ON "Job"("status", "isHidden", "isFavorited");
CREATE INDEX IF NOT EXISTS "Job_applyStatus_idx" ON "Job"("applyStatus");
CREATE INDEX IF NOT EXISTS "Job_hrActiveLevel_idx" ON "Job"("hrActiveLevel");
CREATE INDEX IF NOT EXISTS "Job_isHeadhunter_idx" ON "Job"("isHeadhunter");
CREATE INDEX IF NOT EXISTS "Job_clientCompanyName_idx" ON "Job"("clientCompanyName");
CREATE INDEX IF NOT EXISTS "Job_updatedAt_idx" ON "Job"("updatedAt");
CREATE INDEX IF NOT EXISTS "Job_firstSeen_idx" ON "Job"("firstSeen");
CREATE INDEX IF NOT EXISTS "Job_lastSeen_idx" ON "Job"("lastSeen");

-- 2. 职位深度详情扩展表 (对齐 JobDetail.java，消除多余冷表冗余)
CREATE TABLE IF NOT EXISTS "job_details" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobId" TEXT NOT NULL,
    "platform" TEXT NOT NULL,
    "rawData" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS "job_details_jobId_platform_key" ON "job_details"("jobId", "platform");

-- 3. 企业全景画像表
CREATE TABLE IF NOT EXISTS "Company" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "companyName" TEXT NOT NULL,
    "companyFullName" TEXT,
    "companyId" TEXT,
    "sourcePlatform" TEXT NOT NULL,
    "industry" TEXT,
    "scale" TEXT,
    "stage" TEXT,
    "companyType" TEXT,
    "creditCode" TEXT,
    "logo" TEXT,
    "companyDesc" TEXT,
    "welfare" TEXT,
    "welfareList" TEXT,
    "rawData" TEXT,
    "rawData2" TEXT,
    "rawData3" TEXT,
    "isAgency" BOOLEAN NOT NULL DEFAULT 0,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS "Company_companyName_sourcePlatform_companyId_key" ON "Company"("companyName", "sourcePlatform", "companyId");
CREATE INDEX IF NOT EXISTS "Company_companyName_idx" ON "Company"("companyName");
CREATE INDEX IF NOT EXISTS "Company_companyFullName_idx" ON "Company"("companyFullName");
CREATE INDEX IF NOT EXISTS "Company_industry_idx" ON "Company"("industry");
CREATE INDEX IF NOT EXISTS "Company_scale_idx" ON "Company"("scale");
CREATE INDEX IF NOT EXISTS "Company_stage_idx" ON "Company"("stage");
CREATE INDEX IF NOT EXISTS "Company_creditCode_idx" ON "Company"("creditCode");

-- 4. 独立 AI 分析诊断结果表
CREATE TABLE IF NOT EXISTS "AiJobResult" (
    "jobId" TEXT NOT NULL PRIMARY KEY,
    "score" INTEGER,
    "matchLevel" TEXT,
    "resultText" TEXT,
    "intro" TEXT,
    "predictedQuestions" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS "AiJobResult_matchLevel_idx" ON "AiJobResult"("matchLevel");
CREATE INDEX IF NOT EXISTS "AiJobResult_score_idx" ON "AiJobResult"("score");

-- 5. 面试跟踪记录表 (级联删除)
CREATE TABLE IF NOT EXISTS "Interview" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobId" TEXT NOT NULL,
    "time" DATETIME NOT NULL,
    "type" TEXT NOT NULL,
    "round" TEXT NOT NULL,
    "note" TEXT,
    "debrief" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "Interview_jobId_fkey" FOREIGN KEY ("jobId") REFERENCES "Job" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS "Interview_jobId_idx" ON "Interview"("jobId");
CREATE INDEX IF NOT EXISTS "Interview_time_idx" ON "Interview"("time");

-- 6. 求职看板状态流转日志表 (级联删除)
CREATE TABLE IF NOT EXISTS "job_status_logs" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobRecordId" TEXT NOT NULL,
    "fromStatus" TEXT NOT NULL,
    "toStatus" TEXT NOT NULL,
    "note" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "job_status_logs_jobRecordId_fkey" FOREIGN KEY ("jobRecordId") REFERENCES "Job" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS "job_status_logs_jobRecordId_idx" ON "job_status_logs"("jobRecordId");
CREATE INDEX IF NOT EXISTS "job_status_logs_toStatus_createdAt_idx" ON "job_status_logs"("toStatus", "createdAt");

-- 7. 多版本简历画像表
CREATE TABLE IF NOT EXISTS "resume_profiles" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "targetRole" TEXT,
    "tags" TEXT,
    "content" TEXT NOT NULL,
    "isDefault" BOOLEAN NOT NULL DEFAULT 0,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS "resume_profiles_isDefault_idx" ON "resume_profiles"("isDefault");

-- 8. 企业黑名单表
CREATE TABLE IF NOT EXISTS "BlacklistedCompany" (
    "companyName" TEXT NOT NULL PRIMARY KEY,
    "reason" TEXT,
    "source" TEXT DEFAULT 'manual',
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. 企业并查集合并别名表
CREATE TABLE IF NOT EXISTS "CompanyAlias" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "aliasName" TEXT NOT NULL,
    "canonicalName" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS "CompanyAlias_aliasName_key" ON "CompanyAlias"("aliasName");
CREATE INDEX IF NOT EXISTS "CompanyAlias_canonicalName_idx" ON "CompanyAlias"("canonicalName");

-- 10. 标准面试题库表
CREATE TABLE IF NOT EXISTS "StandardQuestion" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "domain" TEXT,
    "categoryTags" TEXT,
    "corePoints" TEXT,
    "answerKey" TEXT,
    "aiAnswer" TEXT,
    "tags" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS "StandardQuestion_domain_idx" ON "StandardQuestion"("domain");
CREATE INDEX IF NOT EXISTS "StandardQuestion_title_idx" ON "StandardQuestion"("title");

-- 11. 面试题变体问法表
CREATE TABLE IF NOT EXISTS "QuestionVariant" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "standardQuestionId" TEXT NOT NULL,
    "serialNo" INTEGER,
    "title" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "QuestionVariant_standardQuestionId_fkey" FOREIGN KEY ("standardQuestionId") REFERENCES "StandardQuestion" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS "QuestionVariant_standardQuestionId_idx" ON "QuestionVariant"("standardQuestionId");

-- 12. 大模型配置表
CREATE TABLE IF NOT EXISTS "AiSettings" (
    "id" TEXT NOT NULL PRIMARY KEY DEFAULT 'default',
    "activeProfileId" TEXT NOT NULL,
    "profiles" TEXT NOT NULL,
    "resume" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- 13. AI Agent 技能配置表
CREATE TABLE IF NOT EXISTS "AiSkill" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "systemPrompt" TEXT NOT NULL,
    "userPrompt" TEXT NOT NULL,
    "isActive" BOOLEAN NOT NULL DEFAULT 1,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- 14. 智联招聘富化缓存表
CREATE TABLE IF NOT EXISTS "ZhilianEnrichmentCache" (
    "jobId" TEXT NOT NULL PRIMARY KEY,
    "rawData" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);
