<template>
  <div class="shop-page">
    <div class="content-container">
      
      <header class="shop-header">
        <div class="header-left">
          <div class="badge">
            <span class="badge-dot"></span> ITEM SHOP
          </div>
          <h2 class="page-title">캐릭터 상점</h2>
          <p class="page-desc">나만의 개성을 뽐낼 아이템을 구매해보세요!</p>
        </div>
        
        <div class="coin-card">
          <div class="coin-label">내 보유 코인</div>
          <div class="coin-value-row">
            <i class="fa-solid fa-coins coin-icon"></i>
            <span class="coin-amount">{{ userCoins.toLocaleString() }} P</span>
          </div>
        </div>
      </header>

      <nav class="category-tabs">
        <button 
          v-for="cat in categories" 
          :key="cat.id"
          class="tab-btn"
          :class="{ active: currentCategory === cat.id }"
          @click="currentCategory = cat.id"
        >
          {{ cat.label }}
        </button>
      </nav>

      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>상점의 문을 여는 중입니다...</p>
      </div>

      <div v-else class="item-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id" 
          class="item-card"
          :class="{ 'is-owned': item.owned }"
        >
          <div class="item-image-wrapper">
            <img :src="item.imageUrl" :alt="item.name" class="item-img"/>
            <div v-if="item.owned" class="owned-overlay">
              <span class="check-icon">✓</span>
            </div>
          </div>

          <div class="item-info">
            <span class="item-category">{{ getCategoryLabel(item.category) }}</span>
            <h3 class="item-name">{{ item.name }}</h3>
            
            <button 
              class="action-btn"
              :class="item.owned ? 'btn-owned' : 'btn-buy'"
              @click="handleBuy(item)"
              :disabled="item.owned"
            >
              <template v-if="item.owned">
                보유중
              </template>
              <template v-else>
                <i class="fa-solid fa-coins btn-coin-icon"></i> {{ item.price.toLocaleString() }}
              </template>
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="!isLoading && filteredItems.length === 0" class="empty-state">
        <i class="fa-solid fa-box empty-icon"></i>
        <p>해당 카테고리에 아이템이 없습니다.</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getShopItems, buyItem } from '@/api/items.js';
import { getProfile } from '@/api/user.js';

const userCoins = ref(0);
const items = ref([]);
const isLoading = ref(true);

const currentCategory = ref('all');

const categories = [
  { id: 'all', label: '전체' },
  { id: 'hair', label: '헤어' },
  { id: 'hat', label: '모자' },
  { id: 'top', label: '상의' },
  { id: 'bottom', label: '하의' },
  { id: 'face', label: '얼굴' },
  { id: 'skin', label: '스킨' },
  { id: 'etc', label: '기타' },
];

// 카테고리 ID로 한글 라벨 찾기 (헬퍼 함수)
const getCategoryLabel = (catId) => {
  const cat = categories.find(c => c.id === catId);
  return cat ? cat.label : catId;
}

const fetchShopData = async () => {
  isLoading.value = true;
  try {
    const [shopItemsResponse, profileResponse] = await Promise.all([
      getShopItems(),
      getProfile()
    ]);

    if (shopItemsResponse.success) {
      items.value = shopItemsResponse.data;
    }

    if (profileResponse.success) {
      userCoins.value = profileResponse.data.points;
    }

  } catch (error) {
    console.error("상점 정보를 불러오는 데 실패했습니다.", error);
    alert("상점 정보를 불러오는 데 실패했습니다. 다시 시도해주세요.");
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchShopData);

const filteredItems = computed(() => {
  if (currentCategory.value === 'all') {
    return items.value;
  }
  return items.value.filter(item => item.category === currentCategory.value);
});

const handleBuy = async (item) => {
  if (item.owned) return;

  if (confirm(`'${item.name}'을(를) ${item.price}코인에 구매하시겠습니까?`)) {
    try {
      const response = await buyItem(item.id);
      if (response.success) {
        alert("구매 완료!");
        await fetchShopData(); 
      }
    } catch (error) {
      const errorMessage = error.response?.data?.message || "알 수 없는 오류가 발생했습니다.";
      alert(`구매 실패: ${errorMessage}`);
      console.error("구매 처리 중 오류 발생:", error);
    }
  }
};
</script>

<style scoped>
/* 기본 폰트 및 배경 설정 */
.shop-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  width: 100%;
  height: 100%; /* 부모 높이 상속 */
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
  
  /* 스크롤 설정 추가 (모바일 필수) */
  overflow-y: auto; 
  -webkit-overflow-scrolling: touch;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: fit-content;
  min-height: 100%;
}

/* --- Header --- */
.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 8px; /* 헤더 아래 여백 추가 */
}

.header-left {
  display: flex;
  flex-direction: column;
}

.badge {
  display: inline-flex;
  align-items: center;
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 12px;
  width: fit-content;
}
.badge-dot {
  width: 6px;
  height: 6px;
  background-color: #4f46e5;
  border-radius: 50%;
  margin-right: 6px;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.page-desc {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

/* Coin Card */
.coin-card {
  background: #fff;
  padding: 16px 24px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
  min-width: 180px;
}
.coin-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
  margin-bottom: 4px;
}
.coin-value-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.coin-icon { font-size: 20px; }
.coin-amount {
  font-size: 24px;
  font-weight: 800;
  color: #f59e0b;
}

/* --- Category Tabs (수정됨) --- */
.category-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  
  /* 패딩을 넉넉히 주어 잘림 방지 */
  padding: 4px 4px 16px 4px; 
  margin-bottom: 8px;
  
  /* 스크롤바 숨김 */
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; 
  
  /* 가려짐 방지를 위해 z-index와 relative 추가 */
  position: relative;
  z-index: 10;
}
.category-tabs::-webkit-scrollbar {
  display: none;
}

.tab-btn {
  background: #fff;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  font-family: inherit;
  
  /* 버튼 형태 유지 */
  flex-shrink: 0;
}

.tab-btn:hover {
  background: #f8fafc;
  color: #334155;
}

.tab-btn.active {
  background: #1e293b;
  color: #fff;
  border-color: #1e293b;
  box-shadow: 0 4px 10px rgba(30, 41, 59, 0.2);
}

/* --- Item Grid --- */
.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  padding-bottom: 40px; /* 바닥 여백 추가 */
}

.item-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
}

.item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.08);
}

/* Image Area */
.item-image-wrapper {
  background: #f8fafc;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 20px;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: transform 0.2s;
}

.item-card:hover .item-img {
  transform: scale(1.05);
}

.owned-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  background: #22c55e;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* Info Area */
.item-info {
  padding: 20px;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.item-category {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 600;
  margin-bottom: 4px;
}

.item-name {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
  line-height: 1.4;
}

/* Action Button */
.action-btn {
  margin-top: auto; 
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s;
  font-family: inherit;
}

.btn-buy {
  background: #eff6ff;
  color: #2563eb;
}
.btn-buy:hover {
  background: #2563eb;
  color: #fff;
}

.btn-owned {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: default;
}

.btn-coin-icon { font-size: 16px; }

/* --- Loading & Empty States --- */
.loading-state {
  text-align: center;
  padding: 60px 0;
  color: #64748b;
}
.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #94a3b8;
}
.empty-icon { font-size: 48px; display: block; margin-bottom: 16px; opacity: 0.5; }

/* ------------------------------------------- */
/* ★ 반응형 미디어 쿼리 수정 ★ */
/* ------------------------------------------- */

/* 모바일 (640px 이하) */
@media (max-width: 640px) {
  .content-container {
    padding: 24px 16px; /* 좌우 여백 축소 */
  }

  .shop-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }

  .coin-card {
    width: 100%;
  }
  
  /* 탭 영역 여백 확보 */
  .category-tabs {
    padding-bottom: 12px;
    margin-bottom: 16px;
  }

  .item-grid {
    grid-template-columns: repeat(2, 1fr); /* 2열 고정 */
    gap: 12px;
  }
  
  .item-image-wrapper {
    height: 140px;
    padding: 10px;
  }
  
  .item-info {
    padding: 16px;
  }
  
  .item-name {
    font-size: 14px;
  }
  
  .action-btn {
    padding: 10px;
    font-size: 13px;
  }
}
</style>