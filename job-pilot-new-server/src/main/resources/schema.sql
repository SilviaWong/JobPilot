-- CreateTable: Job (职位主表)
CREATE TABLE IF NOT EXISTS "Job" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobId" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "companyName" TEXT NOT NULL,
    "companyFullName" TEXT,
    "companyId" TEXT,
    "salary" TEXT NOT NULL,
    "location" TEXT NOT NULL,
    "education" TEXT,
    "experience" TEXT,
    "city" TEXT,
    "area" TEXT,
    "businessDistrict" TEXT,
    "jobUrl" TEXT,
    "jobDesc" TEXT,
    "skills" TEXT,
    "welfareList" TEXT,
    "hrName" TEXT,
    "hrPosition" TEXT,
    "publishDate" TEXT,
    "updateDate" TEXT,
    "isHeadhunter" BOOLEAN NOT NULL DEFAULT false,
    "clientCompanyName" TEXT,
    "platform" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'normal',
    "rawData" TEXT NOT NULL,
    "rawData2" TEXT,
    "dataSource" TEXT NOT NULL DEFAULT 'unknown',
    "isFavorited" BOOLEAN NOT NULL DEFAULT false,
    "isHidden" BOOLEAN NOT NULL DEFAULT false,
    "tags" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: AiJobResult (独立 AI 分析诊断结果表)
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

-- CreateTable: Company (企业信息表)
CREATE TABLE IF NOT EXISTS "Company" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "companyName" TEXT NOT NULL,
    "companyFullName" TEXT,
    "companyId" TEXT,
    "sourcePlatform" TEXT NOT NULL,
    "industry" TEXT,
    "scale" TEXT,
    "stage" TEXT,
    "companyDesc" TEXT,
    "welfare" TEXT,
    "rawData" TEXT,
    "rawData2" TEXT,
    "isAgency" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: Interview (面试跟踪记录表)
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

-- CreateTable: BlacklistedCompany (黑名单企业表)
CREATE TABLE IF NOT EXISTS "BlacklistedCompany" (
    "companyName" TEXT NOT NULL PRIMARY KEY,
    "reason" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable: CompanyAlias (企业并查集合并别名表)
CREATE TABLE IF NOT EXISTS "CompanyAlias" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "aliasName" TEXT NOT NULL,
    "canonicalName" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- CreateTable: StandardQuestion (标准面试题库表)
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

-- CreateTable: QuestionVariant (面试题变体问法表)
CREATE TABLE IF NOT EXISTS "QuestionVariant" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "standardQuestionId" TEXT NOT NULL,
    "serialNo" INTEGER,
    "title" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL,
    CONSTRAINT "QuestionVariant_standardQuestionId_fkey" FOREIGN KEY ("standardQuestionId") REFERENCES "StandardQuestion" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

-- CreateTable: AiSettings (大模型配置表)
CREATE TABLE IF NOT EXISTS "AiSettings" (
    "id" TEXT NOT NULL PRIMARY KEY DEFAULT 'default',
    "activeProfileId" TEXT NOT NULL,
    "profiles" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: Resume (简历管理表)
CREATE TABLE IF NOT EXISTS "Resume" (
    "id" TEXT NOT NULL PRIMARY KEY DEFAULT 'default',
    "resume" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: ZhilianEnrichmentCache (智联数据缓存表)
CREATE TABLE IF NOT EXISTS "ZhilianEnrichmentCache" (
    "jobId" TEXT NOT NULL PRIMARY KEY,
    "rawData" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: JobDetail (跨平台职位详情缓存扩展表，统一替代 boss_single_details)
CREATE TABLE IF NOT EXISTS "JobDetail" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "jobId" TEXT NOT NULL,
    "platform" TEXT NOT NULL,
    "rawData" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateTable: AiSkill (自定义 Agent AI 技能库表)
CREATE TABLE IF NOT EXISTS "AiSkill" (
    "id" TEXT NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "systemPrompt" TEXT NOT NULL,
    "userPrompt" TEXT NOT NULL,
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" DATETIME NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX IF NOT EXISTS "Job_jobId_platform_key" ON "Job"("jobId", "platform");
CREATE UNIQUE INDEX IF NOT EXISTS "Company_companyName_sourcePlatform_companyId_key" ON "Company"("companyName", "sourcePlatform", "companyId");
CREATE UNIQUE INDEX IF NOT EXISTS "JobDetail_jobId_platform_key" ON "JobDetail"("jobId", "platform");
