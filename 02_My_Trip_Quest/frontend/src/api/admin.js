import api from './index';

export function getAllUsers() {
    return api.get('/api/v1/admin/users');
}

export function updateUserRole(userId, role) {
    return api.patch(`/api/v1/admin/users/${userId}/role`, { role });
}

export function getQuestStats() {
    return api.get('/api/v1/admin/stats/quests');
}

export function getDashboardStats() {
    return api.get('/api/v1/admin/dashboard');
}

export function getEconomyStats() {
    return api.get('/api/v1/admin/stats/economy');
}

export function getContentStats() {
    return api.get('/api/v1/admin/stats/content');
}

export function getTourApiAttractions(params) {
    return api.get('/api/v1/admin/quests/tourapi/attractions', { params });
}

export function generateQuests(data) {
    return api.post('/api/v1/admin/quests/generate', data);
}

// AI 반경 추정
export function estimateLocationRadius(data) {
    return api.post('/api/v1/admin/quests/ai/estimate-radius', data);
}

// 관광지 반경 수정
export function updateLocationRadius(locationId, radius) {
    return api.put(`/api/v1/admin/quests/locations/${locationId}`, { radius });
}

// 퀘스트 추가
export function addQuest(locationId, questData) {
    return api.post(`/api/v1/admin/quests/locations/${locationId}/quests`, questData);
}

// 퀘스트 삭제
export function deleteQuest(questId) {
    return api.delete(`/api/v1/admin/quests/${questId}`);
}

// DB에 저장된 관광지 목록 조회 (기존 유저 API 재사용)
export function getDbLocations(params) {
    return api.get(`/api/v1/quest-map/areas/${params.areaCode}`, {
        params: {
            page: params.page,
            size: params.size,
            keyword: params.keyword
        }
    });
}

// 특정 관광지의 퀘스트 목록 조회 (기존 유저 API 재사용)
export function getDbQuests(locationId) {
    return api.get(`/api/v1/quest-map/locations/${locationId}`);
}