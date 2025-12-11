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

export const updateProfile = async (updateData) => {
  try {
    const response = await api.patch('/api/v1/users/me', updateData);
    return response.data;
  } catch (error) {
    console.error('Error updating user profile:', error);
    throw error;
  }
};

export const deleteAccount = async () => {
  try {
    const response = await api.delete('/api/v1/users/me');
    return response.data;
  } catch (error) {
    console.error('Error deleting account:', error);
    throw error;
  }
};
