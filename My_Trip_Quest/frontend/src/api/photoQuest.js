import api from './index';

/**
 * 사진 퀘스트 완료를 서버에 요청합니다.
 *
 * @param {number} questId - 완료할 퀘스트의 ID
 * @param {File} imageFile - 사용자가 제출한 인증 사진 파일
 * @param {string | null} [latitude] - 수동으로 획득한 위도 (메타데이터 없을 시)
 * @param {string | null} [longitude] - 수동으로 획득한 경도 (메타데이터 없을 시)
 * @returns {Promise<any>} API 응답 객체
 */
export const completePhotoQuest = (questId, imageFile, latitude = null, longitude = null) => {
  const formData = new FormData();
  formData.append('image', imageFile);
  if (latitude !== null && longitude !== null) {
    formData.append('latitude', latitude);
    formData.append('longitude', longitude);
  }

  return api.post(`/api/v1/quest-map/quests/${questId}/complete/photo`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
