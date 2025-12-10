<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <Logo width="220" height="50" />
        <p class="sub-text">여행의 즐거움을 더하다</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="email" class="form-label">이메일</label>
          <div class="input-wrapper">
            <span class="input-icon">✉️</span>
            <input 
              type="email" 
              id="email" 
              v-model="email" 
              class="form-input" 
              placeholder="example@email.com"
              required
            >
          </div>
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">비밀번호</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              type="password" 
              id="password" 
              v-model="password" 
              class="form-input" 
              placeholder="비밀번호를 입력하세요"
              required
            >
          </div>
        </div>
        
        <button type="submit" class="btn-login" :disabled="isLoading">
          <span v-if="!isLoading">로그인</span>
          <span v-else class="spinner"></span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p class="signup-text">
          아직 계정이 없으신가요? 
          <router-link to="/signup" class="link-highlight">회원가입</router-link>
        </p>
        <router-link to="/" class="link-home">홈으로 돌아가기</router-link>
      </div>

    </div>
  </div>
</template>

<script setup>
  import Logo from '@/components/common/Logo.vue';
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import api from '@/api';

const authStore = useAuthStore();
const router = useRouter();

const email = ref('');
const password = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  if (!email.value || !password.value) {
    alert('이메일과 비밀번호를 모두 입력해주세요.');
    return;
  }

  isLoading.value = true; // 로딩 시작

  try {
    const response = await api.post('/api/v1/users/login', {
      email: email.value,
      password: password.value,
    });
    
    // console.log('백엔드 응답:', response.data);

    // 실제 백엔드 응답 구조에 맞게 토큰 경로 수정
    // 예: response.data.data.accessToken 등 확인 필요
    const token = response.data.data.token || response.data.data.accessToken; 
    
    if (token) {
      authStore.login(token);
      router.push('/quest-map');
    } else {
      throw new Error('토큰을 찾을 수 없습니다.');
    }

  } catch (error) {
    console.error('로그인 오류:', error);
    const msg = error.response?.data?.message || '이메일 또는 비밀번호를 확인해주세요.';
    alert(`로그인 실패: ${msg}`);
  } finally {
    isLoading.value = false; // 로딩 종료
  }
};
</script>

<style scoped>

  
/* 폰트 및 기본 배경 설정 */
.auth-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  width: 100%; 
  background-color: #f5f7fb; /* 부드러운 회색 배경 */
  padding: 20px;
}

/* 카드 컨테이너 */
.auth-card {
  background: white;
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); /* 부드러운 그림자 */
  text-align: center;
}

/* 로고 영역 */
.logo-area {
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.logo-text {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.sub-text {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 폼 스타일 */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
}

.form-group {
  text-align: left;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  font-size: 16px;
  color: #94a3b8;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 14px 14px 14px 44px; /* 아이콘 공간 확보 */
  font-size: 15px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background-color: #f8fafc;
  color: #1e293b;
  outline: none;
  transition: all 0.2s;
  font-family: inherit;
}

.form-input:focus {
  background-color: #fff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input::placeholder {
  color: #cbd5e1;
}

/* 로그인 버튼 */
.btn-login {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 700;
  color: white;
  background-color: #2563eb;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
  margin-top: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 52px;
}

.btn-login:hover:not(:disabled) {
  background-color: #1d4ed8;
  transform: translateY(-1px);
}

.btn-login:active:not(:disabled) {
  transform: translateY(0);
}

.btn-login:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

/* 푸터 링크 */
.auth-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-size: 14px;
}

.signup-text {
  color: #64748b;
  margin: 0;
}

.link-highlight {
  color: #2563eb;
  font-weight: 700;
  text-decoration: none;
  margin-left: 4px;
}

.link-highlight:hover {
  text-decoration: underline;
}

.link-home {
  color: #94a3b8;
  font-size: 13px;
  text-decoration: none;
  margin-top: 8px;
}

.link-home:hover {
  color: #64748b;
}

/* 로딩 스피너 */
.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 반응형 */
@media (max-width: 480px) {
  .auth-card {
    padding: 32px 24px;
    box-shadow: none; /* 모바일에서는 그림자 제거하고 평면 느낌 */
    background: transparent;
  }
  .auth-page {
      background-color: #fff; /* 모바일 배경 흰색 */
  }
}
</style>