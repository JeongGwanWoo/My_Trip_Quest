import api from './index';

/**
 * 내 인벤토리 아이템 목록을 가져옵니다.
 * @deprecated 상점 목록은 getShopItems를 사용하세요.
 */
export const getMyInventory = async () => {
  try {
    const response = await api.get('/api/v1/items/inventory'); 
    return response.data;
  } catch (error) {
    console.error('Error fetching inventory:', error);
    throw error;
  }
};

// ================= SHOP APIs =================

/**
 * 상점에 표시될 아이템 목록을 가져옵니다. (소유 여부 포함)
 * @returns {Promise<object>} ApiResponse
 */
export const getShopItems = async () => {
  try {
    const response = await api.get('/api/v1/items/shop');
    return response.data;
  } catch (error) {
    console.error('Error fetching shop items:', error);
    throw error;
  }
};

/**
 * 아이템 구매를 요청합니다.
 * @param {number} itemId 구매할 아이템의 ID
 * @returns {Promise<object>} ApiResponse
 */
export const buyItem = async (itemId) => {
  try {
    const response = await api.post(`/api/v1/items/${itemId}/buy`);
    return response.data;
  } catch (error) {
    console.error(`Error purchasing item ${itemId}:`, error);
    throw error;
  }
};

// ================= Avatar APIs (from item context) =================

export const equipItemApi = async (itemId) => {
  try {
    // 백엔드로 { itemId: 5 } 형태의 데이터 전송
    await api.post('/api/v1/avatar/equip', { itemId });
  } catch (error) {
    console.error('장착 실패:', error);
    throw error;
  }
};

export const unequipItemApi = async (slot) => {
  try {
    // Body: { slot: "HAIR" }
    await api.post('/api/v1/avatar/unequip', { slot });
  } catch (error) {
    console.error('해제 요청 실패:', error);
    throw error;
  }
};