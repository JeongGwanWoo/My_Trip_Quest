<template>
  <div class="quest-map-page">
    <div class="content-container">
      <section class="map-section">
        <h2 class="section-title">📍 퀘스트 지도</h2>
        <div class="map-board">
          <MapComponent :areas="areas" @area-clicked="handleAreaClick" />
          <div class="map-legend">
            <div class="legend-item"><span class="dot yellow"></span> 진행중</div>
            <div class="legend-item"><span class="dot green"></span> 완료</div>
          </div>
        </div>
      </section>
    </div>

    <BottomSheet v-model:isOpen="isSheetOpen">
      <div class="sheet-content">
        <h2 class="section-title">🎯 퀘스트 목록</h2>
        <div class="quest-list">
          <template v-for="quest in quests" :key="quest.id">
            <!-- Area Summary Card -->
            <div
              class="quest-card"
              :class="quest.colorClass"
              @click="fetchLocations(quest.id)"
            >
              <div class="quest-info">
                <div class="quest-icon">{{ quest.icon }}</div>
                <div class="quest-text">
                  <div class="quest-name">{{ quest.name }}</div>
                  <div class="quest-sub">
                    {{ quest.completed }}/{{ quest.total }} 지역 완료
                  </div>
                </div>
              </div>
              <div class="quest-status">
                <div class="percentage-badge">{{ quest.percentage }}%</div>
                <button class="arrow-btn" :class="{ 'expanded': selectedAreaCode === quest.id }">⌄</button>
              </div>
            </div>

            <!-- Location Detail List (conditionally displayed) -->
            <Transition name="slide-fade">
              <div v-if="selectedAreaCode === quest.id" class="location-list">
                <div
                  v-for="(location, index) in areaLocations"
                  :key="location.locationId"
                  class="location-card"
                  :class="locationColor(index)"
                >
                  <div class="location-name">{{ location.title }}</div>
                  <div class="location-quest-count" @click.stop="fetchQuestsForModal(location)">
                    {{ location.questCount }} 퀘스트
                  </div>
                </div>
              </div>
            </Transition>
          </template>
        </div>
      </div>
    </BottomSheet>

    <!-- 통합 모달 -->
    <BaseModal :show="isModalVisible" @close="closeModal">
      <!-- 퀘스트 목록 표시 -->
      <div v-if="modalContentType === 'questList' && selectedLocationForModal">
        <h3>{{ selectedLocationForModal.title }} 퀘스트 목록</h3>
        <div class="nested-quest-list">
          <div v-if="locationQuests.length === 0" class="nested-quest-item no-quests">
            퀘스트가 없습니다.
          </div>
          <div v-for="quest in locationQuests" :key="quest.questId" class="nested-quest-item">
            <span class="quest-title-text">- {{ quest.title }}</span>
            <div class="quest-actions">
              <button class="quest-action-btn details-btn" @click.stop="showQuestDetails(quest)">자세히</button>
              <button class="quest-action-btn accept-btn" @click.stop="acceptQuest(quest.questId)">수락</button>
            </div>
          </div>
        </div>
      </div>
      <!-- 퀘스트 상세 정보 표시 -->
      <div v-else-if="modalContentType === 'questDetails' && selectedQuestForModal">
        <h3>퀘스트 상세 정보</h3>
        <p><strong>제목:</strong> {{ selectedQuestForModal.title }}</p>
        <p><strong>설명:</strong> {{ selectedQuestForModal.description }}</p>
        <p><strong>난이도:</strong> {{ selectedQuestForModal.difficulty }}</p>
        <p><strong>보상 경험치:</strong> {{ selectedQuestForModal.rewardXp }}</p>
        <p><strong>보상 포인트:</strong> {{ selectedQuestForModal.rewardPoints }}</p>
      </div>
      <div v-else>
        <p>정보를 불러오는 중...</p>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from "vue";
import MapComponent from "@/components/map/MapComponent.vue";
import BaseModal from "@/components/ui/BaseModal.vue";
import BottomSheet from "@/components/ui/BottomSheet.vue";
import api from "@/api";

const isSheetOpen = ref(false);
const areas = ref([]);
const quests = ref([]);
const areaLocations = ref([]);
const locationQuests = ref([]);
const selectedAreaCode = ref(null);

// Modal state
const isModalVisible = ref(false);
const modalContentType = ref('');
const selectedQuestForModal = ref(null);
const selectedLocationForModal = ref(null);

const handleAreaClick = (areaCode) => {
  fetchLocations(areaCode);
  isSheetOpen.value = true;
};

onMounted(async () => {
  try {
    const response = await api.get(`/api/v1/quest-map/areas`);
    areas.value = response.data.data;
    quests.value = response.data.data.map(item => {
      const completedCount = item.totalLocationCount - item.incompleteLocationCount;
      return {
        id: item.areaCode,
        name: item.areaName,
        completed: completedCount,
        total: item.totalLocationCount,
        percentage: item.totalLocationCount > 0 ? Math.round((completedCount / item.totalLocationCount) * 100) : 0,
        ...getQuestStyle(item.areaName)
      };
    });
  } catch (error) {
    console.error("미션 지역 데이터를 가져오는 중 오류 발생:", error);
  }
});

const getQuestStyle = (areaName) => {
  switch (areaName) {
    case '서울특별시': return { colorClass: 'bg-red', icon: '🏙️' };
    case '광주광역시': return { colorClass: 'bg-blue', icon: '🌊' };
    default: return { colorClass: 'bg-gray', icon: '❔' };
  }
};

const locationColors = ['border-l-red', 'border-l-blue', 'border-l-green', 'border-l-purple'];
const locationColor = (index) => locationColors[index % locationColors.length];

const fetchLocations = async (areaCode) => {
  if (selectedAreaCode.value === areaCode) {
    selectedAreaCode.value = null;
    areaLocations.value = [];
    return;
  }
  try {
    const response = await api.get(`/api/v1/quest-map/areas/${areaCode}`);
    areaLocations.value = response.data.data;
    selectedAreaCode.value = areaCode;
  } catch (error) {
    console.error(`'${areaCode}' 지역의 관광지 목록을 가져오는 중 오류 발생:`, error);
  }
};

const fetchQuestsForModal = async (location) => {
  try {
    const response = await api.get(`/api/v1/quest-map/locations/${location.locationId}`);
    locationQuests.value = response.data.data;
    selectedLocationForModal.value = location;
    modalContentType.value = 'questList';
    isModalVisible.value = true;
  } catch (error) {
    console.error(`'${location.locationId}' 관광지의 퀘스트 목록을 가져오는 중 오류 발생:`, error);
  }
};

const acceptQuest = async (questId) => {
  try {
    await api.post(`/api/v1/quest-map/quests/${questId}/accept`);
    alert(`퀘스트 #${questId}를 수락했습니다!`);
  } catch (error) {
    console.error(`퀘스트 #${questId} 수락 중 오류 발생:`, error);
    alert(`퀘스트 수락에 실패했습니다: ${error.response?.data?.message || error.message}`);
  }
};

// 퀘스트 상세 모달을 여는 함수
const showQuestDetails = (quest) => {
  // 모달 내용을 바꾸기 전에 잠시 닫아서 애니메이션을 다시 트리거합니다.
  isModalVisible.value = false;

  nextTick(() => {
    selectedQuestForModal.value = quest;
    modalContentType.value = 'questDetails';
    isModalVisible.value = true;
  });
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedQuestForModal.value = null;
  selectedLocationForModal.value = null;
  locationQuests.value = [];
  modalContentType.value = '';
};
</script>

<style scoped>
.quest-map-page {
  width: 100%;
  height: 100vh; /* Full viewport height */
  display: flex;
  flex-direction: column;
}

.content-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.map-section {
  flex-grow: 1;
  position: relative; /* For map legend positioning */
}

.map-board {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: none; /* No border for full-screen map */
  padding: 0;
}

.map-legend {
  position: absolute;
  z-index: 10;
  top: 20px; /* Moved to top */
  right: 20px;
  background: rgba(255, 255, 255, 0.8);
  padding: 10px;
  border-radius: 8px;
  display: flex;
  flex-direction: column; /* Vertical legend */
  gap: 8px;
  font-size: 12px;
  color: #334155;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dot {
  width: 12px;
  height: 12px;
  border: 2px solid #334155;
  border-radius: 50%;
}
.dot.yellow { background: #fbbf24; }
.dot.green { background: #22c55e; }

/* Styles for content inside the bottom sheet */
.sheet-content {
  padding: 0 20px 20px 20px;
}

.section-title {
  font-size: 18px;
  margin-bottom: 20px;
}

.quest-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
/* ... (All other specific styles like .quest-card, .location-card, etc., remain largely the same) */
.quest-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border: 4px solid #1e293b;
  box-shadow: 4px 4px 0px rgba(0, 0, 0, 0.2);
  color: white;
  cursor: pointer;
  transition: transform 0.1s;
}

.quest-card:active {
  transform: translate(-2px, -2px);
  box-shadow: 6px 6px 0px rgba(0,0,0,0.2);
}

.bg-red { background-color: #ef4444; }
.bg-blue { background-color: #3b82f6; }
.bg-green { background-color: #22c55e; }
.bg-purple { background-color: #a855f7; }
.border-l-red { border-left-color: #ef4444; }
.border-l-blue { border-left-color: #3b82f6; }
.border-l-green { border-left-color: #22c55e; }
.border-l-purple { border-left-color: #a855f7; }
.bg-gray { background-color: #6b7280; }

.quest-info { display: flex; align-items: center; gap: 20px; }
.quest-icon { font-size: 24px; background: rgba(255, 255, 255, 0.2); width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; border: 2px solid rgba(0, 0, 0, 0.2); border-radius: 4px; }
.quest-text { display: flex; flex-direction: column; gap: 8px; }
.quest-name { font-size: 14px; text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3); }
.quest-sub { font-size: 12px; opacity: 0.9; }
.quest-status { display: flex; align-items: center; gap: 15px; }
.percentage-badge { background: #fbbf24; color: black; border: 2px solid black; padding: 6px 10px; font-size: 10px; border-radius: 20px; box-shadow: 2px 2px 0px rgba(0,0,0,0.3); }

.arrow-btn { background: transparent; border: none; color: white; font-family: inherit; font-size: 24px; cursor: pointer; transition: transform 0.3s ease; }
.arrow-btn.expanded { transform: rotate(180deg); }

.location-list { padding: 10px; background-color: #f1f5f9; margin: 0 5px 15px 5px; border: 4px solid #1e293b; box-shadow: 6px 6px 0px rgba(0,0,0,0.2); }
.location-card { padding: 12px 15px; background: white; border-bottom: 2px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; font-size: 14px; color: #334155; cursor: pointer; transition: transform 0.1s, box-shadow 0.1s; border-left: 8px solid transparent; min-height: 50px; }
.location-card:active { transform: translate(-2px, -2px); box-shadow: 3px 3px 0px rgba(0,0,0,0.1); }
.location-card:last-child { border-bottom: none; }
.location-quest-count { font-size: 12px; color: #64748b; background: #e2e8f0; padding: 4px 8px; border-radius: 12px; cursor: pointer; transition: background-color 0.2s; }
.location-quest-count:hover { background-color: #cbd5e1; color: #1e293b; }

.nested-quest-list { display: flex; flex-direction: column; gap: 5px; margin-top: 15px; }
.nested-quest-item { font-size: 14px; color: #475569; padding: 10px; border-radius: 5px; background-color: #f8fafc; border: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center; }
.nested-quest-item.no-quests { justify-content: center; color: #94a3b8; }
.quest-title-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex-grow: 1; padding-right: 10px; }
.quest-actions { display: flex; gap: 8px; flex-shrink: 0; }
.quest-action-btn { border: none; padding: 4px 10px; border-radius: 5px; font-size: 12px; cursor: pointer; transition: background-color 0.2s; }
.details-btn { background-color: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; }
.details-btn:hover { background-color: #e2e8f0; }
.accept-btn { background-color: #3b82f6; color: white; border: 1px solid #2563eb; }
.accept-btn:hover { background-color: #2563eb; }
.nested-quest-item:last-child { border-bottom: none; }

.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from, .slide-fade-leave-to { transform: translateY(-10px); opacity: 0; }
</style>