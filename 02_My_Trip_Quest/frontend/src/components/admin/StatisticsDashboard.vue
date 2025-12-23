<template>
  <div class="stats-dashboard">
    <h2>퀘스트 완료 통계</h2>
    <div v-if="loading" class="loading-message">
      데이터를 불러오는 중입니다...
    </div>
    <div v-else-if="error" class="error-message">
      데이터를 불러오는 중 오류가 발생했습니다: {{ error.message }}
    </div>
    <div v-else-if="stats.length === 0" class="no-data-message">
      아직 집계된 퀘스트 데이터가 없습니다.
    </div>
    <div v-else>
      <div class="chart-container">
        <Bar :data="chartData" :options="chartOptions" />
      </div>
      <div class="stats-table-container">
        <table class="stats-table">
          <thead>
            <tr>
              <th>퀘스트 제목</th>
              <th>완료율</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="stat in stats" :key="stat.questId">
              <td>{{ stat.questTitle }}</td>
              <td>{{ stat.completionRate.toFixed(1) }}%</td>
              <td>
                <span :class="['badge', getDifficulty(stat.completionRate).color]">
                  {{ getDifficulty(stat.completionRate).emoji }} {{ getDifficulty(stat.completionRate).text }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getQuestStats } from '@/api/admin.js';
import { Bar } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const stats = ref([]);
const loading = ref(true);
const error = ref(null);

const getDifficulty = (rate) => {
  if (rate >= 75) {
    return { text: '쉬움', color: 'green', emoji: '🟢' };
  } else if (rate < 40) {
    return { text: '어려움', color: 'red', emoji: '🔴' };
  } else {
    return { text: '보통', color: 'yellow', emoji: '🟡' };
  }
};

const chartData = computed(() => ({
  labels: stats.value.map(stat => stat.questTitle),
  datasets: [
    {
      label: '완료율',
      backgroundColor: '#42A5F5',
      borderColor: '#1E88E5',
      borderWidth: 1,
      data: stats.value.map(stat => stat.completionRate),
    },
  ],
}));

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: {
    legend: {
      display: false,
    },
    title: {
      display: true,
      text: '퀘스트별 완료율 (%)',
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const stat = stats.value[context.dataIndex];
          if (!stat) return '';
          const rate = `완료율: ${stat.completionRate.toFixed(1)}%`;
          const details = `(완료: ${stat.completionCount} / 수락: ${stat.acceptanceCount})`;
          return `${rate} ${details}`;
        }
      }
    }
  },
  scales: {
    x: {
      beginAtZero: true,
      max: 100,
      ticks: {
        callback: function(value) {
          return value + '%';
        }
      }
    },
  },
});

onMounted(async () => {
  try {
    const response = await getQuestStats();
    stats.value = response.data;
  } catch (err) {
    error.value = err;
    console.error("통계 데이터 로딩 실패:", err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.stats-dashboard {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

h2 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

.loading-message, .error-message, .no-data-message {
  padding: 20px;
  text-align: center;
  color: #555;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.error-message {
  color: #d9534f;
  background-color: #f2dede;
  border-color: #ebccd1;
}

.chart-container {
  position: relative;
  height: 500px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.stats-table-container {
  overflow-x: auto;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.stats-table {
  width: 100%;
  border-collapse: collapse;
}

.stats-table th, .stats-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #ddd;
  text-align: left;
  vertical-align: middle;
}

.stats-table th {
  background-color: #f2f2f2;
  font-weight: 600;
  color: #333;
}

.stats-table tbody tr:last-child td {
    border-bottom: none;
}

.stats-table tbody tr:hover {
  background-color: #f1f1f1;
}

.badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.badge.green {
  background-color: #28a745;
}

.badge.red {
  background-color: #dc3545;
}

.badge.yellow {
  background-color: #ffc107;
  color: #333;
}
</style>
