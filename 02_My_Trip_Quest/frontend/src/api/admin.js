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