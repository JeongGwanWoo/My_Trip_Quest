import api from './index';

/**
 * 지도에 표시될 지역별 퀘스트 현황 데이터를 가져옵니다.
 * @returns {Promise<any>}
 */
export const getAreaQuestStatus = () => {
    return api.get('/api/v1/quest-map/areas');
};

/**
 * 특정 지역의 퀘스트가 있는 관광지 목록을 가져옵니다.
 * @param {string} areaCode 지역 코드
 * @param {number} page 페이지 번호
 * @param {number} size 페이지 크기
 * @param {string|null} keyword 검색어
 * @returns {Promise<any>}
 */
export const getLocationsByArea = (areaCode, page = 0, size = 10, keyword = null) => {
    const params = { page, size };
    if (keyword) {
        params.keyword = keyword;
    }
    return api.get(`/api/v1/quest-map/areas/${areaCode}`, { params });
};

/**
 * 특정 관광지의 퀘스트 목록을 가져옵니다.
 * @param {number} locationId 관광지 ID
 * @returns {Promise<any>}
 */
export const getQuestsByLocation = (locationId) => {
    return api.get(`/api/v1/quest-map/locations/${locationId}`);
};

/**
 * 퀘스트를 수락합니다.
 * @param {number} questId 퀘스트 ID
 * @returns {Promise<any>}
 */
export const acceptQuest = (questId) => {
    return api.post(`/api/v1/quest-map/quests/${questId}/accept`);
};


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

/**
 * 사진 퀘스트 완료를 서버에 요청합니다.
 * @param {number} questId - 완료할 퀘스트의 ID
 * @param {File} imageFile - 사용자가 업로드한 이미지 파일
 * @returns {Promise<any>} API 응답 객체
 */
export const completePhotoQuest = (questId, imageFile) => {
  const formData = new FormData();
  formData.append('image', imageFile);

  return api.post(`/api/v1/quest-map/quests/${questId}/complete/photo`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

/**
 * 현재 진행중인 퀘스트 목록을 가져옵니다.
 * @returns {Promise<any>}
 */
export const getOngoingQuests = () => {
    return api.get('/api/v1/quest-map/quests/in-progress');
};