<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <h1 class="logo-text">비밀번호 재설정</h1>
        <p class="sub-text">이메일 인증 후 새로운 비밀번호를 설정합니다.</p>
      </div>

      <form @submit.prevent="handleResetPassword" class="auth-form">
        
        <div class="form-group">
          <label for="email" class="form-label">이메일</label>
          <div class="input-group">
            <div class="input-wrapper">
              <span class="input-icon"><i class="fa-solid fa-envelope"></i></span>
              <input 
                type="email" 
                id="email" 
                v-model="email" 
                class="form-input" 
                placeholder="가입한 이메일을 입력하세요"
                :disabled="isCodeSent"
                required
              >
            </div>
            <button type="button" @click="sendVerificationCode" :disabled="!email || isCodeSent" class="btn-check">인증코드 전송</button>
          </div>
        </div>

        <div class="form-group" v-if="isCodeSent">
          <label for="verificationCode" class="form-label">인증코드</label>
          <div class="input-group">
            <div class="input-wrapper">
              <span class="input-icon"><i class="fa-solid fa-shield-check"></i></span>
              <input 
                type="text" 
                id="verificationCode" 
                v-model="verificationCode" 
                class="form-input" 
                placeholder="인증코드 6자리"
                :disabled="isCodeVerified"
                required
              >
            </div>
            <button 
              type="button" 
              @click="verifyCode" 
              :disabled="!verificationCode || isCodeVerified" 
              class="btn-check"
              :class="{ 'btn-success': isCodeVerified }"
            >
              {{ isCodeVerified ? '인증 완료' : '인증 확인' }}
            </button>
          </div>
          <p v-if="verificationMessage" :class="{'verification-success': isCodeVerified, 'verification-error': !isCodeVerified}" class="verification-status">{{ verificationMessage }}</p>
        </div>
        
        <div v-if="isCodeVerified">
          <div class="form-group">
            <label for="newPassword" class="form-label">새 비밀번호</label>
            <div class="input-wrapper">
              <span class="input-icon"><i class="fa-solid fa-lock"></i></span>
              <input 
                type="password" 
                id="newPassword" 
                v-model="newPassword" 
                class="form-input" 
                placeholder="새로운 비밀번호"
                required
              >
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword" class="form-label">비밀번호 확인</label>
            <div class="input-wrapper">
              <span class="input-icon"><i class="fa-solid fa-shield-alt"></i></span>
              <input 
                type="password" 
                id="confirmPassword" 
                v-model="confirmPassword" 
                class="form-input" 
                placeholder="비밀번호 다시 입력"
                required
              >
            </div>
          </div>
        </div>
        
        <button type="submit" class="btn-submit" :disabled="isLoading || !isCodeVerified">
          <span v-if="!isLoading">비밀번호 변경하기</span>
          <span v-else class="spinner"></span>
        </button>

      </form>
      
      <div class="auth-footer">
        <router-link to="/login" class="link-home">로그인으로 돌아가기</router-link>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api';
import { useToast } from '@/utils/toast';

const router = useRouter();
const { showToast } = useToast();

// 상태 변수
const email = ref('');
const verificationCode = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

const isLoading = ref(false);
const isCodeSent = ref(false);
const isCodeVerified = ref(false);
const verificationMessage = ref('');

// 1. 인증코드 전송
const sendVerificationCode = async () => {
  if (!email.value) {
    showToast('이메일을 입력해주세요.', 'warning');
    return;
  }
  isLoading.value = true;
  try {
    // [수정됨] 비밀번호 찾기 전용 엔드포인트 호출 (/send-reset-code)
    await api.post('/api/v1/users/send-reset-code', { email: email.value });
    
    isCodeSent.value = true;
    isCodeVerified.value = false; // 재전송 시 인증 상태 초기화
    verificationMessage.value = '인증코드가 전송되었습니다. 이메일을 확인해주세요.';
    showToast('인증코드가 발송되었습니다.', 'success');
  } catch (error) {
    console.error(error);
    // 가입되지 않은 이메일일 경우 "User not found" 등의 에러가 옴
    const msg = error.response?.data?.message || '전송 실패. 이메일을 확인해주세요.';
    showToast(msg, 'error');
  } finally {
    isLoading.value = false;
  }
};

// 2. 인증코드 확인 (UI 상에서의 확인)
const verifyCode = async () => {
  if (!verificationCode.value) return;
  
  try {
    // verify-code 엔드포인트 호출 (코드 일치 여부 확인)
    await api.post('/api/v1/users/verify-code', {
      email: email.value,
      code: verificationCode.value
    });
    isCodeVerified.value = true;
    verificationMessage.value = '인증되었습니다. 비밀번호를 변경해주세요.';
  } catch (error) {
    isCodeVerified.value = false;
    verificationMessage.value = '인증코드가 일치하지 않습니다.';
  }
};

// 3. 비밀번호 변경 요청
const handleResetPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    showToast('비밀번호가 서로 일치하지 않습니다.', 'warning');
    return;
  }
  
  isLoading.value = true;
  try {
    // 최종 비밀번호 변경 요청 (/reset-password)
    await api.post('/api/v1/users/reset-password', {
      email: email.value,
      verificationCode: verificationCode.value, // 보안을 위해 코드도 같이 전송 (백엔드에서 최종 검증)
      newPassword: newPassword.value
    });
    
    showToast('비밀번호가 변경되었습니다. 로그인해주세요.', 'success');
    router.push('/login');
  } catch (error) {
    console.error(error);
    const msg = error.response?.data?.message || '변경 실패. 다시 시도해주세요.';
    showToast(msg, 'error');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
.auth-page {
  font-family: "Pretendard", sans-serif;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  width: 100%;
  background-color: #f5f7fb;
  padding: 20px;
}
.auth-card {
  background: white;
  width: 100%;
  max-width: 460px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.logo-area { margin-bottom: 32px; }
.logo-text { font-size: 24px; font-weight: 800; color: #1e293b; margin: 0 0 8px 0; }
.sub-text { font-size: 14px; color: #64748b; margin: 0; }

.auth-form { display: flex; flex-direction: column; gap: 20px; margin-bottom: 32px; }
.form-group { text-align: left; }
.form-label { display: block; font-size: 13px; font-weight: 600; color: #334155; margin-bottom: 8px; }

.input-group { display: flex; gap: 8px; }
.input-wrapper { position: relative; display: flex; align-items: center; flex-grow: 1; }
.input-icon { position: absolute; left: 14px; font-size: 16px; color: #94a3b8; }

.form-input {
  width: 100%;
  padding: 14px 14px 14px 44px;
  font-size: 15px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background-color: #f8fafc;
  outline: none;
  transition: all 0.2s;
}
.form-input:focus { background-color: #fff; border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
.form-input:disabled { background-color: #eef2f7; color: #94a3b8; }

.btn-check {
  padding: 14px 16px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  background-color: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  white-space: nowrap;
}
.btn-check:hover:not(:disabled) { background-color: #e2e8f0; }
.btn-check:disabled { background-color: #f8fafc; color: #94a3b8; cursor: not-allowed; }

.btn-success {
  background-color: #dcfce7 !important;
  color: #166534 !important;
  border-color: #86efac !important;
}

.verification-status { font-size: 13px; margin-top: 8px; font-weight: 500; }
.verification-success { color: #22c55e; }
.verification-error { color: #ef4444; }

.btn-submit {
  width: 100%;
  padding: 16px;
  font-size: 16px;
  font-weight: 700;
  color: white;
  background-color: #2563eb;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  margin-top: 12px;
}
.btn-submit:hover:not(:disabled) { background-color: #1d4ed8; }
.btn-submit:disabled { background-color: #94a3b8; cursor: not-allowed; }

.auth-footer { margin-top: 16px; }
.link-home { color: #94a3b8; font-size: 13px; text-decoration: none; }
.link-home:hover { color: #64748b; }

.spinner {
  width: 20px; height: 20px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>