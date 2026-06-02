import axios from 'axios';
// 팀원이 추가한 Pinia 스토어 import (이거 꼭 있어야 합니다!)
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  // Vite의 프록시 설정을 사용하므로 baseURL을 제거합니다.
  // 이제 모든 요청은 '/api/...'와 같은 상대 경로로 시작해야 합니다.
  withCredentials: true, 
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    // ★ [해결] 팀원 코드 채택 (Pinia 사용)
    // 이유: 토큰 관리 로직이 변경되었거나, 중앙에서 관리하기 위함입니다.
    const authStore = useAuthStore();
    const token = authStore.token;

    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

export default api;