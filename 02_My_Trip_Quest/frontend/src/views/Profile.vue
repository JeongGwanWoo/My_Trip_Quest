<template>
  <div class="profile-page">
    <div class="content-container">
      
      <h2 class="page-title">
        <span class="icon">👤</span> USER PROFILE
      </h2>

      <div class="dashboard-grid">
        
        <div class="left-column">
          
          <div class="profile-card">
            <!-- ========== AVATAR DISPLAY ========== -->
            <div class="avatar-wrapper">
              <div class="avatar-bg"></div>
              <div class="avatar-layers">
                <!-- Equipped items will be rendered here -->
                <img 
                  v-for="item in equippedItems" 
                  :key="item.itemId" 
                  :src="item.imageUrl" 
                  :class="['avatar-item-layer', item.slot.toLowerCase()]"
                  :alt="item.name"
                />
              </div>
            </div>
            <!-- ===================================== -->

            <h1 class="username">TRAVELMASTER</h1>
            <p class="user-handle">TRAVELQUEST.COM</p>
          </div>

          <div class="info-box dark-box">
            <div class="info-label">
              <span class="small-icon">📅</span> JOINED
            </div>
            <div class="info-value">2024. 1. 15.</div>
          </div>
        </div>

        <div class="right-column">
          
          <div class="level-box">
            <div class="level-header">
              <div class="level-info">
                <span class="star-icon">⭐</span>
                <div class="text-group">
                  <span class="sub-label">CURRENT LEVEL</span>
                  <span class="main-label">LEVEL 12</span>
                </div>
              </div>
              <div class="xp-badge">
                <span class="xp-label">XP</span>
                3400/12000
              </div>
            </div>

            <div class="xp-progress-container">
              <div class="xp-bar-bg">
                <div class="xp-bar-fill" style="width: 28%">
                  <span class="progress-text">28%</span>
                </div>
              </div>
              <div class="xp-footer">8600 XP TO LEVEL 13</div>
            </div>
          </div>

          <div class="stats-grid">
            <div class="stat-card yellow">
              <div class="stat-icon">🪙</div>
              <div class="stat-content">
                <span class="stat-label">TOTAL COINS</span>
                <span class="stat-value">5000</span>
              </div>
            </div>
            <div class="stat-card green">
              <div class="stat-icon">🏆</div>
              <div class="stat-content">
                <span class="stat-label">MISSIONS</span>
                <span class="stat-value">3/60</span>
              </div>
            </div>
            <div class="stat-card teal">
              <div class="stat-icon">📈</div>
              <div class="stat-content">
                <span class="stat-label">COMPLETION</span>
                <span class="stat-value">5%</span>
              </div>
            </div>
            <div class="stat-card orange">
              <div class="stat-icon">🗺️</div>
              <div class="stat-content">
                <span class="stat-label">CITIES</span>
                <span class="stat-value">1/4</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- ========== INVENTORY SECTION ========== -->
      <section class="inventory-section">
        <h3 class="section-title">
          <span class="book-icon">🎒</span> INVENTORY
        </h3>
        <div class="inventory-grid">
          <div 
            v-for="item in inventoryItems" 
            :key="item.itemId" 
            class="item-card"
            @click="handleEquipItem(item.itemId)"
          >
            <img :src="item.imageUrl" :alt="item.name" class="item-image" />
            <div class="item-name">{{ item.name }}</div>
          </div>
        </div>
      </section>
      <!-- ======================================= -->

      <section class="city-progress-section">
        <h3 class="section-title">
          <span class="book-icon">📖</span> CITY PROGRESS
        </h3>
        <div class="city-cards-row">
          <div v-for="city in cityProgress" :key="city.id" class="mini-city-card">
            <div class="mini-header">
              <span class="mini-icon">{{ city.icon }}</span>
              <div class="mini-info">
                <div class="mini-name">{{ city.name }}</div>
                <div class="mini-sub">{{ city.completed }}/{{ city.total }} MISSIONS</div>
              </div>
            </div>
            
            <div class="mini-progress-bg">
              <div 
                class="mini-progress-fill" 
                :class="city.colorClass"
                :style="{ width: city.percentage + '%' }"
              ></div>
            </div>
            <div class="mini-percent">{{ city.percentage }}%</div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAvatar, equipItem } from '@/api/avatar.js';

// 현재 로그인한 유저 ID (예시)
const userId = ref(1);

// 착용중인 아이템 목록
const equippedItems = ref([]);

// ===================================================================
// !!! 임시 목 데이터(Mock Data) !!!
// 추후 백엔드에서 사용자의 전체 아이템 목록을 가져오는 API를 구현해야 합니다.
// ===================================================================
const inventoryItems = ref([
  { itemId: 1, name: '기본 모자', slot: 'HEAD', imageUrl: 'https://placehold.co/100x100/f87171/white?text=Hat' },
  { itemId: 2, name: '파란 티셔츠', slot: 'TOP', imageUrl: 'https://placehold.co/100x100/60a5fa/white?text=Top' },
  { itemId: 3, name: '청바지', slot: 'BOTTOM', imageUrl: 'https://placehold.co/100x100/3b82f6/white?text=Bottom' },
  { itemId: 4, name: '스포츠 선글래스', slot: 'EYES', imageUrl: 'https://placehold.co/100x100/34d399/white?text=Eyes' },
  { itemId: 5, name: '가죽 부츠', slot: 'SHOES', imageUrl: 'https://placehold.co/100x100/a16207/white?text=Shoes' },
  { itemId: 6, name: '도시 배경', slot: 'BACKGROUND', imageUrl: 'https://placehold.co/200x200/94a3b8/white?text=BG' },
]);
// ===================================================================

// 서버에서 현재 아바타 정보를 가져와 equippedItems를 업데이트하는 함수
const fetchAvatarData = async () => {
  try {
    const response = await getAvatar(userId.value);
    if (response.success) {
      equippedItems.value = response.data.equippedItems;
    }
  } catch (error) {
    console.error("Failed to fetch avatar data:", error);
  }
};

// 아이템 착용을 처리하는 함수
const handleEquipItem = async (itemId) => {
  try {
    const response = await equipItem(userId.value, itemId);
    if (response.success) {
      // 착용 성공 시, 아바타 정보를 다시 불러와 화면을 갱신
      await fetchAvatarData();
    }
  } catch (error) {
    console.error("Failed to equip item:", error);
  }
};

// 컴포넌트가 마운트될 때 아바타 데이터를 불러옴
onMounted(() => {
  fetchAvatarData();
});


const cityProgress = ref([
  { id: 1, name: '서울', icon: '🏙️', completed: 3, total: 15, percentage: 20, colorClass: 'fill-green' },
  { id: 2, name: '부산', icon: '🌊', completed: 0, total: 15, percentage: 0, colorClass: 'fill-blue' },
  { id: 3, name: '제주', icon: '🌴', completed: 0, total: 15, percentage: 0, colorClass: 'fill-orange' },
  { id: 4, name: '경주', icon: '🏛️', completed: 0, total: 15, percentage: 0, colorClass: 'fill-purple' },
]);
</script>

<style scoped>
/* ========== NEW STYLES for AVATAR & INVENTORY ========== */
.avatar-layers {
  position: relative;
  width: 140px;
  height: 140px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-item-layer {
  position: absolute;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
/* z-index로 아이템 레이어 순서 정하기 */
.avatar-item-layer.background { z-index: 1; width: 140px; height: 140px; }
.avatar-item-layer.bottom { z-index: 2; }
.avatar-item-layer.top { z-index: 3; }
.avatar-item-layer.shoes { z-index: 4; }
.avatar-item-layer.accessory { z-index: 5; }
.avatar-item-layer.eyes { z-index: 6; }
.avatar-item-layer.head { z-index: 7; }


.inventory-section {
  background-color: #1e293b;
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
}

.inventory-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px;
}

.item-card {
  background-color: #334155;
  border: 2px solid #000;
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.item-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.3);
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  background-color: #475569;
  border: 1px solid #64748b;
}

.item-name {
  color: #e2e8f0;
  font-size: 10px;
  text-align: center;
}

/* ======================================================== */


.profile-page {
  width: 100%;
  display: flex;
  justify-content: center;
  color: #1e1e1e;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.page-title {
  font-size: 24px;
  color: #fbbf24;
  color: #e2e8f0;
  text-shadow: 2px 2px 0 #000;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 15px;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 20px;
}

.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  background-color: #8b5cf6;
  border: 4px solid #000;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
}

.avatar-wrapper {
  position: relative;
  width: 140px;
  height: 140px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-bg {
  position: absolute;
  width: 120px;
  height: 120px;
  background-color: #fbbf24;
  border-radius: 50%;
  border: 3px solid #000;
}

.username {
  color: white;
  font-size: 20px;
  text-shadow: 2px 2px 0 #000;
  margin: 0 0 5px 0;
  text-align: center;
}

.user-handle {
  color: #ddd6fe;
  font-size: 10px;
  margin: 0;
}

.dark-box {
  background-color: #1e293b;
  border: 4px solid #000;
  padding: 20px;
  color: white;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
}

.info-label {
  font-size: 10px;
  color: #64748b;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.info-value {
  font-size: 14px;
  font-weight: bold;
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.level-box {
  background-color: #3b82f6;
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  color: white;
}

.level-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 15px;
}

.level-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.star-icon { font-size: 30px; text-shadow: 2px 2px 0 #000; color: #fbbf24;}

.text-group { display: flex; flex-direction: column; gap: 5px; }
.sub-label { font-size: 8px; opacity: 0.8; }
.main-label { font-size: 24px; font-weight: bold; text-shadow: 3px 3px 0 #000; }

.xp-badge {
  background-color: rgba(0,0,0,0.3);
  padding: 10px 15px;
  border: 2px solid #000;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}
.xp-label { font-size: 8px; color: #fbbf24; margin-bottom: 2px; }

.xp-progress-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.xp-bar-bg {
  width: 100%;
  height: 24px;
  background-color: #1e293b;
  border: 2px solid #000;
  position: relative;
}

.xp-bar-fill {
  height: 100%;
  background-color: #22c55e;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 5px;
}

.progress-text {
  font-size: 8px;
  color: #000;
  font-weight: bold;
}

.xp-footer {
  text-align: right;
  font-size: 8px;
  opacity: 0.8;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
  gap: 20px;
  flex: 1;
}

.stat-card {
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  color: #000;
}

.stat-card.yellow { background-color: #fbbf24; }
.stat-card.green { background-color: #22c55e; color: white; text-shadow: 1px 1px 0 #000; }
.stat-card.teal { background-color: #06b6d4; color: white; text-shadow: 1px 1px 0 #000; }
.stat-card.orange { background-color: #f97316; color: white; text-shadow: 1px 1px 0 #000; }

.stat-icon { font-size: 24px; }
.stat-content { display: flex; flex-direction: column; gap: 5px; }
.stat-label { font-size: 8px; font-weight: bold; opacity: 0.8; }
.stat-value { font-size: 24px; font-weight: bold; }

.city-progress-section {
  background-color: #3b82f6;
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
}

.section-title {
  color: white;
  font-size: 16px;
  margin: 0 0 20px 0;
  text-shadow: 2px 2px 0 #000;
  display: flex;
  align-items: center;
  gap: 10px;
}

.city-cards-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.mini-city-card {
  background-color: #1e293b;
  padding: 15px;
  border: 2px solid #000;
  color: white;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mini-header {
  display: flex;
  align-items: center;
  gap: 10px;
}
.mini-icon {
  background: rgba(255,255,255,0.1);
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border-radius: 4px;
}
.mini-info { display: flex; flex-direction: column; gap: 4px; }
.mini-name { font-size: 12px; font-weight: bold; }
.mini-sub { font-size: 8px; color: #fbbf24; }

.mini-progress-bg {
  width: 100%;
  height: 8px;
  background-color: #334155;
  position: relative;
}
.mini-progress-fill { height: 100%; }
.fill-green { background-color: #22c55e; }
.fill-blue { background-color: #3b82f6; }
.fill-orange { background-color: #f97316; }
.fill-purple { background-color: #8b5cf6; }

.mini-percent {
  font-size: 8px;
  text-align: right;
  opacity: 0.6;
}

/* 반응형 */
@media (max-width: 900px) {
  .dashboard-grid { grid-template-columns: 1fr; }
  .left-column { flex-direction: row; }
  .profile-card, .dark-box { flex: 1; }
  .city-cards-row { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .left-column { flex-direction: column; }
  .city-cards-row { grid-template-columns: 1fr; }
  .level-header { flex-direction: column; align-items: flex-start; gap: 10px; }
}
</style>