import api from './index';

export const getProfile = async () => {
  try {
    // 이제 헤더는 axios 인터셉터가 자동으로 추가해줍니다.
    const response = await api.get('/api/v1/users/me');
    return response.data;
  } catch (error) {
    console.error('Error fetching user profile:', error);
    throw error;
  }
};
