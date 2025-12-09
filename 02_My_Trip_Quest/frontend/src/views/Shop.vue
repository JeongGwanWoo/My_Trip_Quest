<template>
  <div class="shop-page">
    <div class="content-container">
      
      <header class="shop-header">
        <h2 class="page-title">
          <span class="bag-icon">🛍️</span> CHARACTER SHOP
        </h2>
        
        <div class="my-coin-box">
          <span class="icon">🪙</span>
          <span class="value">{{ userCoins }}</span>
        </div>
      </header>

      <nav class="filter-bar">
        <button 
          v-for="cat in categories" 
          :key="cat.id"
          class="filter-btn"
          :class="{ active: currentCategory === cat.id }"
          @click="currentCategory = cat.id"
        >
          {{ cat.label }}
        </button>
      </nav>

      <div v-if="isLoading" class="loading-container">
        <p>아이템을 불러오는 중...</p>
      </div>

      <div v-else class="item-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id" 
          class="item-card"
          :class="{ 'is-owned': item.owned }"
        >
          <div v-if="item.owned" class="owned-badge">✓ OWNED</div>

          <div class="item-image">
            <img :src="item.imageUrl" :alt="item.name" style="width: 100%; height: 100%; object-fit: contain;"/>
          </div>

          <h3 class="item-name">{{ item.name }}</h3>
          
          <div class="item-category-tag">
            {{ item.category.toUpperCase() }}
          </div>

          <button 
            class="action-btn"
            :class="item.owned ? 'inventory-btn' : 'buy-btn'"
            @click="handleBuy(item)"
            :disabled="item.owned"
          >
            <template v-if="item.owned">
              ✓ IN INVENTORY
            </template>
            <template v-else>
              <span class="coin-icon-small">🪙</span> {{ item.price }}
            </template>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getShopItems, buyItem } from '@/api/items.js';
import { getProfile } from '@/api/user.js'; // getAvatar 대신 getProfile import

const userCoins = ref(0);
const items = ref([]);
const isLoading = ref(true);

const currentCategory = ref('all');

const categories = [
  { id: 'all', label: 'ALL ITEMS' },
  { id: 'hair', label: '머리' },
  { id: 'hat', label: '모자' },
  { id: 'top', label: '상의' },
  { id: 'bottom', label: '하의' },
  { id: 'face', label: '얼굴' },
  { id: 'skin', label: '스킨' },
  { id: 'etc', label: '기타' },
];

const fetchShopData = async () => {
  isLoading.value = true;
  try {
    // getAvatar 호출을 getProfile 호출로 변경
    const [shopItemsResponse, profileResponse] = await Promise.all([
      getShopItems(),
      getProfile()
    ]);

    if (shopItemsResponse.success) {
      items.value = shopItemsResponse.data;
    }

    // getProfile 응답에서 코인(포인트) 정보를 가져옴
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
        // 성공 시 상점 데이터 다시 로드하여 코인과 아이템 소유 상태 갱신
        await fetchShopData(); 
      }
    } catch (error) {
      // API 에러 응답이 'error.response.data'에 담겨있다고 가정
      const errorMessage = error.response?.data?.message || "알 수 없는 오류가 발생했습니다.";
      alert(`구매 실패: ${errorMessage}`);
      console.error("구매 처리 중 오류 발생:", error);
    }
  }
};
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
  color: #e2e8f0;
  font-size: 18px;
}

.shop-page {
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

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  color: #fbbf24;
  -webkit-text-stroke: 1px #000; 
  text-shadow: 3px 3px 0 #000;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  padding: 10px 20px;
  border: 4px solid #000;
  box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
}

.my-coin-box {
  background-color: #fbbf24;
  border: 3px solid #000;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
}

.filter-bar {
  background-color: #1e293b;
  padding: 20px;
  border: 4px solid #000;
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
}

.filter-btn {
  background-color: #111827;
  color: #6b7280;
  border: 2px solid #374151;
  padding: 10px 20px;
  font-family: inherit;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  text-transform: uppercase;
  font-weight: bold;
  transition: all 0.2s;
}

.filter-btn:hover {
  background-color: #374151;
}

.filter-btn.active {
  background-color: #3b82f6;
  color: white;
  border-color: #60a5fa;
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.5);
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}


.item-card {
  background-color: #8b5cf6;
  border: 4px solid #000;
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  box-shadow: 5px 5px 0 rgba(0,0,0,0.2);
  transition: transform 0.1s;
}

.item-card.is-owned {
  background-color: #4b5563; 
}

.item-card:hover {
  transform: translateY(-5px);
}

.owned-badge {
  position: absolute;
  top: -10px;
  right: -5px;
  background-color: #22c55e;
  color: white;
  font-size: 8px;
  padding: 4px 6px;
  border: 2px solid #000;
  transform: rotate(5deg);
}

.item-image {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.item-name {
  color: white;
  font-size: 14px;
  text-align: center;
  margin: 0 0 10px 0;
  text-shadow: 1px 1px 0 #000;
}

.item-category-tag {
  background-color: rgba(0,0,0,0.5);
  color: #fbbf24;
  font-size: 8px;
  padding: 4px 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(255,255,255,0.2);
}

.action-btn {
  width: 100%;
  padding: 10px 0;
  font-family: inherit;
  font-size: 12px;
  font-weight: bold;
  border: 3px solid #000;
  cursor: pointer;
  box-shadow: 3px 3px 0 #000;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.action-btn:active {
  transform: translate(3px, 3px);
  box-shadow: none;
}

.buy-btn {
  background-color: #22c55e;
  color: white;
}

.inventory-btn {
  background-color: #6ee7b7;
  color: #064e3b;
  cursor: default;
}
.inventory-btn:active {
  transform: none;
  box-shadow: 3px 3px 0 #000;
}

@media (max-width: 600px) {
  .shop-header { 
    flex-direction: column;
    gap: 15px;
  }
  .item-grid { 
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 15px;
  }
}
</style>