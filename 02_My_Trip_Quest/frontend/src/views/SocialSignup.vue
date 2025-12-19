<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <span class="logo-icon"><i class="fa-solid fa-sparkles"></i></span>
        <h1 class="logo-text">추가 정보 입력</h1>
        <p class="sub-text">새로운 여행을 위한 닉네임을 설정해주세요</p>
      </div>

      <form @submit.prevent="completeSignup" class="auth-form">
        <div class="form-group">
          <label for="nickname" class="form-label">닉네임</label>
          <div class="input-wrapper">
            <span class="input-icon"><i class="fa-solid fa-user-tag"></i></span>
            <input 
              type="text" 
              id="nickname" 
              v-model="nickname"
              @input="clearError"
              :class="['form-input', { 'input-error': nicknameError }]" 
              placeholder="사용할 닉네임을 입력하세요"
              required
            >
          </div>
          <Transition name="fade-slide">
            <p v-if="nicknameError" class="error-message">{{ nicknameError }}</p>
          </Transition>
        </div>
        
        <button type="submit" class="btn-signup" :disabled="isLoading">
          <span v-if="!isLoading">가입 완료 및 시작하기</span>
          <span v-else class="spinner"></span>
        </button>
      </form>
      
      <div class="auth-footer">
        <p class="info-text">이미 계정이 있으신가요? 
          <router-link to="/login" class="link-highlight">로그인 페이지로</router-link>
        </p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useToast } from '@/utils/toast';
import { socialSignup } from '@/api/user';

const router = useRouter();
const authStore = useAuthStore();
const { showToast } = useToast();

const nickname = ref('');
const isLoading = ref(false);
const nicknameError = ref('');

// 입력 시 에러 메시지 초기화
const clearError = () => {
  nicknameError.value = '';
};

const completeSignup = async () => {
  if (!nickname.value) {
    showToast('닉네임을 입력해주세요.', 'warning');
    return;
  }

  isLoading.value = true;
  try {
    const registrationToken = authStore.registrationToken;
    if (!registrationToken) {
      throw new Error('인증 토큰을 찾을 수 없습니다.');
    }

    const response = await socialSignup({
      registrationToken,
      nickname: nickname.value,
    });

    // 가입 완료 후 JWT 토큰 처리
    const newJwtToken = response.data.token;
    authStore.login(newJwtToken);
    authStore.clearRegistrationToken();

    showToast('환영합니다! 회원가입이 완료되었습니다.', 'success');
    router.push('/');
    
  } catch (error) {
    console.error('Social signup failed:', error);
    
    // 1. 닉네임 중복 에러 처리 (409 Conflict)
    if (error.response && error.response.status === 409) {
      nicknameError.value = error.response.data.message || '이미 사용 중인 닉네임입니다.';
    } 
    // 2. 인증 토큰 만료 등 기타 에러
    else {
      const errorMessage = error.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해주세요.';
      showToast(errorMessage, 'error');
      
      if (errorMessage.includes('token') || errorMessage.includes('토큰')) {
        router.push('/login');
      }
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 배경 및 전체 레이아웃 */
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

/* 카드 디자인 */
.auth-card {
  background: white;
  width: 100%;
  max-width: 440px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.logo-area {
  margin-bottom: 32px;
}

.logo-icon {
  font-size: 36px;
  color: #3b82f6;
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

/* 폼 및 입력 필드 */
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
}

.form-input:focus {
  background-color: #fff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 닉네임 에러 시 스타일 */
.form-input.input-error {
  border-color: #ef4444;
  background-color: #fffafb;
}

.form-input.input-error:focus {
  box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
}

.error-message {
  color: #ef4444;
  font-size: 13px;
  margin-top: 8px;
  font-weight: 500;
}

/* 버튼 스타일 */
.btn-signup {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 700;
  color: white;
  background-color: #1e293b;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 54px;
}

.btn-signup:hover:not(:disabled) {
  background-color: #334155;
  transform: translateY(-1px);
}

.btn-signup:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

/* 하단 푸터 */
.auth-footer {
  font-size: 14px;
}

.info-text {
  color: #64748b;
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

/* 애니메이션 및 스피너 */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: all 0.3s ease;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

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
</style>