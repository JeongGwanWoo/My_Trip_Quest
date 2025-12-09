import api from './index.js';

/**
 * 현재 로그인한 사용자의 아바타 정보(착용 아이템 목록)를 가져옵니다.
 * @returns {Promise<object>} - API 응답 데이터
 */
export const getAvatar = async () => {
  try {
    // The backend now gets the user from the security context, so no userId is needed.
    const response = await api.get(`/api/v1/avatar`);
    return response.data;
  } catch (error) {
    console.error('Failed to get avatar:', error);
    throw error;
  }
};

/**
 * 사용자가 아이템을 착용하도록 요청합니다.
 * @param {number} userId - 사용자 ID
 * @param {number} itemId - 착용할 아이템 ID
 * @returns {Promise<object>} - API 응답 데이터
 */
export const equipItem = async (userId, itemId) => {
  try {
    const response = await api.post(`/api/v1/avatar/${userId}/equip`, { itemId });
    return response.data;
  } catch (error) {
    console.error('Failed to equip item:', error);
    throw error;
  }
};