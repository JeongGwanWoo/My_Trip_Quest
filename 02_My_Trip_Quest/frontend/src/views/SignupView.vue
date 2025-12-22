<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <span class="logo-icon"><i class="fa-solid fa-sparkles"></i></span>
        <h1 class="logo-text">회원가입</h1>
        <p class="sub-text">새로운 여행의 시작을 함께하세요</p>
      </div>

      <form @submit.prevent="handleSignup" class="auth-form">
        
        <div class="form-group">
          <label for="nickname" class="form-label">닉네임</label>
          <div class="input-group">
            <div class="input-wrapper">
              <span class="input-icon"><i class="fa-solid fa-user"></i></span>
              <input 
                type="text" 
                id="nickname" 
                v-model="nickname" 
                @input="onNicknameInput"
                class="form-input" 
                placeholder="사용할 닉네임을 입력하세요"
                required
              >
            </div>
            <button type="button" @click="checkNickname" :disabled="!nickname" class="btn-check">중복 확인</button>
          </div>
          <p v-if="nicknameMessage" :class="nicknameMessageClass" class="nickname-status">{{ nicknameMessage }}</p>
        </div>

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
                placeholder="example@email.com"
                :disabled="isCodeSent"
                required
              >
            </div>
            <button type="button" @click="sendVerificationCode" :disabled="!email || isCodeSent" class="btn-check">인증코드 전송</button>
          </div>
        </div>
        
        <div class="form-group" v-if="isCodeSent">
          <label for="verificationCode" class="form-label">인증코드</label>
          <div class="input-group"> <div class="input-wrapper">
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
        
        <div class="form-group">
          <label for="password" class="form-label">비밀번호</label>
          <div class="input-wrapper">
            <span class="input-icon"><i class="fa-solid fa-lock"></i></span>
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
            <span class="input-icon"><i class="fa-solid fa-shield-alt"></i></span>
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
        
        <button type="submit" class="btn-signup" :disabled="isLoading || nicknameCheckStatus !== 'available' || !isCodeVerified">
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api';
import { useToast } from '@/utils/toast';

const router = useRouter();
const { showToast } = useToast();

// Form fields
const nickname = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const verificationCode = ref('');

// State management
const isLoading = ref(false);
const nicknameCheckStatus = ref('idle'); 
const nicknameMessage = ref('');
const isCodeSent = ref(false);
const isCodeVerified = ref(false); // [추가] 인증 코드 검증 완료 여부
const verificationMessage = ref('');


const nicknameMessageClass = computed(() => {
  return {
    'available': nicknameCheckStatus.value === 'available',
    'taken': nicknameCheckStatus.value === 'taken',
  };
});

const onNicknameInput = () => {
  nicknameCheckStatus.value = 'idle';
  nicknameMessage.value = '';
};

const checkNickname = async () => {
  if (!nickname.value) {
    showToast('닉네임을 입력해주세요.', 'warning');
    return;
  }
  nicknameCheckStatus.value = 'checking';
  nicknameMessage.value = '닉네임 중복을 확인 중입니다...';

  try {
    const response = await api.get(`/api/v1/users/check-nickname`, {
      params: { nickname: nickname.value }
    });
    // 서버 응답 구조에 따라 수정 필요 (response.data.data.isAvailable 등)
    if (response.data.data.isAvailable) {
      nicknameCheckStatus.value = 'available';
      nicknameMessage.value = '사용 가능한 닉네임입니다.';
    } else {
      nicknameCheckStatus.value = 'taken';
      nicknameMessage.value = '이미 사용 중인 닉네임입니다.';
    } 
  } catch (error) {
    console.error('닉네임 중복 확인 오류:', error);
    nicknameCheckStatus.value = 'idle';
    nicknameMessage.value = '확인 중 오류가 발생했습니다.';
  }
};

const sendVerificationCode = async () => {
  if (!email.value) {
    showToast('이메일을 입력해주세요.', 'warning');
    return;
  }
  isLoading.value = true;
  verificationMessage.value = '인증코드를 전송 중입니다...';
  isCodeVerified.value = false; // 재전송 시 인증 상태 초기화

  try {
    await api.post('/api/v1/users/send-verification-code', { email: email.value });
    isCodeSent.value = true;
    verificationMessage.value = '인증코드가 전송되었습니다. 이메일을 확인해주세요.'; 
  } catch (error) {
    console.error('sendVerificationCode error:', error);
    const msg = error.response?.data?.message || '전송 실패. 다시 시도해주세요.';
    showToast(msg, 'error');
    verificationMessage.value = '';
  } finally {
    isLoading.value = false;
  }
};

// [추가] 인증코드 확인 함수
const verifyCode = async () => {
  if (!verificationCode.value) {
    showToast('인증코드를 입력해주세요.', 'warning');
    return;
  }

  try {
    // 주의: 백엔드에 이 엔드포인트(/verify-code)가 구현되어 있어야 합니다.
    // 만약 없다면 백엔드 컨트롤러에 해당 로직을 추가해야 합니다.
    const response = await api.post('/api/v1/users/verify-code', {
      email: email.value,
      code: verificationCode.value
    });

    // 서버 응답 성공 시
    isCodeVerified.value = true;
    verificationMessage.value = '이메일 인증이 완료되었습니다.';
    showToast('인증 성공!', 'success');

  } catch (error) {
    console.error('Verify code error:', error);
    isCodeVerified.value = false;
    verificationMessage.value = '인증코드가 올바르지 않습니다.';
    showToast('인증코드가 일치하지 않습니다.', 'error');
  }
};

const handleSignup = async () => {
  if (password.value !== confirmPassword.value) {
    showToast('비밀번호가 일치하지 않습니다.', 'error');
    return;
  }

  if (nicknameCheckStatus.value !== 'available') {
    showToast('닉네임 중복 확인을 해주세요.', 'warning');
    return;
  }
  
  // [변경] 코드가 전송되었고, '검증까지 완료(isCodeVerified)' 되었는지 확인
  if (!isCodeVerified.value) {
    showToast('이메일 인증 확인 버튼을 눌러 인증을 완료해주세요.', 'warning');
    return;
  }

  isLoading.value = true;

  try {
    await api.post('/api/v1/users/register', {
      nickname: nickname.value,
      email: email.value,
      password: password.value,
      // 인증코드는 이미 검증했으므로 백엔드 로직에 따라 생략하거나 보낼 수 있음
      verificationCode: verificationCode.value, 
    });
    
    showToast('회원가입이 완료되었습니다.', 'success');
    router.push('/login');

  } catch (error) {
    console.error('회원가입 오류:', error);
    const msg = error.response?.data?.message || '오류가 발생했습니다.';
    showToast(`회원가입 실패: ${msg}`, 'error');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 기존 스타일 유지하면서 추가된 부분만 명시 */
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

.auth-card {
  background: white;
  width: 100%;
  max-width: 460px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}

/* ... (기존 로고, 폼 스타일 생략 - 동일함) ... */

.logo-area { margin-bottom: 32px; }
.logo-icon { font-size: 40px; display: block; margin-bottom: 8px; color: #3b82f6; }
.logo-text { font-size: 24px; font-weight: 800; color: #1e293b; margin: 0 0 8px 0; letter-spacing: -0.5px; }
.sub-text { font-size: 14px; color: #64748b; margin: 0; }
.auth-form { display: flex; flex-direction: column; gap: 20px; margin-bottom: 32px; }
.form-group { text-align: left; }
.form-label { display: block; font-size: 13px; font-weight: 600; color: #334155; margin-bottom: 8px; }
.input-group { display: flex; gap: 8px; }
.input-wrapper { position: relative; display: flex; align-items: center; flex-grow: 1; }
.input-icon { position: absolute; left: 14px; font-size: 16px; color: #94a3b8; pointer-events: none; z-index: 2; }

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
.form-input:disabled { background-color: #eef2f7; color: #94a3b8; }
.form-input:focus { background-color: #fff; border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
.form-input::placeholder { color: #cbd5e1; }

/* 버튼 스타일 */
.btn-check {
  padding: 14px 16px;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  background-color: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
  white-space: nowrap; /* 텍스트 줄바꿈 방지 */
}
.btn-check:hover:not(:disabled) { background-color: #e2e8f0; }
.btn-check:disabled { background-color: #f8fafc; color: #94a3b8; cursor: not-allowed; }

/* [추가] 인증 성공 시 버튼 스타일 */
.btn-success {
  background-color: #dcfce7 !important;
  color: #166534 !important;
  border-color: #86efac !important;
}

.nickname-status, .verification-status { font-size: 13px; margin-top: 8px; font-weight: 500; color: #64748b; }
.nickname-status.available { color: #22c55e; }
.nickname-status.taken { color: #ef4444; }

/* [추가] 인증 상태 메시지 색상 */
.verification-success { color: #22c55e; }
.verification-error { color: #ef4444; }

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
  transition: background 0.2s, transform 0.1s;
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 52px;
}
.btn-signup:hover:not(:disabled) { background-color: #334155; transform: translateY(-1px); }
.btn-signup:active:not(:disabled) { transform: translateY(0); }
.btn-signup:disabled { background-color: #94a3b8; cursor: not-allowed; }

.auth-footer { display: flex; flex-direction: column; gap: 12px; font-size: 14px; }
.login-text { color: #64748b; margin: 0; }
.link-highlight { color: #2563eb; font-weight: 700; text-decoration: none; margin-left: 4px; }
.link-highlight:hover { text-decoration: underline; }
.link-home { color: #94a3b8; font-size: 13px; text-decoration: none; margin-top: 8px; }
.link-home:hover { color: #64748b; }

.spinner {
  width: 20px; height: 20px;
  border: 2px solid #ffffff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 480px) {
  .auth-card { padding: 32px 24px; box-shadow: none; background: transparent; }
  .auth-page { background-color: #fff; }
}
</style>