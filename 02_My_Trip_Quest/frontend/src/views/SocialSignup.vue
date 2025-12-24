<template>
  <div class="auth-page">
    <div class="auth-card">
      
      <div class="logo-area">
        <span class="logo-icon"><i class="fa-solid fa-wand-magic-sparkles"></i></span>
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
          <p class="help-text">닉네임은 한글 1~6자, 영문/숫자 1~12자 이내로 입력해주세요.</p>
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
import { ref, onMounted, watch } from 'vue'; // ⭐ onMounted 추가
import { useRouter, useRoute } from 'vue-router'; // ⭐ useRoute 추가
import { useAuthStore } from '@/stores/auth';
import { useToast } from '@/utils/toast';
import { socialSignup } from '@/api/user';

const router = useRouter();
const route = useRoute(); // ⭐ 현재 URL 정보를 가져오기 위해 호출
const authStore = useAuthStore();
const { showToast } = useToast();

const nickname = ref('');
const isLoading = ref(false);
const nicknameError = ref('');

watch(nickname, (newNickname) => {
  const containsKorean = /[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(newNickname);
  const maxLength = containsKorean ? 6 : 12;

  if (newNickname.length > maxLength) {
    nickname.value = newNickname.slice(0, maxLength);
    nicknameError.value = `닉네임은 ${containsKorean ? '한글 6자,' : ''} 영문 12자 이내로 입력해주세요.`;
  } else {
    nicknameError.value = '';
  }
});

/**
 * [해결 포인트 1] 페이지가 마운트되자마자 URL에서 토큰을 추출합니다.
 * 이 과정이 없으면 버튼을 눌렀을 때 스토어에 토큰이 없어 가입 실패 알림이 뜹니다.
 */
onMounted(() => {
  // URL 쿼리 파라미터에서 token 추출 (예: /social-signup?token=xxx)
  const tokenFromUrl = route.query.token;
  
  if (tokenFromUrl) {
    // Pinia 스토어에 즉시 저장
    authStore.setRegistrationToken(tokenFromUrl);
    console.log('URL에서 인증 토큰을 성공적으로 로드했습니다.');

    // (옵션) 주소창을 깔끔하게 유지하고 싶다면 쿼리 파라미터를 제거합니다.
    router.replace({ query: {} });
  } else {
    // 만약 스토어에도 없고 URL에도 없다면 잘못된 접근으로 처리
    if (!authStore.registrationToken) {
      showToast('인증 정보가 없습니다. 다시 로그인해주세요.', 'error');
      router.push('/login');
    }
  }
});

// 입력 시 에러 메시지 초기화
const clearError = () => {
  nicknameError.value = '';
};

/**
 * 회원가입 완료 요청
 */
const completeSignup = async () => {
  if (!nickname.value.trim()) {
    showToast('닉네임을 입력해주세요.', 'warning');
    return;
  }

  isLoading.value = true;
  
  try {
    const registrationToken = authStore.registrationToken;
    
    // 이 단계에서 registrationToken이 없으면 catch 블록으로 넘어감
    if (!registrationToken) {
      throw new Error('registration_token_missing');
    }

    const response = await socialSignup({
      registrationToken,
      nickname: nickname.value,
    });

    // 가입 완료 후 발급된 JWT 토큰으로 즉시 로그인 처리
    const newJwtToken = response.data?.token || response.token;
    
    // authStore.login 액션 내에서 fetchUserProfile()이 실행되도록 설정되어 있어야 함
    await authStore.login(newJwtToken);
    authStore.clearRegistrationToken();

    showToast('환영합니다! 회원가입이 완료되었습니다.', 'success');
    router.push('/');
    
  } catch (error) {
    console.error('Social signup failed:', error);
    
    if (error.message === 'registration_token_missing') {
      showToast('인증 세션이 만료되었습니다. 다시 시도해주세요.', 'error');
      router.push('/login');
    } else if (error.response && (error.response.status === 409 || error.response.status === 400)) {
      nicknameError.value = error.response.data.message || '이미 사용 중인 닉네임입니다.';
    } else {
      const errorMessage = error.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해주세요.';
      showToast(errorMessage, 'error');
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
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
  max-width: 440px;
  padding: 48px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  text-align: center;
}
.logo-area { margin-bottom: 32px; }
.logo-icon { font-size: 36px; color: #3b82f6; display: block; margin-bottom: 12px; }
.logo-text { font-size: 24px; font-weight: 800; color: #1e293b; margin: 0 0 8px 0; letter-spacing: -0.5px; }
.sub-text { font-size: 14px; color: #64748b; margin: 0; }
.auth-form { display: flex; flex-direction: column; gap: 20px; margin-bottom: 32px; }
.form-group { text-align: left; }
.form-label { display: block; font-size: 13px; font-weight: 600; color: #334155; margin-bottom: 8px; }
.input-wrapper { position: relative; display: flex; align-items: center; }
.input-icon { position: absolute; left: 14px; font-size: 16px; color: #94a3b8; }
.form-input { width: 100%; padding: 14px 14px 14px 44px; font-size: 15px; border: 1px solid #e2e8f0; border-radius: 12px; background-color: #f8fafc; color: #1e293b; outline: none; transition: all 0.2s; }
.form-input:focus { background-color: #fff; border-color: #3b82f6; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
.help-text {
  font-size: 12px;
  color: #64748b;
  margin-top: 6px;
  text-align: left;
  padding-left: 4px;
}

.form-input.input-error { border-color: #ef4444; background-color: #fffafb; }
.form-input.input-error:focus { box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1); }
.error-message { color: #ef4444; font-size: 13px; margin-top: 8px; font-weight: 500; }
.btn-signup { width: 100%; padding: 16px; font-size: 16px; font-weight: 700; color: white; background-color: #1e293b; border: none; border-radius: 12px; cursor: pointer; transition: all 0.2s; display: flex; justify-content: center; align-items: center; height: 54px; }
.btn-signup:hover:not(:disabled) { background-color: #334155; transform: translateY(-1px); }
.btn-signup:disabled { background-color: #94a3b8; cursor: not-allowed; }
.auth-footer { font-size: 14px; }
.info-text { color: #64748b; }
.link-highlight { color: #2563eb; font-weight: 700; text-decoration: none; margin-left: 4px; }
.link-highlight:hover { text-decoration: underline; }
.fade-slide-enter-active, .fade-slide-leave-active { transition: all 0.3s ease; }
.fade-slide-enter-from, .fade-slide-leave-to { opacity: 0; transform: translateY(-5px); }
.spinner { width: 20px; height: 20px; border: 2px solid #ffffff; border-top-color: transparent; border-radius: 50%; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>