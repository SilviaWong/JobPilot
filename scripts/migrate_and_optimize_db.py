#!/usr/bin/env python3
"""
JobPilot 数据库拆分与结构重构优化脚本
1. 将数据库从旧项目 job-dashboard-local 中完全独立拆分至 JobPilot/data/jobpilot.db
2. 规范化表结构与命名，消除冗余表 (JobDetail 与 job_details 重复，废弃的 Resume 表，多余的 job_detail_payloads)
3. 将 jobDesc 与 jobUrl 原生并入 Job 主表，建立全景索引与外键约束
4. 迁移旧库所有数据并做完整性校验、执行 VACUUM 释放碎片存储
"""

import os
import sys
import sqlite3
import time

SOURCE_DB = "/Users/wangyanan/Projects2/job-dashboard-local/prisma/dev.db"
TARGET_DIR = "/Users/wangyanan/Projects2/JobPilot/data"
TARGET_DB = os.path.join(TARGET_DIR, "jobpilot.db")

DDL_SCHEMA = """
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

-- 职位表优化索引
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

-- 2. 职位深度详情扩展表 (替代旧系统冗余的 JobDetail / job_details / job_detail_payloads)
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

-- 5. 面试跟踪记录表 (外键约束级联删除)
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

-- 6. 求职看板状态流转日志表
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
"""

def migrate():
    print("=" * 65)
    print("🚀 开始 JobPilot 数据库拆分与结构重构迁移")
    print(f"源数据库: {SOURCE_DB}")
    print(f"目标数据库: {TARGET_DB}")
    print("=" * 65)

    if not os.path.exists(SOURCE_DB):
        print(f"❌ 错误: 源数据库不存在: {SOURCE_DB}")
        sys.exit(1)

    os.makedirs(TARGET_DIR, exist_ok=True)

    # 如果已存在旧的目标数据库，先备份
    if os.path.exists(TARGET_DB):
        backup_path = TARGET_DB + f".bak_{int(time.time())}"
        print(f"📦 备份已有目标数据库至: {backup_path}")
        os.rename(TARGET_DB, backup_path)

    # 1. 初始化目标数据库结构
    print("🔧 步骤 1/4: 创建独立优化表结构与高性能索引...")
    target_conn = sqlite3.connect(TARGET_DB)
    target_cur = target_conn.cursor()
    target_cur.executescript(DDL_SCHEMA)
    target_conn.commit()

    # 2. 连接源数据库进行只读提取
    print("📥 步骤 2/4: 连接源库并执行数据迁移与规范化聚合...")
    source_conn = sqlite3.connect(f"file:{SOURCE_DB}?mode=ro", uri=True)
    source_cur = source_conn.cursor()

    # 2.1 迁移 Job 表 (原生回填 jobDesc 与 jobUrl)
    print("  -> 迁移 Job 主表 (从 job_detail_payloads 深度并入 jobDesc 与 jobUrl)...")
    job_query = """
    SELECT 
        j.id, j.jobId, j.title, j.companyName, j.companyFullName, j.companyId,
        j.salary, j.salaryMin, j.salaryMax, j.salaryAvg, COALESCE(j.salaryMonths, 12),
        j.location, j.province, j.city, j.cityCode, j.area, j.businessDistrict, j.address,
        j.education, j.experience, COALESCE(j.expMinYears, 0), j.expMaxYears,
        COALESCE(p.jobUrl, '') as jobUrl,
        COALESCE(p.jobDesc, '') as jobDesc,
        j.skills, j.welfareList, j.hrName, j.hrPosition,
        j.hrActiveStatus, j.hrActiveLevel,
        j.platformPublishTime, j.platformUpdateTime,
        COALESCE(j.isHeadhunter, 0),
        j.clientCompanyName, j.clientIndustry, j.clientScale, j.clientStage,
        j.platform, COALESCE(j.status, 'normal'), COALESCE(j.applyStatus, 'none'),
        j.appliedAt, j.statusUpdatedAt,
        j.firstSeen, j.lastSeen, j.descHash,
        j.rawData, j.rawData2, COALESCE(j.dataSource, 'unknown'),
        COALESCE(j.isFavorited, 0), COALESCE(j.isHidden, 0), j.tags,
        j.createdAt, j.updatedAt
    FROM Job j
    LEFT JOIN job_detail_payloads p ON j.id = p.jobRecordId
    """
    source_cur.execute(job_query)
    jobs = source_cur.fetchall()
    
    insert_job_sql = """
    INSERT INTO "Job" (
        "id", "jobId", "title", "companyName", "companyFullName", "companyId",
        "salary", "salaryMin", "salaryMax", "salaryAvg", "salaryMonths",
        "location", "province", "city", "cityCode", "area", "businessDistrict", "address",
        "education", "experience", "expMinYears", "expMaxYears",
        "jobUrl", "jobDesc", "skills", "welfareList", "hrName", "hrPosition",
        "hrActiveStatus", "hrActiveLevel",
        "platformPublishTime", "platformUpdateTime",
        "isHeadhunter", "clientCompanyName", "clientIndustry", "clientScale", "clientStage",
        "platform", "status", "applyStatus", "appliedAt", "statusUpdatedAt",
        "firstSeen", "lastSeen", "descHash",
        "rawData", "rawData2", "dataSource", "isFavorited", "isHidden", "tags",
        "createdAt", "updatedAt"
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """
    target_cur.executemany(insert_job_sql, jobs)
    target_conn.commit()
    print(f"     ✅ Job 迁移完成: {len(jobs)} 条记录")

    # 2.2 迁移 Company 表
    print("  -> 迁移 Company 企业全景表...")
    source_cur.execute("""
    SELECT 
        id, companyName, companyFullName, companyId, sourcePlatform,
        industry, scale, stage, companyType, creditCode, logo,
        companyDesc, welfare, welfareList, rawData, rawData2, rawData3,
        COALESCE(isAgency, 0), createdAt, updatedAt
    FROM Company
    """)
    companies = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "Company" (
        id, companyName, companyFullName, companyId, sourcePlatform,
        industry, scale, stage, companyType, creditCode, logo,
        companyDesc, welfare, welfareList, rawData, rawData2, rawData3,
        isAgency, createdAt, updatedAt
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, companies)
    target_conn.commit()
    print(f"     ✅ Company 迁移完成: {len(companies)} 条记录")

    # 2.3 迁移 job_details 表 (规范统一使用 job_details，去除重复的 JobDetail)
    print("  -> 迁移 job_details 详情表...")
    source_cur.execute("SELECT id, jobId, platform, rawData, createdAt, updatedAt FROM job_details")
    details = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "job_details" (id, jobId, platform, rawData, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?)
    """, details)
    target_conn.commit()
    print(f"     ✅ job_details 迁移完成: {len(details)} 条记录")

    # 2.4 迁移 AiJobResult 表
    print("  -> 迁移 AiJobResult AI诊断结果表...")
    source_cur.execute("SELECT jobId, score, matchLevel, resultText, intro, predictedQuestions, createdAt, updatedAt FROM AiJobResult")
    ai_results = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "AiJobResult" (jobId, score, matchLevel, resultText, intro, predictedQuestions, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, ai_results)
    target_conn.commit()
    print(f"     ✅ AiJobResult 迁移完成: {len(ai_results)} 条记录")

    # 2.5 迁移 StandardQuestion 表
    print("  -> 迁移 StandardQuestion 标准题库表...")
    source_cur.execute("SELECT id, title, domain, categoryTags, corePoints, answerKey, aiAnswer, tags, createdAt, updatedAt FROM StandardQuestion")
    std_questions = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "StandardQuestion" (id, title, domain, categoryTags, corePoints, answerKey, aiAnswer, tags, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, std_questions)
    target_conn.commit()
    print(f"     ✅ StandardQuestion 迁移完成: {len(std_questions)} 条记录")

    # 2.6 迁移 QuestionVariant 表
    print("  -> 迁移 QuestionVariant 变体问法表...")
    source_cur.execute("SELECT id, standardQuestionId, serialNo, title, createdAt, updatedAt FROM QuestionVariant")
    variants = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "QuestionVariant" (id, standardQuestionId, serialNo, title, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?)
    """, variants)
    target_conn.commit()
    print(f"     ✅ QuestionVariant 迁移完成: {len(variants)} 条记录")

    # 2.7 迁移 resume_profiles 表
    print("  -> 迁移 resume_profiles 多版本简历表...")
    source_cur.execute("SELECT id, title, targetRole, tags, content, COALESCE(isDefault, 0), createdAt, updatedAt FROM resume_profiles")
    resumes = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "resume_profiles" (id, title, targetRole, tags, content, isDefault, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, resumes)
    target_conn.commit()
    print(f"     ✅ resume_profiles 迁移完成: {len(resumes)} 条记录")

    # 2.8 迁移 AiSettings 表
    print("  -> 迁移 AiSettings 大模型配置表...")
    source_cur.execute("SELECT id, activeProfileId, profiles, resume, createdAt, updatedAt FROM AiSettings")
    ai_settings = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "AiSettings" (id, activeProfileId, profiles, resume, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?)
    """, ai_settings)
    target_conn.commit()
    print(f"     ✅ AiSettings 迁移完成: {len(ai_settings)} 条记录")

    # 2.9 迁移 AiSkill 表
    print("  -> 迁移 AiSkill 智能体技能库表...")
    source_cur.execute("SELECT id, name, description, systemPrompt, userPrompt, COALESCE(isActive, 1), createdAt, updatedAt FROM AiSkill")
    skills = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "AiSkill" (id, name, description, systemPrompt, userPrompt, isActive, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, skills)
    target_conn.commit()
    print(f"     ✅ AiSkill 迁移完成: {len(skills)} 条记录")

    # 2.10 迁移 ZhilianEnrichmentCache 表
    print("  -> 迁移 ZhilianEnrichmentCache 智联富化缓存表...")
    source_cur.execute("SELECT jobId, rawData, createdAt, updatedAt FROM ZhilianEnrichmentCache")
    zhilian_caches = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "ZhilianEnrichmentCache" (jobId, rawData, createdAt, updatedAt)
    VALUES (?, ?, ?, ?)
    """, zhilian_caches)
    target_conn.commit()
    print(f"     ✅ ZhilianEnrichmentCache 迁移完成: {len(zhilian_caches)} 条记录")

    # 2.11 迁移 BlacklistedCompany 与 CompanyAlias
    print("  -> 迁移 BlacklistedCompany & CompanyAlias...")
    source_cur.execute("SELECT companyName, reason, source, createdAt, updatedAt FROM BlacklistedCompany")
    blacklists = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "BlacklistedCompany" (companyName, reason, source, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?)
    """, blacklists)

    source_cur.execute("SELECT id, aliasName, canonicalName, createdAt FROM CompanyAlias")
    aliases = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "CompanyAlias" (id, aliasName, canonicalName, createdAt)
    VALUES (?, ?, ?, ?)
    """, aliases)

    # 2.12 迁移 Interview 与 job_status_logs
    print("  -> 迁移 Interview & job_status_logs...")
    source_cur.execute("SELECT id, jobId, time, type, round, note, debrief, createdAt, updatedAt FROM Interview")
    interviews = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "Interview" (id, jobId, time, type, round, note, debrief, createdAt, updatedAt)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, interviews)

    source_cur.execute("SELECT id, jobRecordId, fromStatus, toStatus, note, createdAt FROM job_status_logs")
    status_logs = source_cur.fetchall()
    target_cur.executemany("""
    INSERT INTO "job_status_logs" (id, jobRecordId, fromStatus, toStatus, note, createdAt)
    VALUES (?, ?, ?, ?, ?, ?)
    """, status_logs)
    target_conn.commit()

    source_conn.close()

    # 3. 数据校验与完整性检查
    print("🔍 步骤 3/4: 数据行数校验与完整性检查...")
    tables = [
        "Job", "Company", "job_details", "AiJobResult", "StandardQuestion",
        "QuestionVariant", "resume_profiles", "AiSettings", "AiSkill",
        "ZhilianEnrichmentCache", "BlacklistedCompany", "CompanyAlias",
        "Interview", "job_status_logs"
    ]
    for tbl in tables:
        target_cur.execute(f'SELECT count(*) FROM "{tbl}"')
        cnt = target_cur.fetchone()[0]
        print(f"     📊 表 [{tbl:24s}]: {cnt:7d} 行")

    target_cur.execute("PRAGMA integrity_check;")
    integrity = target_cur.fetchone()[0]
    print(f"     🛡️ 完整性检查: {integrity}")

    # 4. 优化与压缩存储 (VACUUM / ANALYZE)
    print("⚡ 步骤 4/4: 执行 VACUUM 与统计分析优化...")
    target_cur.execute("PRAGMA optimize;")
    target_cur.execute("ANALYZE;")
    target_conn.commit()
    target_conn.close()

    # 重新 VACUUM
    v_conn = sqlite3.connect(TARGET_DB)
    v_conn.execute("VACUUM;")
    v_conn.close()

    orig_size_mb = os.path.getsize(SOURCE_DB) / 1024 / 1024
    new_size_mb = os.path.getsize(TARGET_DB) / 1024 / 1024

    print("=" * 65)
    print("🎉 数据库拆分与优化迁移成功！")
    print(f"原数据库大小: {orig_size_mb:.2f} MB")
    print(f"新数据库大小: {new_size_mb:.2f} MB")
    print(f"瘦身比例: {(1 - new_size_mb / orig_size_mb) * 100:.1f}% 空间节省")
    print(f"新数据库位置: {TARGET_DB}")
    print("=" * 65)

if __name__ == "__main__":
    migrate()
