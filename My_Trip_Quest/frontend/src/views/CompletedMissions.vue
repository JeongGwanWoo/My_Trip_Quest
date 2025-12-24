<template>
  <div class="completed-missions-page">
    <div class="content-container">
      <header class="page-header">
        <h2 class="page-title">완료한 미션</h2>
      </header>
      <div class="missions-container">
        <div v-if="loading" class="loading-spinner">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <span>로딩 중...</span>
        </div>

        <div v-else-if="missions.length > 0" class="missions-list">
          <div v-for="mission in missions" :key="mission.questId" class="mission-card">
            <div class="mission-header">
              <h3 class="mission-title">{{ mission.questTitle }}</h3>
              <span class="mission-location">{{ mission.locationName }}</span>
            </div>
            <p class="mission-content">{{ mission.questContent }}</p>
            <div class="mission-footer">
              <span class="mission-date">완료: {{ formatDate(mission.completedAt) }}</span>
            </div>
          </div>
        </div>

        <div v-else class="no-missions">
          <p>아직 완료한 미션이 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getCompletedMissions } from '@/api/user.js';
import { useToast } from '@/utils/toast';

const { showToast } = useToast();
const missions = ref([]);
const loading = ref(true);

const fetchMissions = async () => {
  try {
    const response = await getCompletedMissions();
    if (response.success) {
      missions.value = response.data;
    } else {
      showToast(response.message || '미션 목록을 불러오는 데 실패했습니다.', 'error');
    }
  } catch (error) {
    showToast('완료한 미션 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

onMounted(() => {
  fetchMissions();
});
</script>

<style scoped>
.completed-missions-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
  min-height: 100%;
}

.content-container {
  max-width: 800px;
  width: 100%;
  padding: 40px 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
}

.loading-spinner, .no-missions {
  text-align: center;
  padding: 40px;
  color: #64748b;
  font-size: 16px;
}
.loading-spinner i {
  margin-right: 8px;
}

.missions-list {
  display: grid;
  gap: 16px;
}

.mission-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #eef2ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.mission-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.mission-title {
  font-size: 18px;
  font-weight: 700;
  color: #334155;
  margin: 0;
}

.mission-location {
  font-size: 13px;
  color: #64748b;
  background-color: #f8fafc;
  padding: 4px 8px;
  border-radius: 8px;
  white-space: nowrap;
}

.mission-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.mission-footer {
  margin-top: auto;
  text-align: right;
}

.mission-date {
  font-size: 12px;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .content-container {
    padding: 20px 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .mission-card {
    padding: 16px;
  }

  .mission-title {
    font-size: 16px;
  }
}
</style>