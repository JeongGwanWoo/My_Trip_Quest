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