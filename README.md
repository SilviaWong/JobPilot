# 🚀 JobPilot - 智能求职看板与数据管理系统

<p align="center">
  <b>一站式跨平台求职数据清洗、可视化看板管理与 AI 智能诊断系统</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg" alt="Spring Boot 3.2.5" />
  <img src="https://img.shields.io/badge/Java-21-blue.svg" alt="Java 21" />
  <img src="https://img.shields.io/badge/Nuxt-4.5.1-green.svg" alt="Nuxt 4" />
  <img src="https://img.shields.io/badge/Vue-3.5-brightgreen.svg" alt="Vue 3" />
  <img src="https://img.shields.io/badge/MyBatis--Plus-3.5.6-blue.svg" alt="MyBatis-Plus" />
  <img src="https://img.shields.io/badge/SQLite-3.45-lightgrey.svg" alt="SQLite 3" />
</p>

---

## 📌 简介

**JobPilot** 是一款专为求职者打造的个人智能求职数据中心与看板管理系统。

在日常求职中，求职者往往需要在 **Boss直聘**、**前程无忧 51job**、**智联招聘**、**猎聘** 等多个平台间频繁切换，面临**数据分散**、**薪资计算不统一**、**缺乏企业黑名单管控**以及**缺乏个性化 AI 辅助**等痛点。JobPilot 结合 Chrome 浏览器抓取插件与前后端分离架构，实现了跨平台职位数据的自动化归一化清洗、阶段可视化看板、企业别名合并、数据大屏统计及 LLM 大模型求职诊断。

---

## ✨ 核心特性

- 🌐 **跨平台数据聚合与清洗 (Data Normalization)**
  - 接入浏览器插件，全量/增量推送数据至系统（`/api/sync-all`）。
  - 使用**策略模式 (Strategy Pattern)** 标准化解析不同平台的职位详情。
  - 自动折算跨平台薪资（如 `15-25K`、`2-3万`、`400-500元/天`）为统一的 `k/月`，支持高精度的薪资范围筛选。

- 📋 **智能职位大厅与交互抽屉 (`/jobs`)**
  - 多维筛选：按关键词、薪资区间、招聘平台、求职状态（沟通中/已投递/面试中/已淘汰/已失效）、隐藏/黑名单进行精准过滤。
  - 物理列优化：关键展示字段（经验、学历、城市、HR信息、技能/福利标签）直接物理列存储，百万级记录极致毫秒响应。
  - 详情抽屉：支持完整 JD 浏览、匹配度诊断、一键生成针对 HR 的打招呼语与岗位高频面试题预测。

- 📊 **可视化求职看板与面试跟踪 (`/kanban`)**
  - **阶段看板**：按照 `沟通中` ➔ `已投递` ➔ `面试中` ➔ `已淘汰` ➔ `已失效` 阶段可视化拖拽管理。
  - **面试全流程记录**：记录每轮面试（一面/二面/HR面）的时间、形式（线上/现场）、面试备注及复盘总结（Debrief）。

- 🏢 **企业全景与黑名单/别名合并 (`/companies`)**
  - **并查集 (DSU) 别名合并**：支持将同一集团的不同子公司或别名（如“xx科技”、“xx技术”）聚合归并至标准企业名称。
  - **黑名单管控**：一键将劣质/外包企业加入黑名单并填写原因，支持基于 Caffeine 本地缓存的高效自动过滤。

- 📈 **数据大屏与趋势统计 (`/dashboard`)**
  - 薪资区间分布柱状图。
  - 经验要求与学历要求占比饼图。
  - 基于 `echarts-wordcloud` 的热门技术栈与技能词云图。

- 🤖 **大模型与资产管理 (`/ai/*` & `/questions`)**
  - **模型配置 (`/ai/model`)**：配置 API Key、Base URL 与 Model 名称，支持即时连通性测试。
  - **简历资产 (`/ai/resume`)**：维护 Markdown 格式的个人履历，独立解耦存储。
  - **AI 技能库 (`/ai/skills`)**：支持自定义 Agent 技能 Prompt。
  - **标准题库 (`/questions`)**：收集并管理高频面试题与 AI 参考解答。

---

## 🛠️ 技术选型

### 后端 (`job-pilot-new-server`)
- **JDK / 语言**：Java 21
- **核心框架**：Spring Boot 3.2.5 + Spring MVC
- **ORM & 数据库**：MyBatis-Plus 3.5.6 + SQLite 3.45 (`sqlite-jdbc` + `sqlite-dialect`)
- **本地缓存**：Caffeine Cache（黑名单高效缓存）
- **核心设计模式/算法**：策略模式（平台提取器）、并查集（Disjoint Set Union 企业合并）、正则清洗引擎

### 前端 (`job-pilot-web`)
- **核心框架**：Nuxt 4.5.1 / Vue 3.5
- **UI 组件库**：Element Plus + `@element-plus/nuxt`
- **图标库**：Lucide Icons (`lucide-vue-next`)
- **可视化图表**：ECharts 5.5 + `vue-echarts` + `echarts-wordcloud`
- **构建工具**：Vite

---

## 📁 目录结构

```text
JobPilot/
├── REQUIREMENTS.md                     # PRD 需求规格说明文档
├── job-pilot-new-server/               # Spring Boot 后端项目
│   ├── src/main/java/com/jobpilot/jobpilotserver/
│   │   ├── common/                     # 通用响应对象与全局配置
│   │   ├── controller/                 # RESTful API 控制层
│   │   ├── mapper/                     # MyBatis-Plus Mapper 映射层
│   │   ├── model/
│   │   │   ├── entity/                 # 数据库实体类 (Job, Company, Resume, AiSettings 等)
│   │   │   └── vo/                     # 视图对象 (NormalizedJobDataVO, JobDetailVO 等)
│   │   ├── service/                    # 业务逻辑接口及实现类
│   │   ├── strategy/
│   │   │   ├── algorithm/              # CompanyDisjointSet (并查集算法)
│   │   │   ├── normalizer/             # 平台 JobNormalizer 策略模式
│   │   │   └── sync/                   # 多线程并发提取与清洗 Pipeline
│   │   └── util/                       # SalaryParser (薪资解析器) 等工具类
│   └── src/main/resources/
│       ├── schema.sql                  # 数据库表结构初始化脚本
│       └── migration_alter_tables.sql  # 增量数据库表结构迁移 DDL
└── job-pilot-web/                      # Nuxt 4 前端项目
    ├── assets/                         # 静态样式与资源
    ├── components/                     # Vue 共享组件 (JobDetailDrawer 等)
    ├── layouts/                        # 页面布局框架
    ├── pages/                          # 应用路由页面
    │   ├── jobs/                       # 职位大厅
    │   ├── kanban/                     # 投递看板
    │   ├── companies/                  # 企业与黑名单管理
    │   ├── dashboard/                  # 数据大盘
    │   ├── ai/                         # 模型、简历与 AI 技能
    │   └── questions/                  # 面试题库
    └── nuxt.config.ts                  # Nuxt 配置文件
```

---

## 🚀 快速开始

### 前置要求
- **JDK 21** 及以上
- **Node.js 18+** / pnpm / npm / yarn
- **Maven 3.8+**

---

### 1. 后端启动 (`job-pilot-new-server`)

```bash
cd job-pilot-new-server

# 编译项目
mvn clean compile

# 启动 Spring Boot 后端服务 (默认端口 8080)
mvn spring-boot:run
```

- 后端服务默认使用内置 SQLite 数据库，无须额外安装和配置外部数据库。
- 首次启动会自动读取 `src/main/resources/schema.sql` 完成数据表创建。

---

### 2. 前端启动 (`job-pilot-web`)

```bash
cd job-pilot-web

# 安装依赖
npm install

# 启动开发服务器 (默认端口 3000)
npm run dev
```

打开浏览器访问 `http://localhost:3000` 即可进入 JobPilot 智能求职看板。

---

## 🔄 数据库迁移与历史数据回填

如果您升级了系统或使用了旧版的数据库文件，可进行增量物理列升级与历史数据批量清洗回填：

### 1. 执行物理表结构 DDL 迁移
在 SQLite 数据库客户端中执行 [migration_alter_tables.sql](file:///Users/wangyanan/Projects2/JobPilot/job-pilot-new-server/src/main/resources/migration_alter_tables.sql)：
```sql
-- 增加 Job 与 Company 表新增的 19 个展示字段物理列
ALTER TABLE "Job" ADD COLUMN "experience" TEXT;
...
```

### 2. 触发历史数据物理列全量清洗回填
启动后端服务后，发送以下请求触发清洗回填：
```bash
curl -X POST http://localhost:8080/api/admin/backfill
```

**返回响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "processedJobs": 120,
    "updatedJobs": 120,
    "processedCompanies": 45,
    "updatedCompanies": 45
  }
}
```

---

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源。
