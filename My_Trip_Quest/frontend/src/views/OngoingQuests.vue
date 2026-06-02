<template>
  <div class="ongoing-quests-page">
    <div class="content-container">
      <header class="page-header">
        <h2 class="page-title">진행중인 퀘스트</h2>
        <p class="page-subtitle">현재 수락했거나 진행하고 있는 퀘스트 목록입니다.</p>
      </header>

      <div v-if="isLoading" class="loading-container">
        <p>목록을 불러오는 중...</p>
      </div>

      <div v-else-if="quests.length === 0" class="no-quests-container">
        <div class="icon-wrapper">
          <i class="fa-solid fa-ghost"></i>
        </div>
        <h3>진행중인 퀘스트가 없습니다.</h3>
        <p>퀘스트 지도로 가서 새로운 퀘스트를 수락해보세요!</p>
        <router-link to="/quest-map" class="btn-primary">퀘스트 지도로 가기</router-link>
      </div>

      <div v-else class="quests-list">
        <div v-for="quest in quests" :key="quest.questId" class="quest-item" @click="openQuestDetail(quest)">
          <div class="quest-item-header">
            <span class="location-badge">{{ quest.locationName }}</span>
            <span :class="['status-badge', getStatusClass(quest.status)]">{{ formatStatus(quest.status) }}</span>
          </div>
          <h3 class="quest-title">{{ quest.title }}</h3>
          <div class="quest-footer">
            <span class="accepted-date">수락일: {{ formatDate(quest.acceptedAt) }}</span>
          </div>
        </div>
      </div>

      <QuestDetailModal
        v-if="isModalOpen"
        :quest="selectedQuest"
        @close="closeModal"
        @quest-updated="fetchOngoingQuests"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getOngoingQuests } from '@/api/quest.js';
import QuestDetailModal from '@/components/quest/QuestDetailModal.vue';

const quests = ref([]);
const isLoading = ref(true);
const isModalOpen = ref(false);
const selectedQuest = ref(null);

const openQuestDetail = (quest) => {
  selectedQuest.value = quest;
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedQuest.value = null;
};


const fetchOngoingQuests = async () => {
  isLoading.value = true;
  try {
    const response = await getOngoingQuests();
    if (response.data.success && Array.isArray(response.data.data)) {
      quests.value = response.data.data;
    }
  } catch (error) {
    console.error("Error fetching ongoing quests:", error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchOngoingQuests);

const getStatusClass = (status) => {
  if (status === 'IN_PROGRESS') return 'status-in-progress';
  if (status === 'ACCEPTED') return 'status-accepted';
  return '';
};

const formatStatus = (status) => {
  if (status === 'IN_PROGRESS') return '진행중';
  if (status === 'ACCEPTED') return '수락됨';
  return status;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};
</script>

<style scoped>
.ongoing-quests-page {
  font-family: "Pretendard", sans-serif;
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
  min-height: 100vh;
  padding: 40px 20px;
}

.content-container {
  max-width: 800px;
  width: 100%;
}

.page-header {
  margin-bottom: 32px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  margin-top: 8px;
}

.loading-container, .no-quests-container {
  text-align: center;
  padding: 80px 20px;
  background-color: #fff;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.no-quests-container .icon-wrapper {
  font-size: 48px;
  color: #cbd5e1;
  margin-bottom: 24px;
}
.no-quests-container h3 {
  font-size: 22px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 8px;
}
.no-quests-container p {
  color: #64748b;
  margin-bottom: 24px;
}

.btn-primary {
  display: inline-block;
  background-color: #3b82f6;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 700;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #2563eb;
}

.quests-list {
  display: grid;
  gap: 16px;
}

.quest-item {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #eef2ff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}
.quest-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.07);
}

.quest-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.location-badge {
  background-color: #f1f5f9;
  color: #64748b;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}
.status-accepted {
  background-color: #eff6ff;
  color: #3b82f6;
}
.status-in-progress {
  background-color: #f0fdf4;
  color: #22c55e;
}

.quest-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
  word-break: keep-all; /* 한글 단어 중간에 잘리는 현상 방지 */
  overflow-wrap: break-word; /* 긴 영단어 등 비정상적인 오버플로우 방지 */
}

.quest-footer {
  font-size: 13px;
  color: #94a3b8;
}

</style>
