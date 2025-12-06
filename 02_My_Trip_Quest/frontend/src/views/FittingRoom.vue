<template>
  <div class="fitting-page">
    <div class="content-container">

      <section class="preview-section">
        <h2 class="section-label">CHARACTER PREVIEW</h2>
        
        <div class="avatar-display">
          <div class="avatar-layers">
            <div class="layer skin">
              <img :src="equipped.SKIN?.image || '/assets/avatar/avatar-base.png'" alt="skin"/>
            </div>
            <div class="layer bottom" v-if="equipped.BOTTOM"><img :src="equipped.BOTTOM.image" alt="bottom"/></div>
            <div class="layer top" v-if="equipped.TOP"><img :src="equipped.TOP.image" alt="top"/></div>
            <div class="layer hair" v-if="equipped.HAIR"><img :src="equipped.HAIR.image" alt="hair"/></div>
            <div class="layer hat" v-if="equipped.HAT"><img :src="equipped.HAT.image" alt="hat"/></div>
            <div class="layer face" v-if="equipped.FACE"><img :src="equipped.FACE.image" alt="face"/></div>
          </div>
        </div>

        <div class="username">TRAVELMASTER</div>
      </section>

      <section class="customize-section">
        <h2 class="section-label">
          <span class="sparkle-icon">✨</span> CUSTOMIZE YOUR CHARACTER
        </h2>

        <div class="action-bar" style="margin-bottom: 15px; text-align: right;">
          <button @click="saveCurrentAvatar" class="save-btn">
            💾 현재 코디 저장하기
          </button>
        </div>

        <div class="tab-bar">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="tab-btn"
            :class="{ active: currentTab === tab.id }"
            @click="currentTab = tab.id"
          >
            {{ tab.label }}
          </button>
        </div>

        <div class="items-area">
          <div v-if="isLoading" style="color: white;">로딩 중...</div>
          <div v-else-if="currentItems.length === 0" style="color: #ddd;">아이템이 없습니다.</div>
          
          <div 
            v-for="item in currentItems" 
            :key="item.id"
            class="item-card"
            :class="{ selected: isEquipped(item) }"
            @click="equipItem(item)"
          >
            <div v-if="isEquipped(item)" class="check-mark">✔</div>
            
            <div class="item-img"><img :src="item.image" :alt="item.name"/></div>
            <div class="item-name">{{ item.name }}</div>
          </div>
        </div>
      </section>

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

// 장착 상태 관리
const equipped = ref({
  SKIN: { id: null, type: 'SKIN', name: '기본 스킨', image: '/assets/avatar/avatar-base.png' },
  HAIR: null,
  HAT: null,
  TOP: null,
  BOTTOM: null,
  FACE: null,
});

const tabs = [
  { id: 'recent', label: '모두' },
  { id: 'HAIR', label: '머리' },
  { id: 'HAT', label: '모자' },
  { id: 'TOP', label: '상의' },
  { id: 'BOTTOM', label: '하의' },
  { id: 'FACE', label: '얼굴' },
  { id: 'SKIN', label: '스킨' },
];

// 1. 목록 데이터 가공
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

// 2. 옷 입히기 (화면 반영)
const equipItem = (item) => {
  if (isEquipped(item)) {
    if (item.type !== 'SKIN') {
      equipped.value[item.type] = null;
    }
  } else {
    equipped.value[item.type] = item;
  }
};

// 3. 저장 버튼 기능
const saveCurrentAvatar = async () => {
  if (!confirm('현재 착용한 모습을 저장하시겠습니까?')) return;
  
  try {
    const promises = [];

    // ★ [핵심 변경] 모든 슬롯을 순회하며 검사합니다.
    // SKIN은 벗을 수 없으니 제외하고 나머지 슬롯만 체크
    const slotsToCheck = ['HAIR', 'HAT', 'TOP', 'BOTTOM', 'FACE'];

    for (const slot of slotsToCheck) {
      const currentItem = equipped.value[slot];

      if (currentItem && currentItem.id) {
        // 1. 입고 있는 게 있으면 -> 장착 요청 (Equip)
        promises.push(equipItemApi(currentItem.id));
      } else {
        // 2. 입고 있는 게 없으면(null) -> 해제 요청 (Unequip)
        promises.push(unequipItemApi(slot));
      }
    }
    
    // (스킨은 무조건 장착되어 있다고 가정하거나, 별도 처리)
    if (equipped.value.SKIN && equipped.value.SKIN.id) {
        promises.push(equipItemApi(equipped.value.SKIN.id));
    }

    await Promise.all(promises);
    alert('성공적으로 저장되었습니다! (벗은 것도 반영됨)');

  } catch (err) {
    console.error(err);
    alert('저장 중 오류가 발생했습니다.');
  }
};

// ★★★ 4. [핵심] 페이지 로딩 시 장착 아이템 불러오기
onMounted(async () => {
  try {
    const data = await getMyInventory();
    allBackendItems.value = data;

    data.forEach(userItem => {
      // ★ 핵심: 백엔드에서 온 변수명(equipped) 또는 (isEquipped) 둘 다 체크
      const isOn = userItem.equipped || userItem.isEquipped;

      if (isOn && userItem.item) {
        const itemDetail = userItem.item;
        
        // 슬롯 이름 대문자 변환 (안전하게)
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
    error.value = '아이템 로딩 실패: ' + err.message;
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});


</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
.fitting-page {
  width: 100%;
  display: flex;
  justify-content: center;
  color: #1e1e1e;
  padding-top: 20px;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.preview-section {
  background-color: #8b5cf6;
  border: 4px solid #000;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  color: white;
}

.section-label {
  font-size: 14px;
  margin-bottom: 20px;
  text-shadow: 2px 2px 0 #000;
  letter-spacing: 2px;
  font-weight: bold;
}

.avatar-display {
  width: 150px;
  height: 150px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  position: relative;
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
  overflow: hidden;
}

.avatar-layers {
  position: relative;
  width: 100px; 
  height: 100px;
}

.layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.layer > img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  filter: drop-shadow(2px 2px 0 rgba(0,0,0,0.3));
}

.layer.skin { z-index: 10; }
.layer.bottom { z-index: 20; }
.layer.top { z-index: 30; }
.layer.face { z-index: 40; }
.layer.hair { z-index: 50; }
.layer.hat { z-index: 60; }

.username {
  font-size: 18px;
  font-weight: bold;
  text-shadow: 2px 2px 0 #000;
  letter-spacing: 1px;
}

.customize-section {
  background-color: #ec4899;
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  min-height: 400px;
}

.customize-section .section-label {
  font-size: 16px;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  text-shadow: 2px 2px 0 #000;
}

.tab-bar {
  background-color: #1e293b;
  padding: 10px;
  border: 4px solid #fbbf24;
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab-btn {
  background: transparent;
  border: 2px solid transparent;
  color: #94a3b8;
  padding: 8px 16px;
  font-size: 12px;
  font-weight: bold;
  cursor: pointer;
  text-transform: uppercase;
}

.tab-btn:hover { color: white; }

.tab-btn.active {
  background-color: #fbbf24;
  color: #000;
  border: 2px solid #000;
  box-shadow: 2px 2px 0 #000;
}

.items-area {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 15px;
  padding: 10px;
}

.item-card {
  background-color: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(0,0,0,0.1);
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  position: relative;
  transition: transform 0.1s;
  border-radius: 8px;
}

.item-card:hover {
  transform: translateY(-3px);
  background-color: rgba(255, 255, 255, 0.4);
}

.item-card.selected {
  background-color: #22c55e;
  border: 3px solid #fbbf24;
  box-shadow: 3px 3px 0 rgba(0,0,0,0.2);
}

.check-mark {
  position: absolute;
  top: 3px;
  right: 5px;
  color: #fbbf24;
  font-size: 12px;
  text-shadow: 1px 1px 0 #000;
}

.item-img {
  width: 50px;
  height: 50px;
  margin-bottom: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.item-img img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  filter: drop-shadow(2px 2px 0 rgba(0,0,0,0.2));
}

.item-name {
  font-size: 10px;
  color: white;
  text-shadow: 1px 1px 0 #000;
  text-align: center;
  word-break: keep-all;
}

.save-btn {
  background-color: #fbbf24; /* 노란색 포인트 */
  color: #000;
  border: 3px solid #000;
  padding: 10px 20px;
  font-weight: bold;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
  transition: transform 0.1s, box-shadow 0.1s;
  font-family: inherit; /* 폰트 상속 */
}

.save-btn:active {
  transform: translate(2px, 2px); /* 눌리는 효과 */
  box-shadow: 2px 2px 0 rgba(0,0,0,0.2);
}

.save-btn:hover {
  background-color: #f59e0b;
}
</style>