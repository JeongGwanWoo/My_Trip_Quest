<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <span class="logo-icon">✨</span>
        <h1 class="logo-text">회원가입</h1>
        <p class="sub-text">새로운 여행의 시작을 함께하세요</p>
      </div>

      <form @submit.prevent="handleSignup" class="auth-form">
        
        <div class="form-group">
          <label for="nickname" class="form-label">닉네임</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              id="nickname" 
              v-model="nickname" 
              class="form-input" 
              placeholder="사용할 닉네임을 입력하세요"
              required
            >
          </div>
        </div>

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

        <div class="form-group">
          <label for="confirmPassword" class="form-label">비밀번호 확인</label>
          <div class="input-wrapper">
            <span class="input-icon">🛡️</span>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="confirmPassword" 
              class="form-input" 
              placeholder="비밀번호를 다시 입력하세요"
              required
            >
          </div>
        </div>
        
        <button type="submit" class="btn-signup" :disabled="isLoading">
          <span v-if="!isLoading">회원가입 완료</span>
          <span v-else class="spinner"></span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p class="login-text">
          이미 계정이 있으신가요? 
          <router-link to="/login" class="link-highlight">로그인</router-link>
        </p>
        <router-link to="/" class="link-home">홈으로 돌아가기</router-link>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api';

const router = useRouter();
const nickname = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);

const handleSignup = async () => {
  if (!nickname.value || !email.value || !password.value || !confirmPassword.value) {
    alert('모든 필드를 입력해주세요.');
    return;
  }
  if (password.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  
  isLoading.value = true;

  try {
    await api.post('/api/v1/users/register', {
      nickname: nickname.value,
      email: email.value,
      password: password.value,
    });
    
    alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
    router.push('/login');

  } catch (error) {
    console.error('회원가입 오류:', error);
    const msg = error.response?.data?.message || '잠시 후 다시 시도해주세요.';
    alert(`회원가입 실패: ${msg}`);
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
  
  /* 레이아웃 뭉개짐 방지를 위한 핵심 설정 */
  width: 100%; 
  
  background-color: #f5f7fb;
  padding: 20px;
}

/* 카드 컨테이너 */
.auth-card {
  background: white;
  width: 100%;
  max-width: 460px; /* 로그인보다 입력 필드가 많아서 조금 더 넓게 */
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}

/* 로고 영역 */
.logo-area {
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 8px;
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
  gap: 20px;
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

.form-input::placeholder {
  color: #cbd5e1;
}

/* 회원가입 버튼 */
.btn-signup {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 700;
  color: white;
  background-color: #1e293b; /* 로그인 버튼과 차별화를 위해 다크 네이비 사용 */
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s, transform 0.1s;
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 52px;
}

.btn-signup:hover:not(:disabled) {
  background-color: #334155;
  transform: translateY(-1px);
}

.btn-signup:active:not(:disabled) {
  transform: translateY(0);
}

.btn-signup:disabled {
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

.login-text {
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
    box-shadow: none;
    background: transparent;
  }
  .auth-page {
      background-color: #fff;
  }
}
</style>