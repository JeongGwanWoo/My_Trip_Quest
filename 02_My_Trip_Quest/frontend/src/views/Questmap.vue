<template>
  <div class="quest-map-page">
    <div class="content-container">
      
      <section class="map-card-wrapper">
        
        <div class="map-frame">
          <MapComponent :areas="areas" @area-clicked="handleAreaClick" class="map-component" />
        </div>
        
        <div class="map-legend">
          <div class="legend-header">상태 안내</div>
          <div class="legend-item">
            <span class="status-dot yellow"></span>
            <span class="label">진행중</span>
          </div>
          <div class="legend-item">
            <span class="status-dot green"></span>
            <span class="label">완료됨</span>
          </div>
        </div>

        <BottomSheet v-model:isOpen="isSheetOpen" class="map-bottom-sheet">
          <div class="sheet-content">
            <div class="sheet-header">
              <div class="header-top-row">
                <div class="badge">
                  <span class="badge-dot"></span> QUEST LIST
                </div>
                <button @click="isSheetOpen = false" class="btn-close" title="닫기">
                  ✕
                </button>
              </div>
              <h2 class="section-title">탐험할 지역을 선택하세요</h2>
            </div>

            <div class="quest-list">
              <template v-for="quest in quests" :key="quest.id">
                <div
                  class="quest-card"
                  :class="[quest.colorClass, { 'is-active': selectedAreaCode === quest.id }]"
                  @click="fetchLocations(quest.id)"
                >
                  <div class="card-left">
                    <div class="quest-icon-box">{{ quest.icon }}</div>
                    <div class="quest-text">
                      <div class="quest-name">{{ quest.name }}</div>
                      <div class="quest-sub">
                        총 {{ quest.total }}개 중 <span class="accent-text">{{ quest.completed }}개 완료</span>
                      </div>
                    </div>
                  </div>
                  <div class="card-right">
                    <div class="progress-circle" :style="`--progress: ${quest.percentage}%`">
                      <span>{{ quest.percentage }}%</span>
                    </div>
                    <button class="arrow-btn" :class="{ 'expanded': selectedAreaCode === quest.id }">
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="6 9 12 15 18 9"></polyline>
                      </svg>
                    </button>
                  </div>
                </div>

                <Transition name="slide-fade">
                  <div v-if="selectedAreaCode === quest.id" class="location-list-container">
                    <div class="location-list-connector"></div>
                    <div class="location-list">
                      <div
                        v-for="(location, index) in areaLocations"
                        :key="location.locationId"
                        class="location-item"
                        @click.stop="fetchQuestsForModal(location)"
                      >
                        <div class="location-info">
                          <span class="bullet-point" :class="getLocationColorClass(index)"></span>
                          <span class="location-name">{{ location.title }}</span>
                        </div>
                        <div class="quest-count-badge">
                          퀘스트 {{ location.questCount }}개
                        </div>
                      </div>
                    </div>
                  </div>
                </Transition>
              </template>
            </div>
          </div>
        </BottomSheet>

      </section> 
    </div>

    <BaseModal :show="isModalVisible" @close="closeModal">
      <div class="modal-inner">
        <div v-if="modalContentType === 'questList' && selectedLocationForModal">
          <div class="modal-header">
            <h3>{{ selectedLocationForModal.title }}</h3>
            <span class="modal-subtitle">수행 가능한 퀘스트 목록입니다.</span>
          </div>
          
          <div class="nested-quest-list">
            <div v-if="locationQuests.length === 0" class="empty-state">
              <span>📭</span>
              <p>현재 수행 가능한 퀘스트가 없습니다.</p>
            </div>
            
            <div v-else v-for="quest in locationQuests" :key="quest.questId" class="nested-quest-item">
              <div class="quest-item-content">
                <span class="quest-title-text">{{ quest.title }}</span>
              </div>
              <div class="quest-actions">
                <button class="btn-text" @click.stop="showQuestDetails(quest)">상세보기</button>
                
                <!-- 퀘스트 상태에 따른 동적 버튼 -->
                <template v-if="quest.status === 'ACCEPTED' || quest.status === 'IN_PROGRESS'">
                  <button v-if="quest.questTypeId === 1" class="btn-primary-sm" @click.stop="handleCompleteArrival(quest.questId)">
                    완료하기
                  </button>
                  <button v-else-if="quest.questTypeId === 2" class="btn-primary-sm" @click.stop="triggerFileInput(quest.questId)">
                    사진 업로드
                  </button>
                </template>

                <template v-else-if="quest.status === 'COMPLETED'">
                   <button class="btn-primary-sm" disabled>완료됨</button>
                </template>

                <template v-else-if="quest.status === 'FAILED'">
                   <button class="btn-secondary-sm" disabled>실패</button>
                </template>

                <template v-else>
                  <button class="btn-primary-sm" @click.stop="acceptQuest(quest.questId)">
                    수락
                  </button>
                </template>
              </div>
            </div>
          </div>
          
          <!-- Hidden file input for photo quests -->
          <input type="file" ref="fileInputRef" @change="handleFileSelect" accept="image/*" style="display: none;">

          <!-- "내 위치 가져오기" button for photo quests without metadata -->
          <div v-if="showGeolocationButton && selectedQuestForModal?.questTypeId === 2" class="manual-location-action">
              <p>사진에서 위치 정보를 찾을 수 없습니다.</p>
              <button class="btn-primary-sm" @click="handleGetLocationAndUpload()">
                내 현재 위치로 인증하기
              </button>
          </div>
        </div>

        <div v-else-if="modalContentType === 'questDetails' && selectedQuestForModal">
          <div class="modal-header">
            <h3>퀘스트 상세 정보</h3>
          </div>
          <div class="quest-detail-content">
            <h4 class="detail-title">{{ selectedQuestForModal.title }}</h4>
            <p class="detail-desc">{{ selectedQuestForModal.description }}</p>
            
            <div class="detail-grid">
              <div class="detail-box">
                <span class="label">난이도</span>
                <span class="value">{{ selectedQuestForModal.difficulty }}</span>
              </div>
              <div class="detail-box">
                <span class="label">보상 경험치</span>
                <span class="value xp">+{{ selectedQuestForModal.rewardXp }} XP</span>
              </div>
              <div class="detail-box">
                <span class="label">보상 포인트</span>
                <span class="value point">{{ selectedQuestForModal.rewardPoints }} P</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="loading-state">
          <div class="spinner"></div>
          <p>정보를 불러오는 중...</p>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from "vue";
import MapComponent from "@/components/map/MapComponent.vue";
import BaseModal from "@/components/ui/BaseModal.vue";
import BottomSheet from "@/components/ui/BottomSheet.vue";
import api from "@/api";
import { completeArrivalQuest } from "@/api/quest";
import { completePhotoQuest } from "@/api/photoQuest"; // ★ Add this import

// --- State ---
const isSheetOpen = ref(false);
const areas = ref([]);
const quests = ref([]);
const areaLocations = ref([]);
const locationQuests = ref([]);
const selectedAreaCode = ref(null);

// Modal State
const isModalVisible = ref(false);
const modalContentType = ref('');
const selectedQuestForModal = ref(null);
const selectedLocationForModal = ref(null);

// ★ New states for photo quest
const fileInputRef = ref(null); // Reference to the hidden file input
const selectedImageFile = ref(null); // Temporarily store the selected image file
const showGeolocationButton = ref(false); // Controls visibility of "Get My Location" button
const activePhotoQuestId = ref(null); // ID of the photo quest being processed

// --- Methods ---
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
    console.error("Error fetching areas:", error);
  }
});

const getQuestStyle = (areaName) => {
  switch (areaName) {
    case '서울특별시': return { colorClass: 'accent-red', icon: '🏙️' };
    case '광주광역시': return { colorClass: 'accent-blue', icon: '🌊' };
    default: return { colorClass: 'accent-gray', icon: '📍' };
  }
};

const getLocationColorClass = (index) => {
    const colors = ['dot-red', 'dot-blue', 'dot-green', 'dot-purple'];
    return colors[index % colors.length];
};

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
    console.error(`Error fetching locations for area ${areaCode}:`, error);
  }
};

const fetchQuestsForModal = async (location) => {
  try {
    const response = await api.get(`/api/v1/quest-map/locations/${location.locationId}`);
    console.log('Quest list API response:', response.data.data); // 응답 데이터 구조 확인
    locationQuests.value = response.data.data;
    selectedLocationForModal.value = location;
    modalContentType.value = 'questList';
    isModalVisible.value = true;
  } catch (error) {
    console.error(`Error fetching quests:`, error);
  }
};

const acceptQuest = async (questId) => {
  try {
    await api.post(`/api/v1/quest-map/quests/${questId}/accept`);
    alert(`퀘스트 #${questId}를 수락했습니다!`);
    // 퀘스트 목록을 새로고침하여 버튼 상태를 업데이트합니다.
    if (selectedLocationForModal.value) {
      await fetchQuestsForModal(selectedLocationForModal.value);
    }
  } catch (error) {
    console.error(`Error accepting quest:`, error);
    alert(`실패: ${error.response?.data?.message || error.message}`);
  }
};

const handleCompleteArrival = async (questId) => {
  if (!navigator.geolocation) {
    alert("이 브라우저에서는 위치 정보 서비스를 사용할 수 없습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const { latitude, longitude } = position.coords;
      try {
        await completeArrivalQuest(questId, latitude, longitude);
        alert(`퀘스트 #${questId} 완료!`);
        if (selectedLocationForModal.value) {
          await fetchQuestsForModal(selectedLocationForModal.value);
        }
      } catch (error) {
        console.error(`Error completing arrival quest:`, error);
        alert(`퀘스트 완료 실패: ${error.response?.data?.message || error.message}`);
      }
    },
    (error) => {
      console.error("Error getting location:", error);
      alert(`위치 정보를 가져오는 데 실패했습니다: ${error.message}`);
    }
  );
};

// ★ Methods for photo quest, updated for reliability
const triggerFileInput = (questId) => {
  activePhotoQuestId.value = questId; // Store current questId
  fileInputRef.value.click();
};

const uploadPhotoForQuest = async (questId, imageFile, latitude = null, longitude = null) => {
  try {
    showGeolocationButton.value = false; // Reset button visibility
    await completePhotoQuest(questId, imageFile, latitude, longitude);
    alert(`사진 퀘스트 #${questId} 완료!`);
    if (selectedLocationForModal.value) {
      await fetchQuestsForModal(selectedLocationForModal.value);
    }
    // Reset temporary states
    selectedImageFile.value = null;
    activePhotoQuestId.value = null;
  } catch (error) {
    console.error(`Error completing photo quest:`, error);
    const errorMessage = error.response?.data?.message || error.message;

    if (error.response?.data?.code === 'PHOTO_METADATA_MISSING') {
      alert("사진에 위치 정보가 없습니다. 현재 위치로 인증하시겠습니까?");
      selectedImageFile.value = imageFile; // Store file for re-upload with manual location
      showGeolocationButton.value = true;
    } else {
      alert(`사진 퀘스트 완료 실패: ${errorMessage}`);
      // Reset temporary states
      selectedImageFile.value = null;
      showGeolocationButton.value = false;
      activePhotoQuestId.value = null;
    }
  }
};

const handleFileSelect = async (event) => {
  const file = event.target.files[0];
  if (file) {
    if (activePhotoQuestId.value) {
      await uploadPhotoForQuest(activePhotoQuestId.value, file);
    } else {
      alert("퀘스트 정보가 불충분합니다. 다시 시도해주세요.");
    }
  }
  // Clear file input regardless of selection to allow re-selection of the same file
  event.target.value = null;
};

const handleGetLocationAndUpload = async () => {
  if (!navigator.geolocation) {
    alert("이 브라우저에서는 위치 정보 서비스를 사용할 수 없습니다.");
    return;
  }

  if (!activePhotoQuestId.value) {
      alert("퀘스트 정보가 불충분합니다. 다시 시도해주세요.");
      return;
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const { latitude, longitude } = position.coords;
      if (selectedImageFile.value) {
        await uploadPhotoForQuest(activePhotoQuestId.value, selectedImageFile.value, latitude.toString(), longitude.toString());
      } else {
        alert("업로드할 사진 파일을 찾을 수 없습니다. 다시 시도해주세요.");
      }
    },
    (error) => {
      console.error("Error getting location for photo upload:", error);
      alert(`위치 정보를 가져오는 데 실패했습니다: ${error.message}`);
    }
  );
};

const showQuestDetails = (quest) => {
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
  // ★ Reset photo quest specific states
  selectedImageFile.value = null;
  showGeolocationButton.value = false;
  activePhotoQuestId.value = null;
};
</script>

<style scoped>
/* 페이지 레이아웃 */
.quest-map-page {
  font-family: "Pretendard", sans-serif;
  width: 100%;
  flex-grow: 1; /* 부모 높이 채움 */
  display: flex;
  flex-direction: column;
  background-color: #f5f7fb;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
}

.content-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  min-height: 0; /* Flexbox 버그 방지 */
}

/* 지도 카드 래퍼 (바텀시트의 부모 기준점) */
.map-card-wrapper {
  flex-grow: 1;
  position: relative; /* 중요: 자식 absolute 요소들의 기준 */
  width: 100%;
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden; /* 중요: 바텀시트가 사이드바 쪽으로 넘치지 않게 자름 */
  border: 1px solid #eef2ff;
  display: flex;
  flex-direction: column;
}

/* 지도 프레임 */
.map-frame {
  flex-grow: 1; /* 지도가 빈 공간을 모두 차지 */
  width: 100%;
  height: 100%;
  position: relative;
}

/* 지도 컴포넌트 */
:deep(.map-component),
.map-component {
  width: 100%;
  height: 100%;
  display: block;
}

/* ----------------------------------------------------- */
/* ★ 바텀 시트 스타일 재정의 (적절한 높이, 클릭 가능) */
/* ----------------------------------------------------- */

/* 1. 바텀시트 컨테이너 */
/* 1. 바텀시트 컨테이너 */
:deep(.bottom-sheet-container),
.map-bottom-sheet {
  position: absolute !important;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  width: 100% !important;
  height: 100% !important;
  z-index: 100;
  pointer-events: auto !important; /* 전체 영역 클릭 활성화 */
}

/* 2. 어두운 배경 (오버레이) */
:deep(.sheet-overlay) {
  position: absolute !important;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 101;
  pointer-events: auto !important; /* 오버레이 클릭으로 닫기 가능 */
}

/* 3. 실제 올라오는 하얀색 시트 (내용물) */
:deep(.sheet-content-wrapper) {
  position: absolute !important;
  top: auto !important;
  bottom: 0 !important;
  left: 0;
  width: 100% !important;
  max-width: none !important;
  height: 50vh !important; /* 화면의 50% 높이로 적절하게 조정 */
  max-height: none !important;
  
  z-index: 102; /* 오버레이보다 높게 */
  
  /* 스타일 */
  border-radius: 24px 24px 0 0;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
  background-color: white;
  
  /* 클릭 활성화 */
  pointer-events: auto !important;
  
  /* 부드러운 애니메이션 */
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  /* 스크롤 가능하도록 */
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 닫힌 상태 */
:deep(.bottom-sheet-container:not(.is-open) .sheet-content-wrapper) {
  transform: translateY(100%);
}

/* 열린 상태 */
:deep(.bottom-sheet-container.is-open .sheet-content-wrapper) {
  transform: translateY(0);
}

/* ----------------------------------------------------- */

/* 지도 위 범례 (Legend) */
.map-legend {
  position: absolute;
  top: 24px; right: 24px;
  background: rgba(255, 255, 255, 0.95);
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex; flex-direction: column; gap: 10px;
  backdrop-filter: blur(8px);
  z-index: 10;
  min-width: 140px;
}
.legend-header { font-size: 13px; font-weight: 700; color: #94a3b8; margin-bottom: 4px; }
.legend-item { display: flex; align-items: center; gap: 10px; }
.status-dot { width: 10px; height: 10px; border-radius: 50%; box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.5); }
.status-dot.yellow { background: #fbbf24; }
.status-dot.green { background: #22c55e; }
.legend-item .label { font-size: 14px; color: #334155; font-weight: 500; }

/* 시트 내부 스타일 */
.sheet-content { padding: 24px; background-color: #fff; height: 100%; overflow-y: auto; }
.sheet-header { margin-bottom: 24px; }
.header-top-row { display: flex; justify-content: space-between; align-items: flex-start; }
.btn-close { background: none; border: none; font-size: 20px; cursor: pointer; color: #94a3b8; padding: 4px; }
.btn-close:hover { color: #64748b; }

.badge { display: inline-flex; align-items: center; background: #e0e7ff; color: #3730a3; padding: 6px 14px; border-radius: 20px; font-size: 12px; font-weight: 700; margin-bottom: 12px; }
.badge-dot { width: 6px; height: 6px; background-color: #4f46e5; border-radius: 50%; margin-right: 6px; }
.section-title { font-size: 20px; font-weight: 800; color: #1e293b; letter-spacing: -0.5px; margin: 0; }

/* 퀘스트 리스트 스타일 */
.quest-list { display: flex; flex-direction: column; gap: 16px; padding-bottom: 40px; }
.quest-card { display: flex; justify-content: space-between; align-items: center; padding: 20px; background: #fff; border: 1px solid #f1f5f9; border-radius: 16px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04); cursor: pointer; transition: all 0.2s ease; position: relative; overflow: hidden; }
.quest-card:hover { transform: translateY(-2px); box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08); border-color: #e2e8f0; }
.quest-card.is-active { border-color: #3b82f6; background-color: #eff6ff; }
.quest-card.accent-red::before { content: ""; position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #ef4444; }
.quest-card.accent-blue::before { content: ""; position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #3b82f6; }
.quest-card.accent-gray::before { content: ""; position: absolute; left: 0; top: 0; bottom: 0; width: 4px; background: #94a3b8; }
.card-left { display: flex; align-items: center; gap: 16px; }
.quest-icon-box { width: 48px; height: 48px; background: #f8fafc; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.quest-name { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
.quest-sub { font-size: 13px; color: #64748b; }
.accent-text { color: #3b82f6; font-weight: 600; }
.card-right { display: flex; align-items: center; gap: 16px; }
.arrow-btn { background: transparent; border: none; color: #94a3b8; cursor: pointer; transition: transform 0.3s ease; padding: 4px; }
.arrow-btn.expanded { transform: rotate(180deg); color: #3b82f6; }

.location-list-container { margin-top: -8px; margin-bottom: 8px; padding-left: 24px; }
.location-list-connector { width: 2px; height: 16px; background: #e2e8f0; margin-left: 23px; margin-bottom: 4px; }
.location-list { background: #f8fafc; border-radius: 12px; padding: 8px; border: 1px solid #e2e8f0; }
.location-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border-radius: 8px; cursor: pointer; transition: background 0.2s; }
.location-item:hover { background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.location-info { display: flex; align-items: center; gap: 10px; }
.location-name { font-size: 14px; font-weight: 500; color: #334155; }
.bullet-point { width: 8px; height: 8px; border-radius: 2px; }
.dot-red { background: #fca5a5; }
.dot-blue { background: #93c5fd; }
.dot-green { background: #86efac; }
.dot-purple { background: #d8b4fe; }
.quest-count-badge { font-size: 12px; color: #64748b; background: #e2e8f0; padding: 4px 8px; border-radius: 6px; }

/* Modal & Transition */
.modal-inner { padding: 10px; }
.modal-header h3 { font-size: 20px; font-weight: 800; color: #1e293b; margin: 0 0 4px 0; }
.modal-subtitle { font-size: 14px; color: #64748b; }
.nested-quest-list { margin-top: 24px; display: flex; flex-direction: column; gap: 12px; }
.empty-state { text-align: center; padding: 40px 0; color: #94a3b8; }
.nested-quest-item { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px; display: flex; justify-content: space-between; align-items: center; transition: border-color 0.2s; }
.nested-quest-item:hover { border-color: #cbd5e1; }
.quest-title-text { font-weight: 600; color: #334155; font-size: 15px; }
.quest-actions { display: flex; gap: 8px; }
.btn-text { background: none; border: none; color: #64748b; font-size: 13px; cursor: pointer; font-weight: 500; }
.btn-text:hover { color: #334155; }
.btn-primary-sm { background: #2563eb; color: white; border: none; padding: 8px 16px; border-radius: 8px; font-size: 13px; font-weight: 600; cursor: pointer; transition: background 0.2s; }
.btn-primary-sm:hover { background: #1d4ed8; }

.btn-secondary-sm {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary-sm:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.quest-detail-content { margin-top: 24px; }
.detail-title { font-size: 18px; font-weight: 700; color: #1e293b; margin-bottom: 8px; }
.detail-desc { font-size: 15px; color: #4b5563; line-height: 1.6; margin-bottom: 24px; }
.detail-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.detail-box { background: #f1f5f9; padding: 12px; border-radius: 12px; text-align: center; display: flex; flex-direction: column; gap: 4px; }
.detail-box .label { font-size: 12px; color: #64748b; }
.detail-box .value { font-size: 15px; font-weight: 700; color: #334155; }
.detail-box .value.xp { color: #8b5cf6; }
.detail-box .value.point { color: #f59e0b; }
.slide-fade-enter-active, .slide-fade-leave-active { transition: all 0.3s ease; }
.slide-fade-enter-from, .slide-fade-leave-to { opacity: 0; transform: translateY(-10px); }
</style>