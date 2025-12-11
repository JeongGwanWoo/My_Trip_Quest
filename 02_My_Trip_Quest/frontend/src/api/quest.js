import api from './index';

/**
 * 도착 퀘스트 완료를 서버에 요청합니다.
 * @param {number} questId - 완료할 퀘스트의 ID
 * @param {number} latitude - 사용자의 현재 위도
 * @param {number} longitude - 사용자의 현재 경도
 * @returns {Promise<any>} API 응답 객체
 */
export const completeArrivalQuest = (questId, latitude, longitude) => {
  return api.post(`/api/v1/quest-map/quests/${questId}/complete/arrival`, {
    latitude,
    longitude,
  });
};

/**
 * 퀘스트 포기를 서버에 요청합니다.
 * @param {number} questId - 포기할 퀘스트의 ID
 * @returns {Promise<any>} API 응답 객체
 */
export const forfeitQuest = (questId) => {
  return api.post(`/api/v1/quest-map/quests/${questId}/forfeit`);
};
