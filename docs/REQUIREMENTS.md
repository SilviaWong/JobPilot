# JobPilot - 智能求职看板与数据管理系统 (PRD 需求文档)

---

## 1. 项目背景与目标

### 1.1 项目背景
在当今求职市场中，求职者往往需要在多个主流招聘平台（如 Boss直聘、前程无忧 51job、智联招聘、猎聘等）之间频繁切换，面临以下痛点：
1. **数据分散**：无法在一个统一界面下查看和管理跨平台的投递意向与沟通记录。
2. **信息清洗困难**：各平台的薪资表示方式（如 `15-25K`、`2-3万`、`400-500元/天`）、职位标签、HR 活跃度差异巨大，难以对比。
3. **黑名单与重复公司管理缺失**：难以快捷屏蔽劣质企业或合并同系子公司。
4. **AI 辅助能力弱**：缺乏针对具体 JD 自动生成打招呼语、匹配度诊断、以及预估面试考题的能力。

### 1.2 项目目标
**JobPilot** 旨在通过前后端分离架构（Spring Boot 3 + Nuxt 4），打造一款个人专属的智能求职数据中心与看板系统。系统与 Chrome 抓取插件配合，支持多平台数据的自动化抓取、规范化清洗、可视化分析、黑名单管控以及基于 LLM 大模型的求职辅助。

---

## 2. 整体架构与技术选型

### 2.1 架构设计
系统采用标准的前后端分离架构：
- **前端 (`job-pilot-web`)**：Nuxt 4 / Vue 3 + Element Plus，负责响应式视图、可视化看板、交互抽屉及 HTTP 代理。
- **后端 (`job-pilot-new-server`)**：Spring Boot 3 + MyBatis-Plus + SQLite，负责业务逻辑、策略模式数据清洗、大模型客户端封装与数据持久化。
- **数据同步端**：通过 Chrome 扩展抓取 API，全量/增量向后端 `/api/sync-all` 提交数据。

### 2.2 技术栈明细
- **前端**：Nuxt 4, Vue 3, Element Plus, ECharts, vue-echarts, Lucide Icons, Vite
- **后端**：Java 17+, Spring Boot 3.4, MyBatis-Plus 3.5, SQLite 3
- **核心算法/模式**：
  - **DSU (Disjoint Set Union) 并查集**：用于企业别名与母子公司合并映射。
  - **Strategy (策略模式)**：用于 Boss、51job、智联、猎聘多平台数据 NormalizedJobData 解析。
  - **Regex Normalizer**：用于跨平台薪资折算（统一换算为 `k/月`）。

---

## 3. 核心功能需求规格

### 3.1 职位大厅与数据检索 (`/jobs`)
* **多平台数据聚合**：统一展示 Boss直聘、前程无忧、智联招聘、猎聘数据。
* **数据标准化 (Data Normalization)**：
  - 将各平台原始 JSON 解析并组装为统一的 `NormalizedJobDataVO`（包含标准标题、规范薪资、标准经验学历要求、HR 活跃状态、技能标签）。
* **高级筛选与搜索**：
  - **关键词搜索**：支持职位名称、公司名称模糊查询。
  - **薪资区间筛选**：通过 `SalaryParser` 自动折算薪资（如 `15-20k`），支持按照目标薪资范围筛选。
  - **平台与状态过滤**：支持指定招聘平台，以及在 `communication`（沟通中）、`applied`（已投递）、`interviewing`（面试中）、`eliminated`（已淘汰）、`expired`（已失效）之间切换。
  - **隐藏/失效混合开关**：可选择是否包含已隐藏或已被平台下架的失效职位。
* **职位状态变更**：支持在列表或详情中一键更新职位状态。

### 3.2 职位详情抽屉 (`JobDetailDrawer.vue`)
* **完整 JD 视图**：展现场所地址、招聘 HR 姓名与头衔、HR 活跃时间、原始公司属性。
* **AI 职位匹配诊断**：
  - 调用 LLM 对当前 Resume Markdown 与 Job Description 进行匹配分析。
  - 输出 0-100 匹配得分、A/B/C 匹配等级、核心优势与短板建议。
* **AI 招呼语与面试预测**：
  - 一键生成针对该 HR 和 JD 的个性化 Boss/平台沟通开场白。
  - 自动预测该岗位可能会问到的技术/业务面试题。

### 3.3 企业管理与黑名单控制 (`/companies`)
* **企业全景图**：展示汇总的公司名称、规模、行业、标签及相关职位数。
* **黑名单机制 (`BlacklistedCompany`)**：
  - 支持将劣质公司添加至黑名单（可附带屏蔽原因）。
  - 勾选黑名单后，职位大厅自动打上 `isBlacklisted` 标记或进行隐蔽过滤。
* **企业并查集合并 (`CompanyAlias`)**：
  - 支持将同一集团的不同子公司或别名（如“xx科技”、“xx技术”）合并映射至主公司名称。

### 3.4 投递看板与面试管理 (`/kanban`)
* **阶段看板**：按照 `沟通中` -> `已投递` -> `面试中` -> `已淘汰` 五个列进行可视化看板展示。
* **面试记录管理 (`Interview`)**：
  - 记录各轮面试的时间、形式（线上/现场）、面试轮次（一面/二面/HR面）。
  - 支持填写面试备注与复盘总结（Debrief）。

### 3.5 数据统计与可视化大屏 (`/dashboard`)
* **薪资分布图表**：柱状图展示当前搜集职位的薪资集中区间。
* **经验与学历占比**：饼图展示市场对于工作年限和学历要求的分布比例。
* **热点技能词云**：采用 `echarts-wordcloud` 生成高频技术栈词云图。

### 3.6 大模型与个人资产配置 (`/ai/*` & `/questions`)
* **LLM 模型配置 (`/ai/model`)**：配置 API Key、Base URL、Model 名称，支持连通性测试。
* **个人简历资产 (`/ai/resume`)**：维护 Markdown 格式的个人履历，供 AI 诊断比对。
* **面试题库管理 (`/questions`)**：收集高频面试题与 AI 自动生成的参考解答。

### 3.7 多平台数据抓取与全量同步 (`POST /api/sync-all`)
* 接收外部插件提交的 JSON 报表。
* 自动提取 `boss_scraped_v2`、`51job_scraped`、`zhilian_scraped`、`liepin_scraped` 并去重入库。
* 对比历史数据，自动将本次抓取中缺失的在招职位标为 `expired`（失效）。

---

## 4. 数据模型设计 (Database Schema)

| 表名 | 描述 | 主键 / 核心字段 |
| :--- | :--- | :--- |
| `Job` | 职位主表 | `id` (UUID), `jobId`, `platform`, `title`, `companyName`, `salary`, `status`, `rawData`, `rawData2`, `bossSingleDetail` |
| `Company` | 企业信息表 | `name`, `industry`, `scale`, `stage`, `rawTags` |
| `BlacklistedCompany` | 黑名单公司 | `companyName` (PK), `reason`, `createdAt` |
| `CompanyAlias` | 公司合并别名表 | `id`, `aliasName`, `canonicalName` |
| `Interview` | 面试记录表 | `id`, `jobId`, `time`, `type`, `round`, `note`, `debrief` |
| `AiJobResult` | AI 匹配诊断结果 | `jobId` (PK), `score`, `matchLevel`, `analysisJson` |
| `AiSettings` | AI 模型与简历配置 | `key` (PK), `value` |
| `StandardQuestion` | 标准题库表 | `id`, `category`, `questionStr`, `standardAnswer` |

---

## 5. RESTful API 规范明细

### 5.1 职位相关 API
- `GET /api/jobs`: 获取职位列表（支持 `page`, `pageSize`, `query`, `platform`, `status`, `salaryFilter`, `filterShowHidden`, `filterShowBlacklisted`）
- `POST /api/jobs`: 创建/更新单个职位
- `PUT /api/jobs/{id}/status`: 修改职位生命周期状态
- `DELETE /api/jobs/{id}`: 删除职位
- `POST /api/jobs/analyze`: 触发 AI 匹配分析
- `POST /api/jobs/predict-questions`: 触发 AI 面试预测

### 5.2 公司相关 API
- `GET /api/companies`: 分页查询公司列表
- `GET /api/companies/all`: 获取所有公司列表
- `GET /api/companies/blacklist`: 获取黑名单公司列表
- `POST /api/companies/blacklist`: 添加黑名单公司
- `DELETE /api/companies/blacklist/{companyName}`: 移除黑名单公司
- `POST /api/companies/merge`: 执行公司并查集合并

### 5.3 面试记录 API
- `GET /api/interviews`: 查询面试记录
- `POST /api/interviews`: 保存/更新面试记录
- `PUT /api/interviews/{id}/status`: 更新面试状态
- `DELETE /api/interviews/{id}`: 删除面试记录

### 5.4 仪表盘与全量同步 API
- `GET /api/dashboard`: 获取可视化统计数据
- `POST /api/sync-all`: 接收 Chrome 插件推送的全量抓取数据

---

## 6. 非功能性需求

1. **高性能与响应时间**：
   - 依赖本地 SQLite 存储，单表万级职位数据查询与正则表达式清洗耗时控制在 `200ms` 以内。
2. **轻量化部署**：
   - 后端使用内嵌 SQLite 数据库，无需安装外部 MySQL/PostgreSQL 实例，开箱即用。
3. **数据隐私保障**：
   - 大模型 Key 与个人简历数据仅保存在本地 SQLite 中，绝不上传至任何第三方中转服务。
