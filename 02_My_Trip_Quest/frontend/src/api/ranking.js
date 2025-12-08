import api from './index';

/**
 * 글로벌 랭킹 목록을 가져옵니다.
 * @param {number} limit 조회할 랭킹 개수 (기본값 10)
 * @returns {Promise<object>} ApiResponse
 */
export const getRankings = async (limit = 10) => {
  try {
    const response = await api.get(`/api/v1/rankings?limit=${limit}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching rankings:', error);
    throw error;
  }
};

/**
 * 현재 로그인한 사용자의 랭킹 정보를 가져옵니다.
 * @param {number} userId 사용자 ID
 * @returns {Promise<object>} ApiResponse
 */
export const getMyRank = async (userId) => {
  try {
    const response = await api.get(`/api/v1/rankings/my-rank?userId=${userId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching my rank:', error);
    throw error;
  }
};
