<template>
  <div class="stats-dashboard">
    <h2>시스템 건강도 (System Health)</h2>
    
    <div v-if="loading" class="loading-message">
      데이터를 불러오는 중입니다...
    </div>
    <div v-else-if="error" class="error-message">
      오류 발생: {{ error.message }}
    </div>
    <div class="dashboard-grid">
      
      <!-- Group 1: 시스템 현황 (System Health) -->
      <!-- 1. 시간대별 트래픽 -->
      <div class="chart-card">
        <h3>시간대별 트래픽 (24h)</h3>
        <div class="chart-wrapper">
          <Line v-if="trafficData" :data="trafficData" :options="baseChartOptions" />
          <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- 2. 에러율 추이 -->
      <div class="chart-card">
        <h3>에러 발생 추이 (1h)</h3>
        <div class="chart-wrapper">
          <Line v-if="errorData" :data="errorData" :options="errorChartOptions" />
           <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- 3. 느린 API Top 5 -->
      <div class="chart-card">
        <h3>느린 API 기능 Top 5 (Avg ms)</h3>
        <div class="chart-wrapper">
          <Bar v-if="slowestData" :data="slowestData" :options="horizontalBarOptions" />
           <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- Group 2: 콘텐츠 및 유저 (Content & User) -->
      <!-- 4. 유저 레벨 분포 -->
      <div class="chart-card">
        <h3>유저 레벨 분포 (User Levels)</h3>
        <div class="chart-wrapper">
          <Bar v-if="levelDistributionData" :data="levelDistributionData" :options="baseChartOptions" />
          <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- 5. 지역별 인기 퀘스트 -->
      <div class="chart-card">
        <h3>지역별 인기 퀘스트 (Hot Regions)</h3>
        <div class="chart-wrapper">
          <Doughnut v-if="regionalHotspotData" :data="regionalHotspotData" :options="doughnutOptions" />
          <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- 6. 퀘스트 완료율 -->
      <div class="chart-card">
        <h3>퀘스트 완료율</h3>
        <div class="chart-wrapper">
          <Bar v-if="questData" :data="questData" :options="questChartOptions" />
        </div>
      </div>

      <!-- Group 3: 경제 분석 (Economy) -->
      <!-- 7. 포인트 경제 분석 -->
      <div class="chart-card">
        <h3>포인트 경제 분석 (Economy)</h3>
        <div class="chart-wrapper">
          <Doughnut v-if="economyPointsData" :data="economyPointsData" :options="doughnutOptions" />
          <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>

      <!-- 8. 인기 아이템 Top 5 -->
      <div class="chart-card">
        <h3>인기 아이템 Top 5 (Best Sellers)</h3>
        <div class="chart-wrapper">
          <Bar v-if="topSellingItemsData" :data="topSellingItemsData" :options="horizontalBarOptions" />
          <div v-else class="no-data">데이터 없음</div>
        </div>
      </div>
      
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';

import { getQuestStats, getDashboardStats, getEconomyStats, getContentStats } from '@/api/admin.js';
import { Bar, Line, Doughnut } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  CategoryScale,
  LinearScale,
  Filler,
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, LineElement, PointElement, ArcElement, CategoryScale, LinearScale, Filler);

const loading = ref(true);
const error = ref(null);

// Data Refs
const systemStats = ref(null); // { traffic, errorRates, slowestFeatures }
const questStats = ref([]);
const economyStats = ref(null); // { totalPointsEarned, totalPointsSpent, topSellingItems }
const contentStats = ref(null); // { userLevelStats, regionalStats }

// Fetch Data
onMounted(async () => {
  try {
    const [sysRes, questRes, ecoRes, contRes] = await Promise.all([
      getDashboardStats(),
      getQuestStats(),
      getEconomyStats(),
      getContentStats()
    ]);
    systemStats.value = sysRes.data;
    questStats.value = questRes.data;
    economyStats.value = ecoRes.data;
    contentStats.value = contRes.data;
  } catch (err) {
    error.value = err;
    console.error("Dashboard Load Error:", err);
  } finally {
    loading.value = false;
  }
});

// --- Computed Chart Data ---

// 1. Traffic Chart (Line)
const trafficData = computed(() => {
  if (!systemStats.value?.traffic) return null;
  const data = systemStats.value.traffic;
  return {
    labels: data.map(d => d.timeGroup),
    datasets: [{
      label: 'Request Count',
      data: data.map(d => d.requestCount),
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.1)',
      tension: 0.3,
      fill: true
    }]
  };
});

// 2. Error Chart (Line)
const errorData = computed(() => {
  if (!systemStats.value?.errorRates) return null;
  const data = systemStats.value.errorRates;
  return {
    labels: data.map(d => d.timeSlot),
    datasets: [{
      label: 'Error Count',
      data: data.map(d => d.errorCount),
      borderColor: '#ef4444',
      backgroundColor: 'rgba(239, 68, 68, 0.1)',
      tension: 0.1
    }]
  };
});

// 3. Slowest API (Horizontal Bar)
const slowestData = computed(() => {
  if (!systemStats.value?.slowestFeatures) return null;
  const data = systemStats.value.slowestFeatures;
  return {
    labels: data.map(d => d.featureName),
    datasets: [{
      label: 'Avg Execution Time (ms)',
      data: data.map(d => d.avgTime),
      backgroundColor: '#f59e0b',
    }]
  };
});

// 4. Quest Chart (Stacked: Completed vs Incomplete)
const questData = computed(() => {
  if (!questStats.value.length) return null;
  return {
    labels: questStats.value.map(s => s.questTitle),
    datasets: [
      {
        label: '완료 (Success)',
        data: questStats.value.map(s => s.completionCount),
        backgroundColor: '#10b981', // Green
        stack: 'Stack 0',
      },
      {
        label: '미완료/실패 (Incomplete)',
        data: questStats.value.map(s => s.acceptanceCount - s.completionCount),
        backgroundColor: '#94a3b8', // Gray
        stack: 'Stack 0',
      }
    ]
  };
});

// 5. Economy: Points (Doughnut)
const economyPointsData = computed(() => {
  if (!economyStats.value) return null;
  return {
    labels: ['총 획득 포인트 (Earned)', '총 사용 포인트 (Spent)'],
    datasets: [{
      data: [economyStats.value.totalPointsEarned, economyStats.value.totalPointsSpent],
      backgroundColor: ['#3b82f6', '#ef4444'],
      hoverOffset: 4
    }]
  };
});

// 6. Economy: Top Selling Items (Bar)
const topSellingItemsData = computed(() => {
  if (!economyStats.value?.topSellingItems) return null;
  const items = economyStats.value.topSellingItems;
  return {
    labels: items.map(i => i.itemName),
    datasets: [{
      label: '판매량 (Sold)',
      data: items.map(i => i.salesCount),
      backgroundColor: '#8b5cf6',
    }]
  };
});

// 7. Content: User Level Distribution (Bar)
const levelDistributionData = computed(() => {
  if (!contentStats.value?.userLevelStats) return null;
  const stats = contentStats.value.userLevelStats;
  return {
    labels: stats.map(s => s.levelRange),
    datasets: [{
      label: '유저 수 (Users)',
      data: stats.map(s => s.userCount),
      backgroundColor: '#f97316', // Orange
    }]
  };
});

// 8. Content: Regional Hotspots (Doughnut)
const regionalHotspotData = computed(() => {
  if (!contentStats.value?.regionalStats) return null;
  const stats = contentStats.value.regionalStats;
  // Generate colors dynamically or use a preset palette
  const colors = [
    '#3b82f6', '#ef4444', '#10b981', '#f59e0b', '#6366f1', 
    '#8b5cf6', '#ec4899', '#14b8a6', '#f43f5e', '#a855f7'
  ];
  
  return {
    labels: stats.map(s => s.areaName),
    datasets: [{
      data: stats.map(s => s.completedCount),
      backgroundColor: colors.slice(0, stats.length),
      hoverOffset: 4
    }]
  };
});

// --- Options ---
const baseChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } }
};

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'bottom' } }
};

const errorChartOptions = {
  ...baseChartOptions,
  scales: { y: { beginAtZero: true, suggestedMax: 5 } }
};

const horizontalBarOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: { legend: { display: false } }
};

const questChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: { 
    legend: { display: true }, // Legend ON
    tooltip: {
      callbacks: {
        footer: (tooltipItems) => { return ''; },
        label: function(context) {
           let label = context.dataset.label || '';
           if (label) {
               label += ': ';
           }
           if (context.parsed.x !== null) {
               label += context.parsed.x + '회';
           }
           return label;
        }
      }
    }
  },
  scales: { 
    x: { 
      stacked: true,
      grid: { display: false }
    },
    y: { 
      stacked: true,
      grid: { display: false }
    }
  }
};

</script>

<style scoped>
.stats-dashboard {
  padding: 24px;
  background-color: #f8fafc;
  min-height: 100%;
}

h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
}

.section-title {
  margin-top: 40px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

@media (max-width: 1280px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
}

.chart-card h3 {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 16px;
  font-weight: 600;
}

.chart-wrapper {
  height: 300px;
  position: relative;
}

.loading-message, .error-message {
  text-align: center;
  padding: 40px;
  color: #64748b;
}

.error-message {
  color: #ef4444;
}

.no-data {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
</style>
