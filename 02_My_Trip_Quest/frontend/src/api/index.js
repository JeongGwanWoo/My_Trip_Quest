import axios from 'axios';
// 팀원이 추가한 Pinia 스토어 import (이거 꼭 있어야 합니다!)
import { useAuthStore } from '@/stores/auth';

const api = axios.create({
  // ★ 주의: 만약 프록시 설정이 없다면 'http://localhost:8080'으로 바꿔야 할 수도 있습니다.
  baseURL: 'http://localhost:8080', 
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