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
 * * [수정됨] 카테고리 필터링을 위해 category 파라미터가 추가되었습니다.
 * * @param {number} page 요청할 페이지 번호 (0-based)
 * @param {number} size 페이지 당 아이템 수
 * @param {string} category (선택) 필터링할 카테고리 (예: 'HAIR', 'TOP'). 'all'이거나 없으면 전체 조회.
 * @returns {Promise<object>} ApiResponse
 */
export const getShopItems = async (page = 0, size = 12, category) => {
  try {
    // 1. 기본 파라미터 설정
    const params = { page, size };

    // 2. category가 유효하고 'all'이 아닐 경우에만 params에 추가
    // 백엔드 컨트롤러에서는 @RequestParam(required = false) String category 로 받아야 함
    if (category && category !== 'all') {
      params.category = category;
    }

    const response = await api.get('/api/v1/items/shop', {
      params // { page: 0, size: 12, category: 'HAIR' } 형태로 전송됨
    });
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