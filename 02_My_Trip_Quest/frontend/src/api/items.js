import api from './index';

export const getAllItems = async () => {
  try {
    // [수정 전] const response = await api.get('/api/items');
    
    // [수정 후] 뒤에 '/inventory'를 붙여서 내 옷장 데이터를 요청합니다.
    const response = await api.get('/api/items/inventory'); 
    
    return response.data;
  } catch (error) {
    console.error('Error fetching all items:', error);
    throw error;
  }


};

export const equipItemApi = async (itemId) => {
  try {
    // 백엔드로 { itemId: 5 } 형태의 데이터 전송
    await api.post('/api/avatar/equip', { itemId });
  } catch (error) {
    console.error('장착 실패:', error);
    throw error;
  }
};

export const unequipItemApi = async (slot) => {
  try {
    // Body: { slot: "HAIR" }
    await api.post('/api/avatar/unequip', { slot });
  } catch (error) {
    console.error('해제 요청 실패:', error);
    throw error;
  }
};