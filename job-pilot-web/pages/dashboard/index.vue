<template>
  <div class="dashboard-page">
    <div class="page-header">
      <div class="header-title-box">
        <h2>数据看板</h2>
        <p class="subtitle" v-if="activeView === 'funnel'">
          投递全链路流转 · 漏斗转化率、阶段耗时与滞留风险监控
        </p>
        <p class="subtitle" v-else>
          全平台求职市场洞察 · 共分析 {{ totalJobs }} 个职位
        </p>
      </div>

      <div class="header-controls" style="display: flex; align-items: center; gap: 12px;">
        <el-radio-group v-model="activeView" size="default">
          <el-radio-button value="funnel">🎯 求职效能与转化漏斗</el-radio-button>
          <el-radio-button value="market">🌐 全网岗位市场大盘</el-radio-button>
        </el-radio-group>
        <el-button :loading="loading" @click="fetchData(true)" style="border-radius: 6px;">
          🔄 刷新数据
        </el-button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="error" class="error-state">
      <el-alert :title="error" type="error" show-icon />
    </div>

    <!-- 视角 1: 求职效能与转化漏斗 -->
    <FunnelDashboardView
      v-else-if="activeView === 'funnel'"
      :funnel-data="funnelData"
      :time-range="funnelTimeRange"
      @update:timeRange="handleTimeRangeChange"
      @select-job="openJobDrawer"
    />

    <!-- 视角 2: 全网岗位市场大盘 -->
    <div v-else class="dashboard-container">
      <!-- KPI Stats Row -->
      <div class="kpi-row">
        <div class="kpi-card">
          <div class="kpi-label">收录职位总数</div>
          <div class="kpi-value primary">{{ statsData?.kpi?.totalJobs || totalJobs }}</div>
          <div class="kpi-tip">全平台入库职位</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">新发职位 (7天内)</div>
          <div class="kpi-value success">{{ statsData?.kpi?.recent7d || 0 }}</div>
          <div class="kpi-tip">占比 {{ statsData?.kpi?.newJobRatio || 0 }}% · 🟢 黄金投递期</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">近30天活跃新发</div>
          <div class="kpi-value warning">{{ statsData?.kpi?.recent30d || 0 }}</div>
          <div class="kpi-tip">🔥 招聘热招期</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">常驻职位 (>60天)</div>
          <div class="kpi-value danger">{{ statsData?.kpi?.stale60d || 0 }}</div>
          <div class="kpi-tip">⚠️ 疑似常挂/简历库岗</div>
        </div>
      </div>

      <!-- New Jobs Trend Chart -->
      <div class="charts-row full-width">
        <div class="chart-card">
          <div class="card-header-with-controls">
            <div class="card-title-group">
              <h3 class="chart-title" style="margin: 0; text-align: left;">📈 新增职位时序趋势</h3>
              <span class="card-subtitle">追踪不同时间窗口下全网职位的更新与扩招节奏</span>
            </div>
            <el-radio-group v-model="trendDimension" size="small">
              <el-radio-button value="weekly">按周统计 (近12周)</el-radio-button>
              <el-radio-button value="daily">近30天每日</el-radio-button>
              <el-radio-button value="monthly">按月度</el-radio-button>
            </el-radio-group>
          </div>
          <ClientOnly>
            <v-chart class="chart trend-chart" :option="trendChartOption" autoresize />
          </ClientOnly>
        </div>
      </div>

      <div class="charts-row">
        <!-- Salary Chart (With View Switcher: 区间 vs 各工作年限均薪) -->
        <div class="chart-card">
          <div class="card-header-with-controls" style="margin-bottom: 12px;">
            <div class="card-title-group">
              <h3 class="chart-title" style="margin: 0;">💰 薪资宏观透视</h3>
            </div>
            <el-radio-group v-model="salaryViewMode" size="small">
              <el-radio-button value="range">薪资区间分布</el-radio-button>
              <el-radio-button value="matrix">各年限平均月薪</el-radio-button>
            </el-radio-group>
          </div>
          <ClientOnly>
            <v-chart class="chart" :option="salaryViewMode === 'range' ? salaryChartOption : expSalaryChartOption" autoresize />
          </ClientOnly>
        </div>

        <!-- Experience Chart -->
        <div class="chart-card">
          <h3 class="chart-title">🎓 经验要求占比</h3>
          <ClientOnly>
            <v-chart class="chart" :option="expChartOption" autoresize />
          </ClientOnly>
        </div>
      </div>

      <div class="charts-row">
        <!-- Platform Distribution Chart -->
        <div class="chart-card">
          <h3 class="chart-title">🌐 招聘平台岗位来源</h3>
          <ClientOnly>
            <v-chart class="chart" :option="platformChartOption" autoresize />
          </ClientOnly>
        </div>

        <!-- HR Active Distribution Chart -->
        <div class="chart-card">
          <h3 class="chart-title">⚡ HR 活跃度画像与岗位鲜度</h3>
          <ClientOnly>
            <v-chart class="chart" :option="hrActiveChartOption" autoresize />
          </ClientOnly>
        </div>
      </div>

      <div class="charts-row">
        <!-- City Distribution Chart -->
        <div class="chart-card">
          <h3 class="chart-title">🏙️ 重点城市招聘需求排行</h3>
          <ClientOnly>
            <v-chart class="chart" :option="cityChartOption" autoresize />
          </ClientOnly>
        </div>

        <!-- Skills WordCloud -->
        <div class="chart-card">
          <h3 class="chart-title">热门技能词云</h3>
          <ClientOnly>
            <v-chart class="chart wordcloud-chart" :option="skillsChartOption" autoresize />
          </ClientOnly>
        </div>
      </div>
    </div>

    <!-- 详情抽屉 -->
    <JobDetailDrawer ref="jobDrawerRef" @status-changed="handleStatusChanged" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

// Echarts setup
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart, LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

use([
  CanvasRenderer,
  BarChart,
  PieChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const loading = ref(true)
const error = ref(null)
const statsData = ref(null)
const funnelData = ref(null)
const totalJobs = ref(0)
const trendDimension = ref('weekly')
const activeView = ref('funnel')
const salaryViewMode = ref('range')
const funnelTimeRange = ref('all')
const jobDrawerRef = ref(null)

const fetchFunnelData = async (timeRange = funnelTimeRange.value, refresh = false) => {
  try {
    const params = new URLSearchParams()
    if (timeRange && timeRange !== 'all') params.set('timeRange', timeRange)
    if (refresh) params.set('refresh', 'true')
    const qs = params.toString() ? `?${params.toString()}` : ''
    const res = await $fetch(`/api/dashboard/funnel${qs}`)
    if (res?.success) {
      funnelData.value = res.data
    }
  } catch (e) {
    console.error('Failed to fetch funnel data:', e)
  }
}

const handleTimeRangeChange = (val) => {
  funnelTimeRange.value = val
  fetchFunnelData(val, false)
}

const fetchData = async (refresh = false) => {
  loading.value = true
  try {
    const query = refresh ? '?refresh=true' : ''
    const funnelQuery = refresh
      ? `?refresh=true&timeRange=${funnelTimeRange.value}`
      : (funnelTimeRange.value !== 'all' ? `?timeRange=${funnelTimeRange.value}` : '')
    const [dashRes, funnelRes] = await Promise.all([
      $fetch(`/api/dashboard${query}`),
      $fetch(`/api/dashboard/funnel${funnelQuery}`)
    ])

    if (dashRes?.success) {
      statsData.value = dashRes.data
      totalJobs.value = dashRes.data.totalJobs
    }

    if (funnelRes?.success) {
      funnelData.value = funnelRes.data
    }
  } catch (e) {
    error.value = e.message || '获取数据失败'
  } finally {
    loading.value = false
  }
}

const openJobDrawer = (jobId) => {
  jobDrawerRef.value?.open(jobId)
}

const handleStatusChanged = () => {
  fetchData()
}

onMounted(async () => {
  try {
    // Dynamically import echarts-wordcloud on the client side to prevent SSR errors
    await import('echarts-wordcloud')
  } catch (e) {}
  await fetchData()
})


// Trend Chart Options
const trendChartOption = computed(() => {
  if (!statsData.value || !statsData.value.trends) return {}
  const items = statsData.value.trends[trendDimension.value] || []
  const xData = items.map(i => i.label)
  const yData = items.map(i => i.count)

  const isDaily = trendDimension.value === 'daily'

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    grid: { left: '3%', right: '4%', bottom: isDaily ? '12%' : '6%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: xData,
      boundaryGap: false,
      axisLabel: {
        rotate: isDaily ? 45 : 0,
        interval: isDaily ? 2 : 0,
        color: '#64748b'
      }
    },
    yAxis: {
      type: 'value',
      name: '新增职位数',
      nameTextStyle: { color: '#64748b' },
      splitLine: { lineStyle: { stroke: '#f1f5f9', type: 'dashed' } }
    },
    series: [
      {
        name: '新增职位数',
        type: 'line',
        smooth: true,
        data: yData,
        symbolSize: 7,
        showSymbol: !isDaily || yData.length < 15,
        itemStyle: { color: '#0ea5e9' },
        lineStyle: { width: 3, color: '#0ea5e9' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(14, 165, 233, 0.38)' },
              { offset: 1, color: 'rgba(14, 165, 233, 0.01)' }
            ]
          }
        }
      }
    ]
  }
})

// Salary Chart Options
const salaryChartOption = computed(() => {
  if (!statsData.value) return {}
  const data = statsData.value.salary
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: Object.keys(data),
      axisTick: { alignWithLabel: true }
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '职位数量',
        type: 'bar',
        barWidth: '60%',
        data: Object.values(data),
        itemStyle: { color: '#3498db' }
      }
    ]
  }
})

// Experience-Salary Matrix Options (各工作年限平均薪资)
const expSalaryChartOption = computed(() => {
  if (!statsData.value?.expSalaryMatrix) return {}
  const list = statsData.value.expSalaryMatrix || []
  const xData = list.map(i => i.experience)
  const yData = list.map(i => i.avgSalary)

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const item = params[0]
        const raw = list[item.dataIndex]
        return `<b>${item.name}</b><br/>平均月薪: <span style="color:#059669;font-weight:bold;">${item.value} K</span><br/>样本统计数: ${raw?.count || 0} 个`
      }
    },
    grid: { left: '3%', right: '4%', bottom: '5%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: xData,
      axisTick: { alignWithLabel: true },
      axisLabel: { color: '#64748b', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      name: '平均薪资 (K/月)',
      nameTextStyle: { color: '#64748b' },
      splitLine: { lineStyle: { stroke: '#f1f5f9', type: 'dashed' } }
    },
    series: [
      {
        name: '平均薪资',
        type: 'bar',
        barWidth: '40%',
        data: yData,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#10b981' },
              { offset: 1, color: '#059669' }
            ]
          },
          borderRadius: [4, 4, 0, 0]
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}k',
          color: '#059669',
          fontWeight: 'bold',
          fontSize: 11
        }
      }
    ]
  }
})

// Experience Chart Options
const expChartOption = computed(() => {
  if (!statsData.value) return {}
  const data = statsData.value.experience
  const pieData = Object.keys(data).map(key => ({
    name: key,
    value: data[key]
  }))
  return {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '经验要求',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 18, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: pieData
      }
    ],
    color: ['#e74c3c', '#f1c40f', '#2ecc71', '#9b59b6', '#95a5a6']
  }
})


// Platform Chart Options
const platformChartOption = computed(() => {
  if (!statsData.value?.platform) return {}
  const data = statsData.value.platform
  const pieData = Object.keys(data).map(key => ({
    name: key,
    value: data[key]
  }))
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: '平台分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        data: pieData
      }
    ],
    color: ['#00bebd', '#ff6000', '#2563eb', '#3b82f6', '#8b5cf6']
  }
})

// HR Active Level Distribution Chart Options
const hrActiveChartOption = computed(() => {
  if (!statsData.value?.hrActive) return {}
  const raw = statsData.value.hrActive
  const labelMap = {
    active: '活跃 HR (7天内)',
    moderate: '正常沟通 (30天内)',
    stale: '沉寂超时 (>30天)',
    zombie: '疑似僵尸岗',
    unknown: '未标记状态'
  }
  const colorMap = {
    active: '#10b981',
    moderate: '#3b82f6',
    stale: '#f59e0b',
    zombie: '#ef4444',
    unknown: '#94a3b8'
  }
  const pieData = Object.keys(raw)
    .filter(k => raw[k] > 0)
    .map(k => ({
      name: labelMap[k] || k,
      value: raw[k],
      itemStyle: { color: colorMap[k] || '#cbd5e1' }
    }))

  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c}个 ({d}%)' },
    legend: { top: '5%', left: 'center', textStyle: { fontSize: 12 } },
    series: [
      {
        name: 'HR 活跃度',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        data: pieData
      }
    ]
  }
})

// City Distribution Chart Options (重点招聘城市排行)
const cityChartOption = computed(() => {
  if (!statsData.value?.city) return {}
  const raw = statsData.value.city
  const cities = Object.keys(raw).reverse()
  const counts = Object.values(raw).reverse()

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: <b>{c} 个岗位</b>'
    },
    grid: { left: '3%', right: '10%', bottom: '5%', top: '5%', containLabel: true },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { stroke: '#f1f5f9', type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: cities,
      axisTick: { alignWithLabel: true },
      axisLabel: { color: '#334155', fontWeight: 500, fontSize: 12 }
    },
    series: [
      {
        name: '岗位数量',
        type: 'bar',
        barWidth: '55%',
        data: counts,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#38bdf8' },
              { offset: 1, color: '#2563eb' }
            ]
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          color: '#64748b',
          fontSize: 11
        }
      }
    ]
  }
})

// Skills WordCloud Options
const skillsChartOption = computed(() => {
  if (!statsData.value) return {}
  const data = statsData.value.skills
  
  // Sort and take top 80
  const sortedSkills = Object.entries(data)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 80)
    
  const wordCloudData = sortedSkills.map(([name, value]) => ({
    name,
    value
  }))

  return {
    tooltip: { show: true },
    series: [{
      type: 'wordCloud',
      shape: 'circle',
      keepAspect: false,
      left: 'center',
      top: 'center',
      width: '100%',
      height: '100%',
      right: null,
      bottom: null,
      sizeRange: [14, 50],
      rotationRange: [-45, 90],
      rotationStep: 45,
      gridSize: 8,
      drawOutOfBound: false,
      layoutAnimation: true,
      textStyle: {
        fontFamily: '"Helvetica Neue", Helvetica, Arial, sans-serif',
        fontWeight: 'bold',
        color: function () {
          const colors = ['#3498db', '#e74c3c', '#2ecc71', '#e67e22', '#9b59b6', '#34495e', '#1abc9c', '#e84393', '#6c5ce7', '#00cec9']
          return colors[Math.floor(Math.random() * colors.length)]
        }
      },
      emphasis: {
        focus: 'self',
        textStyle: {
          textShadowBlur: 10,
          textShadowColor: '#333'
        }
      },
      data: wordCloudData
    }]
  }
})
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.header-title-box {
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.header-controls {
  display: flex;
  align-items: center;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
  margin: 0;
}

.subtitle {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* KPI Card Row */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.kpi-card {
  background: white;
  padding: 18px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.kpi-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.kpi-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.kpi-value.primary { color: #0284c7; }
.kpi-value.success { color: #16a34a; }
.kpi-value.warning { color: #ea580c; }
.kpi-value.danger { color: #dc2626; }
.kpi-value.info { color: #64748b; }

.kpi-tip {
  font-size: 12px;
  color: #94a3b8;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.charts-row.full-width {
  grid-template-columns: 1fr;
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-color);
}

.card-header-with-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-subtitle {
  font-size: 12px;
  color: #94a3b8;
}

.chart-title {
  margin-top: 0;
  margin-bottom: 20px;
  text-align: center;
  font-size: 16px;
  color: var(--text-color);
  font-weight: 600;
}

.chart {
  height: 350px;
  width: 100%;
}

.trend-chart {
  height: 320px;
}

.wordcloud-chart {
  height: 400px;
}

.loading-state, .error-state {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

@media (max-width: 900px) {
  .kpi-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>
