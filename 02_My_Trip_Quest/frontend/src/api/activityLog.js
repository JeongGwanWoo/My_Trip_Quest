import api from './index';

/**
 * 현재 로그인한 사용자의 활동 로그를 가져옵니다.
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export const getMyActivityLogs = () => {
    return api.get('/api/v1/activity-logs/me');
};
