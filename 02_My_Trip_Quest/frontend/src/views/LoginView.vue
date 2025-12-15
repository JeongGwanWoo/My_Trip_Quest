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

      <div class="social-login-section">
        <div class="divider">
          <span>간편 로그인</span>
        </div>
        <div class="social-buttons">
          
          <a href="http://localhost:8080/oauth2/authorization/kakao" class="btn-social btn-kakao">
            <svg class="social-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 3C5.373 3 0 6.608 0 11.059C0 13.88 1.813 16.386 4.568 17.824C4.339 18.666 3.52 20.676 3.42 20.959C3.32 21.242 3.655 21.364 3.868 21.222C4.08 21.081 7.42 18.799 8.019 18.36C9.284 18.683 10.615 18.853 12 18.853C18.627 18.853 24 15.245 24 10.794C24 6.343 18.627 3 12 3Z" />
            </svg>
            <span>카카오로 시작하기</span>
          </a>

          <a href="http://localhost:8080/oauth2/authorization/naver" class="btn-social btn-naver">
            <svg class="social-icon" viewBox="0 0 24 24" fill="currentColor">
              <path d="M16.273 12.845L7.376 0H0v24h7.727V11.155L16.624 24H24V0h-7.727v12.845z"/>
            </svg>
            <span>네이버로 시작하기</span>
          </a>

          <a href="http://localhost:8080/oauth2/authorization/google" class="btn-social btn-google">
             <svg class="social-icon" viewBox="0 0 24 24">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
            </svg>
            <span>Google로 시작하기</span>
          </a>

        </div>
      </div>
      
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

  isLoading.value = true;

  try {
    const response = await api.post('/api/v1/users/login', {
      email: email.value,
      password: password.value,
    });
    
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
    isLoading.value = false;
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
  background-color: #f5f7fb;
  padding: 20px;
}

/* 카드 컨테이너 */
.auth-card {
  background: white;
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}

/* 로고 영역 */
.logo-area {
  margin-bottom: 40px;
}

.sub-text {
  font-size: 14px;
  color: #64748b;
  margin: 10px 0 0 0;
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
  padding: 14px 14px 14px 44px;
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

.btn-login:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

/* --- 소셜 로그인 섹션 --- */
.social-login-section {
  width: 100%;
  margin-top: 2rem;
  margin-bottom: 2rem;
}

.divider {
  display: flex;
  align-items: center;
  color: #94a3b8;
  font-size: 0.85rem;
  margin-bottom: 1.2rem;
}

.divider::before,
.divider::after {
  content: "";
  flex: 1;
  border-bottom: 1px solid #e2e8f0;
}

.divider span {
  padding: 0 10px;
}

.social-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 소셜 버튼 공통 스타일 */
.btn-social {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 50px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 15px;
  text-decoration: none;
  transition: opacity 0.2s;
}

.btn-social:hover {
  opacity: 0.9;
}

.social-icon {
  position: absolute;
  left: 20px; /* 아이콘을 왼쪽에 고정 */
  width: 20px;
  height: 20px;
}

/* 1. 카카오 스타일 */
.btn-kakao {
  background-color: #FEE500;
  color: #191919;
  border: none;
}
.btn-kakao .social-icon {
  fill: #191919;
}

/* 2. 네이버 스타일 */
.btn-naver {
  background-color: #03C75A;
  color: #FFFFFF;
  border: none;
}
.btn-naver .social-icon {
  fill: #FFFFFF;
}

/* 3. 구글 스타일 */
.btn-google {
  background-color: #FFFFFF;
  color: #3c4043;
  border: 1px solid #dadce0;
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

.link-home {
  color: #94a3b8;
  font-size: 13px;
  text-decoration: none;
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
    box-shadow: none;
    background: transparent;
  }
  .auth-page {
      background-color: #fff;
  }
}
</style>