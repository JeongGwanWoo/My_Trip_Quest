<template>
  <div class="fitting-page">
    <div class="content-container">
      
      <header class="page-header">
        <div class="badge">
          <span class="badge-dot"></span> FITTING ROOM
        </div>
        <h2 class="page-title">나만의 스타일 꾸미기</h2>
      </header>

      <div class="main-layout">
        <section class="preview-card">
          <div class="preview-header">
            <h3>CHARACTER PREVIEW</h3>
          </div>
          
          <div class="preview-content-wrapper">
            <div class="avatar-stage">
              <div class="stage-bg"></div>
              <div class="avatar-layers">
                <div class="layer skin">
                  <img :src="equipped.SKIN?.image || '/assets/avatar/skin-base.png'" alt="skin"/>
                </div>
                <div class="layer bottom" v-if="equipped.BOTTOM">
                  <img :src="equipped.BOTTOM.image" alt="bottom"/>
                </div>
                <div class="layer top" v-if="equipped.TOP">
                  <img :src="equipped.TOP.image" alt="top"/>
                </div>
                <div class="layer hair" v-if="equipped.HAIR">
                  <img :src="equipped.HAIR.image" alt="hair"/>
                </div>
                <div class="layer face" v-if="equipped.FACE">
                  <img :src="equipped.FACE.image" alt="face"/>
                </div>
                <div class="layer hat" v-if="equipped.HAT">
                  <img :src="equipped.HAT.image" alt="hat"/>
                </div>
              </div>
            </div>

            <div class="preview-actions">
              <div class="character-info">
                <span class="role-badge">TRAVELER</span>
                <span class="username">{{ authStore.userInfo?.nickname || 'TRAVELMASTER' }}</span>
              </div>

              <button @click="saveCurrentAvatar" class="btn-save">
                <span><i class="fa-solid fa-floppy-disk" style="margin-right: 8px;"></i> 스타일 저장하기</span>
              </button>
            </div>
          </div>
        </section>

        <section class="inventory-card">
          <div class="inventory-header">
            <h3 class="card-title">보유 아이템</h3>
            <span class="item-count">전체 {{ currentItems.length }}개</span>
          </div>

          <nav class="category-tabs">
            <button 
              v-for="tab in tabs" 
              :key="tab.id"
              class="tab-btn"
              :class="{ active: currentTab === tab.id }"
              @click="currentTab = tab.id"
            >
              {{ tab.label }}
            </button>
          </nav>

          <div class="items-area-wrapper">
            <div v-if="isLoading" class="state-msg">
              <div class="spinner"></div>
              <p>옷장을 여는 중...</p>
            </div>
            
            <div v-else-if="currentItems.length === 0" class="state-msg">
              <i class="fa-solid fa-shirt empty-icon"></i>
              <p>이 카테고리에 아이템이 없습니다.</p>
            </div>
            
            <div v-else class="items-grid">
              <div 
                v-for="item in currentItems" 
                :key="item.id"
                class="item-card"
                :class="{ selected: isEquipped(item) }"
                @click="equipItem(item)"
              >
                <div class="item-img-box">
                  <img :src="item.image" :alt="item.name"/>
                </div>
                <div class="item-info">
                  <span class="item-name">{{ item.name }}</span>
                </div>
                
                <div v-if="isEquipped(item)" class="check-overlay">
                  <span class="check-icon">✓</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

    </div>

    <!-- Save Confirmation Modal -->
    <BaseModal :show="showSaveConfirmModal" @close="closeSaveConfirmModal">
      <div class="modal-body">
        <h3 class="modal-title">스타일 저장</h3>
        <p class="modal-text">현재 스타일을 저장하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeSaveConfirmModal">취소</button>
          <button class="btn-confirm" @click="executeSaveAvatar">저장하기</button>
        </div>
      </div>
    </BaseModal>

    <!-- Save Result Modal -->
    <BaseModal :show="showSaveResultModal" @close="closeSaveResultModal">
      <div class="modal-body">
        <h3 class="modal-title" :class="{ 'text-green': saveResultType === 'success', 'text-red': saveResultType === 'error' }">
          {{ saveResultType === 'success' ? '저장 완료!' : '저장 실패' }}
        </h3>
        <p class="modal-text">{{ saveResultMessage }}</p>
        <div class="modal-actions">
          <button class="btn-confirm" @click="closeSaveResultModal">확인</button>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getMyInventory, equipItemApi, unequipItemApi  } from '@/api/items';
import BaseModal from '@/components/ui/BaseModal.vue';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const currentTab = ref('recent');
const allBackendItems = ref([]); 
const isLoading = ref(true);
const error = ref(null);

const equipped = ref({
  SKIN: { id: null, type: 'SKIN', name: '기본 스킨', image: '/assets/avatar/skin-base.png' },
  HAIR: null,
  HAT: null,
  TOP: null,
  BOTTOM: null,
  FACE: null,
});

// New states for save modals
const showSaveConfirmModal = ref(false);
const showSaveResultModal = ref(false);
const saveResultMessage = ref('');
const saveResultType = ref(''); // 'success' or 'error'

const tabs = [
  { id: 'recent', label: '전체' },
  { id: 'HAIR', label: '헤어' },
  { id: 'HAT', label: '모자' },
  { id: 'TOP', label: '상의' },
  { id: 'BOTTOM', label: '하의' },
  { id: 'FACE', label: '얼굴' },
  { id: 'SKIN', label: '스킨' },
];

const categorizedInventory = computed(() => {
  const inventory = {
    SKIN: [], HAIR: [], HAT: [], TOP: [], BOTTOM: [], FACE: [],
  };

  allBackendItems.value.forEach(userItem => {
    const itemDetail = userItem.item;
    if (itemDetail && itemDetail.slot) {
      const slotCategory = itemDetail.slot.toUpperCase();
      if (inventory[slotCategory]) {
        inventory[slotCategory].push({
          id: itemDetail.itemId,       
          type: itemDetail.slot,       
          name: itemDetail.name,       
          image: itemDetail.imageUrl,  
        });
      }
    }
  });
  return inventory;
});

const currentItems = computed(() => {
  if (currentTab.value === 'recent') {
    const allItems = [];
    for (const category in categorizedInventory.value) {
      allItems.push(...categorizedInventory.value[category]);
    }
    return allItems;
  }
  return categorizedInventory.value[currentTab.value] || [];
});

const isEquipped = (item) => {
  if (!item || !item.type) return false;
  const current = equipped.value[item.type]; 
  return current && current.id === item.id;
};

const equipItem = (item) => {
  if (isEquipped(item)) {
    if (item.type !== 'SKIN') {
      equipped.value[item.type] = null;
    }
  } else {
    equipped.value[item.type] = item;
  }
};

const closeSaveResultModal = async () => {
  showSaveResultModal.value = false;
  saveResultMessage.value = '';
  saveResultType.value = '';
  // Optionally re-fetch inventory or equipped items here if needed
};

const executeSaveAvatar = async () => {
  closeSaveConfirmModal(); // Close confirm modal first
  
  try {
    const promises = [];
    const slotsToCheck = ['HAIR', 'HAT', 'TOP', 'BOTTOM', 'FACE'];

    for (const slot of slotsToCheck) {
      const currentItem = equipped.value[slot];
      if (currentItem && currentItem.id) {
        promises.push(equipItemApi(currentItem.id));
      } else {
        promises.push(unequipItemApi(slot));
      }
    }
    
    if (equipped.value.SKIN && equipped.value.SKIN.id) {
        promises.push(equipItemApi(equipped.value.SKIN.id));
    }

    await Promise.all(promises);
    saveResultMessage.value = '스타일이 성공적으로 저장되었습니다!';
    saveResultType.value = 'success';

  } catch (err) {
    console.error(err);
    saveResultMessage.value = err.response?.data?.message || '스타일 저장 중 오류가 발생했습니다.';
    saveResultType.value = 'error';
  } finally {
    showSaveResultModal.value = true;
  }
};


const saveCurrentAvatar = async () => {
  showSaveConfirmModal.value = true;
};

const closeSaveConfirmModal = () => {
  showSaveConfirmModal.value = false;
};

onMounted(async () => {
  try {
    const data = await getMyInventory();
    allBackendItems.value = data;

    data.forEach(userItem => {
      const isOn = userItem.equipped || userItem.isEquipped;

      if (isOn && userItem.item) {
        const itemDetail = userItem.item;
        const slotName = itemDetail.slot ? itemDetail.slot.toUpperCase() : null;

        if (slotName) {
            equipped.value[slotName] = {
                id: itemDetail.itemId,
                type: slotName,
                name: itemDetail.name,
                image: itemDetail.imageUrl
            };
        }
      }
    });

  } catch (err) {
    error.value = err.message;
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
/* 1. 박스 사이징 초기화 */
*, *::before, *::after {
  box-sizing: border-box;
}

/* 기본 페이지 설정 */
.fitting-page {
  font-family: "Pretendard", sans-serif;
  width: 100%;
  min-height: 100%; 
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
}

.content-container {
  max-width: 1100px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* --- Header --- */
.page-header { margin-bottom: 8px; }

.badge {
  display: inline-flex; align-items: center; background: #e0e7ff; color: #3730a3;
  padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 700; margin-bottom: 12px;
}
.badge-dot { width: 6px; height: 6px; background-color: #4f46e5; border-radius: 50%; margin-right: 6px; }

.page-title {
  font-size: 32px; font-weight: 800; color: #1e293b; margin: 0; letter-spacing: -0.5px;
}

/* --- Main Layout (Flexbox 기반으로 변경) --- */
.main-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  width: 100%;
}

/* --- 1. Preview Card --- */
.preview-card {
  flex: 0 0 320px; /* 고정 너비 */
  background: #fff; border-radius: 24px; padding: 32px 24px;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); border: 1px solid #eef2ff;
  position: sticky; top: 24px;
}

.preview-header h3 {
  font-size: 14px; font-weight: 700; color: #94a3b8; letter-spacing: 1px;
  margin-bottom: 24px; width: 100%; text-align: center;
}

.preview-content-wrapper {
  display: flex; flex-direction: column; align-items: center; width: 100%;
}

/* Avatar */
.avatar-stage {
  width: 280px; height: 280px; 
  position: relative; display: block;
  margin: 0 auto 24px;
}
.stage-bg {
  position: absolute; top: 50%; left: 50%; 
  transform: translate(-50%, -50%);
  width: 220px; height: 220px;
  background: radial-gradient(circle, #eff6ff 0%, #fff 70%);
  border-radius: 50%; 
  z-index: 0;
}
.avatar-layers { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; }
.layer { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
.layer > img {
  width: 100%; height: 100%; 
  object-fit: contain; display: block;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
}
.layer.skin { z-index: 10; }
.layer.face { z-index: 20; }
.layer.bottom { z-index: 30; }
.layer.top { z-index: 40; }
.layer.hair { z-index: 50; }
.layer.hat { z-index: 60; }

.preview-actions { width: 100%; text-align: center; }
.character-info { margin-bottom: 32px; }
.role-badge {
  display: inline-block; background: #f1f5f9; color: #64748b;
  font-size: 11px; font-weight: 700; padding: 4px 8px;
  border-radius: 6px; margin-bottom: 8px;
}
.username { display: block; font-size: 20px; font-weight: 800; color: #1e293b; }

.btn-save {
  width: 100%; background: #2563eb; color: #fff; border: none;
  border-radius: 12px; padding: 16px; font-size: 16px; font-weight: 600;
  cursor: pointer; transition: background 0.2s, transform 0.1s;
  box-shadow: 0 4px 6px rgba(37, 99, 235, 0.2);
}
.btn-save:hover { background: #1d4ed8; transform: translateY(-1px); }
.btn-save:active { transform: translateY(0); }

/* --- 2. Inventory Card --- */
.inventory-card {
  flex: 1; /* 남은 공간 차지 */
  min-width: 0; /* flex item의 최소 너비 문제 해결 */
  background: #fff; border-radius: 24px; padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); border: 1px solid #eef2ff;
  min-height: 600px; display: flex; flex-direction: column;
}

.inventory-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
}
.card-title { font-size: 20px; font-weight: 800; color: #1e293b; margin: 0; }
.item-count { font-size: 14px; color: #64748b; }

.category-tabs {
  display: flex; gap: 8px; margin-bottom: 24px;
  overflow-x: auto; padding-bottom: 4px; scrollbar-width: none;
}
.category-tabs::-webkit-scrollbar { display: none; }

.tab-btn {
  background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0;
  padding: 8px 16px; border-radius: 20px; font-size: 14px; font-weight: 600;
  cursor: pointer; white-space: nowrap; transition: all 0.2s; flex-shrink: 0;
}
.tab-btn:hover { background: #f1f5f9; color: #334155; }
.tab-btn.active { background: #1e293b; color: #fff; border-color: #1e293b; }

.items-area-wrapper { 
  flex-grow: 1;
  min-height: 0; /* flex-grow와 함께 사용 시 넘침 방지 */
  min-width: 0; /* Flex 자식요소 넘침 방지 추가 */
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
  height: 100%;
}

.item-card {
  background: #fff; border: 1px solid #f1f5f9; border-radius: 16px;
  padding: 12px; cursor: pointer; transition: all 0.2s; position: relative;
  display: flex; flex-direction: column; align-items: center;
}
.item-card:hover { transform: translateY(-4px); box-shadow: 0 8px 16px rgba(0, 0, 0, 0.06); }
.item-card.selected { border: 2px solid #3b82f6; background: #eff6ff; }

.item-img-box {
  width: 70px; height: 70px; display: flex; align-items: center; justify-content: center;
  margin-bottom: 8px; background: #f8fafc; border-radius: 12px;
}
.item-img-box img { width: 100%; height: 100%; object-fit: contain; }

.item-info { text-align: center; width: 100%; }
.item-name {
  font-size: 13px; color: #334155; font-weight: 500; display: block;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

.check-overlay {
  position: absolute; top: -6px; right: -6px;
  background: #3b82f6; color: #fff; width: 20px; height: 20px;
  border-radius: 50%; display: flex; align-items: center; justify-content: center;
  font-size: 12px; border: 2px solid #fff;
}

/* 로딩 및 빈 상태 메시지 */
.state-msg {
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  padding: 60px 0; color: #94a3b8; height: 100%;
}
.spinner {
  width: 32px; height: 32px; border: 3px solid #e2e8f0;
  border-top-color: #3b82f6; border-radius: 50%;
  animation: spin 1s linear infinite; margin-bottom: 12px;
}
@keyframes spin { to { transform: rotate(360deg); } }
.empty-icon { font-size: 32px; margin-bottom: 12px; opacity: 0.5; }


/* --- Modal Content Styling --- */
.modal-body {
  padding: 16px 8px;
  text-align: center;
}
.modal-title {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 12px;
}
.modal-text {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 32px;
}
.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.modal-actions button {
  flex: 1;
  border: none;
  border-radius: 12px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-confirm {
  background: #2563eb;
  color: white;
}
.btn-confirm:hover {
  background: #1d4ed8;
}
.btn-cancel {
  background: #e2e8f0;
  color: #475569;
}
.btn-cancel:hover {
  background: #cbd5e1;
}

/* Success/Error colors */
.text-green { color: #22c55e; }
.text-red { color: #ef4444; }


/* ------------------------------------------- */
/* ★ 반응형 미디어 쿼리 ★ */
/* ------------------------------------------- */

/* 태블릿 및 모바일 (1024px 이하) */
@media (max-width: 1024px) {
  .main-layout {
    flex-direction: column; /* 세로 배치 */
    gap: 20px;
  }
  .preview-card { 
    position: static; 
    flex-basis: auto; /* flex-basis 초기화 */
    width: 100%;
  }
  .preview-content-wrapper { 
    flex-direction: row; 
    justify-content: center; 
    gap: 32px; 
    align-items: center;
  }
  .avatar-stage { 
    margin: 0; 
    width: 220px; 
    height: 220px; 
  }
  .stage-bg { 
    width: 180px; 
    height: 180px; 
  }
  .preview-actions { 
    width: auto; 
    text-align: left; 
  }
  .character-info { 
    margin-bottom: 16px; 
    text-align: left; 
  }
  .btn-save { 
    width: auto; 
    min-width: 200px; 
    padding: 14px 24px;
  }
}

/* 모바일 (600px 이하) */
@media (max-width: 600px) {
  .content-container {
    padding: 24px 16px; /* 좌우 패딩 16px 추가 */
  }
  
  .page-header, .preview-card {
    padding-left: 0; /* content-container가 패딩을 가짐 */
    padding-right: 0;
  }

  /* ★ 핵심 수정: 인벤토리 카드가 부모 영역을 뚫고 나가지 않도록 설정 ★ */
  .inventory-card {
     min-height: auto;
     width: 100%;
     min-width: 0; /* 중요: Flex 자식 넘침 방지 */
     padding-left: 0; /* 내부 스크롤을 위해 패딩 제거 */
     padding-right: 0;
  }

  /* 내부 컨텐츠들은 스크롤 영역을 제외하고 패딩을 가짐 */
  .inventory-header {
      padding-left: 24px; /* 여백 증가 */
      padding-right: 24px; /* 여백 증가 */
      padding-top: 10px; /* Adjust as needed */
      padding-bottom: 10px; /* Adjust as needed */
  }
  .category-tabs {
      padding-left: 16px;
      padding-right: 16px;
  }

  /* ★ 카테고리 탭 가로 스크롤 설정 ★ */
  .category-tabs {
    width: auto; /* width: 100% 대신 auto로 변경 */
    padding: 0 16px 4px 16px; /* 좌우 패딩 추가 */
    overflow-x: auto;
    flex-wrap: nowrap; /* 줄바꿈 방지 */
    justify-content: flex-start;
  }

  .page-title { font-size: 24px; }
  
  .preview-card {
    padding-top: 16px; padding-bottom: 16px;
  }
  .preview-header h3 { margin-bottom: 16px; }
  .preview-content-wrapper {
    gap: 16px; justify-content: space-around;
  }
  .avatar-stage {
    width: 160px; height: 160px;
  }
  .stage-bg {
    width: 130px; height: 130px;
  }
  .username { font-size: 18px; }
  .btn-save {
    min-width: unset; width: 100%;
    padding: 12px 16px; font-size: 14px;
  }
  .character-info { margin-bottom: 12px; }

  /* ★ 아이템 그리드 -> 수평 스크롤 Flex로 완벽 변경 ★ */
  .items-grid {
    display: flex; 
    flex-wrap: nowrap; /* 절대 줄바꿈 금지 */
    overflow-x: auto;
    gap: 12px;
    width: 100%; /* 너비 강제 */
    padding: 0 16px 12px 16px; /* 좌우 패딩 + 스크롤바 공간 */
    
    /* grid 속성 초기화 */
    height: auto;
    grid-template-columns: none; 

    /* 스크롤바 숨기기 */
    -ms-overflow-style: none;
    scrollbar-width: none;
  }
  .items-grid::-webkit-scrollbar { display: none; }
  
  .item-card {
    flex: 0 0 90px; /* 고정 너비, 줄어들지 않음 */
  }

  /* 마지막 아이템 뒤 여백 */
  .item-card:last-child {
    margin-right: 16px;
  }
  .tab-btn:last-child {
    margin-right: 16px;
  }

  .item-img-box {
    width: 100%; height: 70px; border-radius: 8px;
  }
  .item-name { font-size: 12px; }
  .check-overlay { width: 18px; height: 18px; font-size: 10px; }
}
</style>
