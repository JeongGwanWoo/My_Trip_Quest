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
                  <img :src="equipped.SKIN?.image || '/assets/avatar/avatar-base.png'" alt="skin"/>
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
                <span class="username">TRAVELMASTER</span>
              </div>

              <button @click="saveCurrentAvatar" class="btn-save">
                <span>💾 스타일 저장하기</span>
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
              <span class="empty-icon">👕</span>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getMyInventory, equipItemApi, unequipItemApi  } from '@/api/items';

const currentTab = ref('recent');
const allBackendItems = ref([]); 
const isLoading = ref(true);
const error = ref(null);

const equipped = ref({
  SKIN: { id: null, type: 'SKIN', name: '기본 스킨', image: '/assets/avatar/avatar-base.png' },
  HAIR: null,
  HAT: null,
  TOP: null,
  BOTTOM: null,
  FACE: null,
});

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

const saveCurrentAvatar = async () => {
  if (!confirm('현재 스타일을 저장하시겠습니까?')) return;
  
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
    alert('스타일이 저장되었습니다!');

  } catch (err) {
    console.error(err);
    alert('저장 중 오류가 발생했습니다.');
  }
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
  /* 높이 100% 고정을 풀고 최소 높이만 지정 -> 스크롤은 App.vue에서 처리 */
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
  height: fit-content;
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

/* --- Main Layout --- */
.main-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
  width: 100%;
}

/* --- 1. Preview Card --- */
.preview-card {
  background: #fff; border-radius: 24px; padding: 32px 24px;
  display: flex; flex-direction: column; align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); border: 1px solid #eef2ff;
  position: sticky; top: 24px; width: 100%;
}

.preview-header h3 {
  font-size: 14px; font-weight: 700; color: #94a3b8; letter-spacing: 1px;
  margin-bottom: 24px; width: 100%; text-align: center;
}

.preview-content-wrapper {
  display: flex; flex-direction: column; align-items: center; width: 100%;
}

/* ------------------------------------------------ */
/* ★ 아바타 위치 정렬 수정 (이미지 크기 동일할 때) ★ */
/* ------------------------------------------------ */
.avatar-stage {
  width: 280px; /* 아바타가 보일 영역 크기 */
  height: 280px; 
  position: relative;
  display: block; /* Flex 제거 */
  margin: 0 auto 24px;
}

.stage-bg {
  position: absolute; 
  top: 50%; left: 50%; 
  transform: translate(-50%, -50%);
  width: 220px; height: 220px;
  background: radial-gradient(circle, #eff6ff 0%, #fff 70%);
  border-radius: 50%; 
  z-index: 0;
}

.avatar-layers { 
  position: absolute; 
  top: 0; left: 0; 
  width: 100%; height: 100%; 
  z-index: 1; 
}

.layer {
  position: absolute; 
  top: 0; left: 0; 
  width: 100%; height: 100%;
  /* Flex 제거: 이미지가 캔버스 크기대로 꽉 차게 둠 */
}

.layer > img {
  width: 100%; height: 100%; 
  object-fit: contain; /* 이미지 비율 유지하며 꽉 채움 */
  display: block;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
}

/* 레이어 순서 (Z-Index) */
.layer.skin { z-index: 10; }
.layer.face { z-index: 20; }
.layer.bottom { z-index: 30; }
.layer.top { z-index: 40; }
.layer.hair { z-index: 50; }
.layer.hat { z-index: 60; }

/* ------------------------------------------------ */

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
  background: #fff; border-radius: 24px; padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05); border: 1px solid #eef2ff;
  min-height: 600px; display: flex; flex-direction: column; width: 100%;
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

.items-area-wrapper { flex-grow: 1; }

.items-grid {
  display: grid;
  /* 기본 그리드 설정 */
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
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

/* ------------------------------------------- */
/* ★ 반응형 미디어 쿼리 (화면 잘림 해결) ★ */
/* ------------------------------------------- */

/* 태블릿 (1024px 이하) */
@media (max-width: 1024px) {
  .main-layout {
    grid-template-columns: 1fr; /* 세로 배치 */
    gap: 20px;
  }
  .preview-card { position: static; padding: 20px; }
  .preview-content-wrapper { flex-direction: row; justify-content: center; gap: 32px; }
  .avatar-stage { margin: 0; width: 220px; height: 220px; }
  .stage-bg { width: 180px; height: 180px; }
  .preview-actions { width: auto; text-align: left; }
  .character-info { margin-bottom: 16px; text-align: left; }
  .btn-save { width: auto; min-width: 200px; }
}

/* 모바일 (600px 이하) - 핵심 수정 구간 */
@media (max-width: 600px) {
  .content-container {
    padding: 24px 16px; /* 좌우 패딩 축소 */
  }

  .preview-card {
    padding: 20px 16px;
  }

  /* 미리보기 다시 세로 배치 */
  .preview-content-wrapper {
    flex-direction: column;
    gap: 16px;
  }
  .preview-actions { text-align: center; }
  .character-info { text-align: center; }
  .btn-save { width: 100%; }

  .inventory-card {
    padding: 20px 12px; /* 패딩 더 축소 */
    min-height: auto;
  }

  /* 그리드 수정: 최소 너비를 줄여서 화면 밖으로 밀려나지 않게 함 */
  .items-grid {
    /* 100px -> 85px로 축소하여 작은 화면에서도 3열 유지 가능성 높임 */
    grid-template-columns: repeat(auto-fill, minmax(85px, 1fr)); 
    gap: 10px;
  }

  .item-img-box {
    width: 100%;
    height: 60px; /* 이미지 박스 높이 줄임 */
  }
  
  .item-name {
    font-size: 11px; /* 폰트 사이즈 조절 */
  }
}
</style>