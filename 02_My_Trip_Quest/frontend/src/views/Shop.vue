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
        
        <div class="coin-card" v-if="isLoggedIn">
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
          @click="changeCategory(cat.id)"
        >
          {{ cat.label }}
        </button>
      </nav>

      <div v-if="isLoading && items.length === 0" class="loading-state">
        <div class="spinner"></div>
        <p>상점의 문을 여는 중입니다...</p>
      </div>

      <div v-else-if="items.length > 0" class="item-grid">
        <div 
          v-for="item in filteredItems" 
          :key="item.id" 
          class="item-card"
          :class="{ 'is-owned': item.owned }"
        >
          <div class="item-image-wrapper" @click="handleItemClick(item)">
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

      <div v-if="!isLoading && totalPages > 1" class="pagination-container">
        <button v-if="isMobile && currentPage < totalPages" @click="loadMore" class="load-more-btn" :disabled="isFetchingMore">
          <span v-if="!isFetchingMore">더보기</span>
          <div v-else class="spinner-small"></div>
        </button>
        <Pagination
          v-if="!isMobile"
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-changed="onPageChange"
        />
      </div>

    </div>

    <BaseModal :show="showLoginModal" @close="closeLoginModal">
      <div class="modal-body">
        <h3 class="modal-title">로그인 필요</h3>
        <p class="modal-text">로그인이 필요한 서비스입니다. 로그인 페이지로 이동하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeLoginModal">취소</button>
          <button class="btn-confirm" @click="goToLogin">로그인</button>
        </div>
      </div>
    </BaseModal>

    <BaseModal :show="showPurchaseConfirmModal" @close="closePurchaseConfirmModal">
      <div class="modal-body">
        <h3 class="modal-title">아이템 구매</h3>
        <p class="modal-text" v-if="itemToPurchase">
          '{{ itemToPurchase.name }}'을(를) {{ itemToPurchase.price.toLocaleString() }} 코인에 구매하시겠습니까?
        </p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closePurchaseConfirmModal">취소</button>
          <button class="btn-confirm" @click="executePurchase">구매</button>
        </div>
      </div>
    </BaseModal>

    <BaseModal :show="showPurchaseResultModal" @close="closePurchaseResultModal">
      <div class="modal-body">
        <h3 class="modal-title" :class="{ 'text-green': purchaseResultType === 'success', 'text-red': purchaseResultType === 'error' }">
          {{ purchaseResultType === 'success' ? '구매 완료!' : '구매 실패' }}
        </h3>
        <p class="modal-text">{{ purchaseResultMessage }}</p>
        <div class="modal-actions">
          <button class="btn-confirm" @click="closePurchaseResultModal">확인</button>
        </div>
      </div>
    </BaseModal>

    <BaseModal :show="showPreviewModal" @close="showPreviewModal = false">
      <div class="modal-body preview-modal-body">
        <h3 class="modal-title">아이템 미리보기</h3>
        <div class="avatar-preview-container">
          <div class="avatar-stage">
            <div class="stage-bg"></div>
            <div class="avatar-layers">
              <img :src="previewEquipped.SKIN?.imageUrl || '/assets/avatar/skin-base.png'" alt="skin" class="layer skin"/>
              <img v-if="previewEquipped.BOTTOM" :src="previewEquipped.BOTTOM.imageUrl" class="layer bottom"/>
              <img v-if="previewEquipped.TOP" :src="previewEquipped.TOP.imageUrl" class="layer top"/>
              <img v-if="previewEquipped.FACE" :src="previewEquipped.FACE.imageUrl" class="layer face"/>
              <img v-if="previewEquipped.HAIR" :src="previewEquipped.HAIR.imageUrl" class="layer hair"/>
              <img v-if="previewEquipped.HAT" :src="previewEquipped.HAT.imageUrl" class="layer hat"/>
            </div>
          </div>
          <p class="preview-item-name">{{ currentPreviewItem?.name }}</p>
        </div>
        <div class="modal-actions">
          <button class="btn-confirm" @click="showPreviewModal = false">닫기</button>
        </div>
      </div>
    </BaseModal>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';
import { getShopItems, buyItem, getMyInventory } from '@/api/items.js';
import { getProfile } from '@/api/user.js';
import BaseModal from '@/components/ui/BaseModal.vue';
import Pagination from '@/components/ui/Pagination.vue';
import { useBreakpoints } from '@/utils/useBreakpoints.js';

const router = useRouter();
const authStore = useAuthStore();
const { isLoggedIn } = storeToRefs(authStore);
const { isMobile } = useBreakpoints();

// Component State
const userCoins = ref(0);
const items = ref([]);
const isLoading = ref(true);
const isFetchingMore = ref(false);

const previewEquipped = ref({
  SKIN: null,
  HAIR: null,
  HAT: null,
  TOP: null,
  BOTTOM: null,
  FACE: null,
});

// Pagination State
const currentPage = ref(1);
const totalPages = ref(1);
const totalItems = ref(0);
const pageSize = 12;

// Modal State
const showLoginModal = ref(false);
const showPurchaseConfirmModal = ref(false);
const showPurchaseResultModal = ref(false);
const purchaseResultMessage = ref('');
const purchaseResultType = ref('');
const itemToPurchase = ref(null);
const showPreviewModal = ref(false); 
const currentPreviewItem = ref(null); 

// Category State
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

const getCategoryLabel = (catId) => {
  const cat = categories.find(c => c.id === catId);
  return cat ? cat.label : catId;
}

const fetchShopItemsData = async (page = 1, loadMore = false) => {
    if (!loadMore) {
      isLoading.value = true;
    } else {
      isFetchingMore.value = true;
    }

  try {
    const response = await getShopItems(page - 1, pageSize, currentCategory.value);
    if (response.success) {
      const data = response.data;
      if (loadMore) {
        items.value.push(...data.items);
      } else {
        items.value = data.items;
      }
      currentPage.value = data.currentPage + 1;
      totalPages.value = data.totalPages;
      totalItems.value = data.totalItems;
    }
  } catch(error) {
    console.error("아이템 목록을 불러오는 데 실패했습니다.", error);
  } finally {
    isLoading.value = false;
    isFetchingMore.value = false;
  }
};

const fetchUserData = async () => {
  if (!isLoggedIn.value) return;
  try {
    const profileResponse = await getProfile();
    if (profileResponse.success) {
      userCoins.value = profileResponse.data.points;
    }
  } catch (error) {
    console.error("사용자 정보를 불러오는 데 실패했습니다.", error);
  }
};

onMounted(async () => {
  await fetchShopItemsData(1);
  await fetchUserData();
  if (isLoggedIn.value) {
    try {
      const inventoryData = await getMyInventory();
      inventoryData.forEach(userItem => {
        if (userItem.equipped && userItem.item) {
          const { item } = userItem;
          const slot = item.slot.toUpperCase();
          if (previewEquipped.value.hasOwnProperty(slot)) {
            previewEquipped.value[slot] = item;
          }
        }
      });
      if (!previewEquipped.value.SKIN) {
        previewEquipped.value.SKIN = { imageUrl: '/assets/avatar/skin-base.png' };
      }
    } catch (error) {
      console.error("Error fetching inventory for preview:", error);
    }
  }
});

const handleItemClick = (item) => {
  if (!item || !item.category) return;
  const slot = item.category.toUpperCase();
  if (previewEquipped.value.hasOwnProperty(slot)) {
    // Create a new object to ensure reactivity
    previewEquipped.value[slot] = { ...item };
    currentPreviewItem.value = item; 
    showPreviewModal.value = true; 
  }
};

const filteredItems = computed(() => {
  return items.value;
});

const changeCategory = (categoryId) => {
  currentCategory.value = categoryId;
  items.value = [];
  fetchShopItemsData(1);
};


const onPageChange = (page) => {
  window.scrollTo(0, 0);
  fetchShopItemsData(page);
};

const loadMore = () => {
  if (currentPage.value < totalPages.value) {
    fetchShopItemsData(currentPage.value + 1, true);
  }
};


const closeLoginModal = () => {
  showLoginModal.value = false;
};

const goToLogin = () => {
  router.push('/login');
  closeLoginModal();
};

const closePurchaseConfirmModal = () => {
  showPurchaseConfirmModal.value = false;
  itemToPurchase.value = null;
};

const closePurchaseResultModal = async () => {
  showPurchaseResultModal.value = false;
  purchaseResultMessage.value = '';
  purchaseResultType.value = '';
  if (isLoggedIn.value) {
    await fetchShopItemsData(currentPage.value);
    await fetchUserData();
  }
};

const executePurchase = async () => {
  if (!itemToPurchase.value) return;
  const targetItem = itemToPurchase.value;
  showPurchaseConfirmModal.value = false;

  try {
    const response = await buyItem(targetItem.id);
    if (response.success) {
      purchaseResultMessage.value = `'${targetItem.name}'을(를) 구매 완료했습니다!`;
      purchaseResultType.value = 'success';
    } else {
      purchaseResultMessage.value = response.message || "구매에 실패했습니다.";
      purchaseResultType.value = 'error';
    }
  } catch (error) {
    console.error("구매 처리 중 오류 발생:", error);
    purchaseResultMessage.value = error.response?.data?.message || "알 수 없는 오류가 발생했습니다.";
    purchaseResultType.value = 'error';
  } finally {
    showPurchaseResultModal.value = true;
    itemToPurchase.value = null; 
  }
};

const handleBuy = async (item) => {
  if (item.owned) return;
  if (!isLoggedIn.value) {
    showLoginModal.value = true;
    return;
  }
  itemToPurchase.value = item;
  showPurchaseConfirmModal.value = true;
};
</script>

<style scoped>
/* 기본 폰트 및 배경 설정 */
.shop-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  width: 100%;
  height: 100%; 
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
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
  margin-bottom: 8px; 
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

/* --- Category Tabs --- */
.category-tabs {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 4px 4px 16px 4px; 
  margin-bottom: 8px;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; 
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
  padding-bottom: 40px; 
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

/* --- Pagination & Load More --- */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.load-more-btn {
  width: 100%;
  max-width: 300px;
  padding: 14px;
  font-size: 16px;
  font-weight: 700;
  color: #334155;
  background-color: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.load-more-btn:hover:not(:disabled) {
  background-color: #f8fafc;
}
.load-more-btn:disabled {
    cursor: not-allowed;
    opacity: 0.7;
}
.spinner-small {
    width: 20px;
    height: 20px;
    margin: 0 auto;
    border: 2px solid #334155;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}


/* Item Preview Modal Styles */
.preview-modal-body {
  padding: 20px;
  max-width: 320px; 
  margin: 0 auto;
}

.avatar-preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

/* Avatar Styles (Adjusted with padding) */
.avatar-stage {
  width: 280px; height: 280px; 
  position: relative; display: block;
  margin-bottom: 15px; 
}
.stage-bg {
  position: absolute; top: 50%; left: 50%; 
  transform: translate(-50%, -50%);
  width: 220px; height: 220px;
  background: radial-gradient(circle, #eff6ff 0%, #fff 70%);
  border-radius: 50%; 
  z-index: 0;
}
.avatar-layers { 
  position: absolute; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  z-index: 1; 
  /* Added padding to prevent cutoff */
  padding: 20px;
}
.layer { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
.layer > img {
  width: 100%; height: 100%; 
  object-fit: contain; display: block;
  filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
}
.skin { z-index: 10; }
.bottom { z-index: 20; }
.top { z-index: 30; }
.face { z-index: 40; }
.hair { z-index: 50; }
.hat { z-index: 60; }

.preview-item-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  text-align: center;
}

/* Modal Body Styles */
.modal-body {
  text-align: center;
  padding: 20px;
}

.modal-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 10px;
}

.modal-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 25px;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.modal-actions button {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  min-width: 100px;
  transition: background-color 0.2s;
}

.btn-cancel {
  background-color: #f1f5f9;
  color: #334155;
}
.btn-cancel:hover {
  background-color: #e2e8f0;
}

.btn-confirm {
  background-color: #3b82f6;
  color: white;
}
.btn-confirm:hover {
  background-color: #2563eb;
}

/* 모바일 (640px 이하) - Added missing @media block */
@media (max-width: 640px) {
  .content-container {
    padding: 24px 16px;
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
  
  .category-tabs {
    padding-bottom: 12px;
    margin-bottom: 16px;
  }

  .item-grid {
    grid-template-columns: repeat(2, 1fr); 
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