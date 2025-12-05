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

      <div class="item-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id" 
          class="item-card"
          :class="{ 'is-owned': item.owned }"
        >
          <div v-if="item.owned" class="owned-badge">✓ OWNED</div>

          <div class="item-image">
            <img :src="item.image" :alt="item.name" style="width: 100%; height: 100%; object-fit: contain;"/>
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
import { ref, computed } from 'vue';

const userCoins = ref(5000);

const currentCategory = ref('all');

const categories = [
  { id: 'all', label: 'ALL ITEMS' },
  { id: 'hair', label: '머리' },
  { id: 'hat', label: '모자' },
  { id: 'top', label: '상의' },
  { id: 'bottom', label: '하의' },
  { id: 'face', label: '얼굴' },
  { id: 'skin', label: '스킨' },
];

const items = ref([
  { id: 1, name: '기본 스킨', category: 'skin', price: 0, owned: true, image: '/assets/avatar/skin-base.svg' },
  { id: 2, name: '멋진 스킨', category: 'skin', price: 500, owned: false, image: '/assets/avatar/skin-base.svg' }, // 임시 이미지
  { id: 3, name: '행복 스킨', category: 'skin', price: 800, owned: false, image: '/assets/avatar/skin-base.svg' }, // 임시 이미지
  { id: 101, name: '테스트 머리', category: 'hair', price: 300, owned: true, image: '/assets/avatar/testhair.png' },
  { id: 102, name: '긴머리', category: 'hair', price: 500, owned: false, image: '/assets/avatar/testhair.png' }, // 임시 이미지
  { id: 201, name: '테스트 상의', category: 'top', price: 600, owned: true, image: '/assets/avatar/testshirts.png' },
  { id: 202, name: '티셔츠', category: 'top', price: 400, owned: false, image: '/assets/avatar/testshirts.png' }, // 임시 이미지
  { id: 301, name: '테스트 하의', category: 'bottom', price: 700, owned: true, image: '/assets/avatar/testpants.png' },
  { id: 302, name: '반바지', category: 'bottom', price: 500, owned: false, image: '/assets/avatar/testpants.png' }, // 임시 이미지
  { id: 401, name: '모자', category: 'hat', price: 800, owned: false, image: '/assets/avatar/testhair.png' }, // 임시 이미지
  { id: 501, name: '안경', category: 'face', price: 400, owned: false, image: '/assets/avatar/testhair.png' }, // 임시 이미지
]);

const filteredItems = computed(() => {
  if (currentCategory.value === 'all') {
    return items.value;
  }
  return items.value.filter(item => item.category === currentCategory.value);
});

const handleBuy = (item) => {
  if (item.owned) return;

  if (userCoins.value >= item.price) {
    if(confirm(`'${item.name}'을(를) ${item.price}코인에 구매하시겠습니까?`)) {
      userCoins.value -= item.price;
      item.owned = true;
      alert("구매 완료!");
    }
  } else {
    alert("코인이 부족합니다!");
  }
};
</script>

<style scoped>

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
  grid-template-columns: repeat(5, 1fr);
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
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.emoji {
  font-size: 40px;
  filter: drop-shadow(2px 2px 0 rgba(0,0,0,0.3));
}

.item-name {
  color: white;
  font-size: 12px;
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
  font-size: 10px;
  font-weight: bold;
  border: 2px solid #000;
  cursor: pointer;
  box-shadow: 2px 2px 0 #000;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.action-btn:active {
  transform: translate(2px, 2px);
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
  box-shadow: 2px 2px 0 #000;
}

@media (max-width: 1024px) {
  .item-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 600px) {
  .shop-header { 
    flex-direction: row; /* Keep them in a row */
    gap: 10px; /* Reduce gap */
    padding: 10px; /* Reduce header padding */
  }
  .page-title {
    font-size: 16px; /* Smaller font for character shop title */
    padding: 8px 10px; /* Smaller padding */
    gap: 8px; /* Smaller gap */
    -webkit-text-stroke: 0.5px #000; /* Thinner stroke */
    text-shadow: 2px 2px 0 #000; /* Smaller shadow */
  }
  .my-coin-box {
    font-size: 12px; /* Smaller font for coin count */
    padding: 8px 10px; /* Smaller padding */
    gap: 5px; /* Smaller gap */
  }
  .filter-bar { 
    justify-content: flex-start; /* Align to start for scrolling */
    overflow-x: auto; /* Enable horizontal scrolling */
    flex-wrap: nowrap; /* Prevent wrapping */
    padding: 15px 10px; /* Adjust padding */
    gap: 8px; /* Reduce gap between buttons */

    /* Hide scrollbar */
    -ms-overflow-style: none;  /* IE and Edge */
    scrollbar-width: none;  /* Firefox */
  }
  .filter-bar::-webkit-scrollbar {
    display: none; /* Chrome, Safari and Opera */
  }
  .filter-btn {
    flex-shrink: 0; /* Prevent buttons from shrinking */
    font-size: 9px; /* Smaller font size */
    padding: 8px 12px; /* Smaller padding */
  }
  .filter-btn .filter-icon {
    font-size: 12px; /* Smaller icon size */
  }
  .item-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>