-- =================================================================
-- JobPilot 数据库 DDL 增量迁移脚本
-- 用于在已有 SQLite / H2 数据库中加入 Job 与 Company 表新增的页面展示字段
-- =================================================================

-- 1. Job 表增加页面展示字段
ALTER TABLE "Job" ADD COLUMN "experience" TEXT;
ALTER TABLE "Job" ADD COLUMN "city" TEXT;
ALTER TABLE "Job" ADD COLUMN "area" TEXT;
ALTER TABLE "Job" ADD COLUMN "businessDistrict" TEXT;
ALTER TABLE "Job" ADD COLUMN "jobUrl" TEXT;
ALTER TABLE "Job" ADD COLUMN "jobDesc" TEXT;
ALTER TABLE "Job" ADD COLUMN "skills" TEXT;
ALTER TABLE "Job" ADD COLUMN "welfareList" TEXT;
ALTER TABLE "Job" ADD COLUMN "hrName" TEXT;
ALTER TABLE "Job" ADD COLUMN "hrPosition" TEXT;
ALTER TABLE "Job" ADD COLUMN "publishDate" TEXT;
ALTER TABLE "Job" ADD COLUMN "updateDate" TEXT;
ALTER TABLE "Job" ADD COLUMN "isHeadhunter" BOOLEAN DEFAULT false;
ALTER TABLE "Job" ADD COLUMN "clientCompanyName" TEXT;

-- 2. Company 表增加公司基本信息与福利字段
ALTER TABLE "Company" ADD COLUMN "industry" TEXT;
ALTER TABLE "Company" ADD COLUMN "scale" TEXT;
ALTER TABLE "Company" ADD COLUMN "stage" TEXT;
ALTER TABLE "Company" ADD COLUMN "companyDesc" TEXT;
ALTER TABLE "Company" ADD COLUMN "welfare" TEXT;
