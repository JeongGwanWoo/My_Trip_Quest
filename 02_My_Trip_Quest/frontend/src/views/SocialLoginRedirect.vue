<template>
  <div class="loading-container">
    <div class="spinner"></div>
    <p>로그인 중입니다...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useToast } from '@/utils/toast'; // 1. 토스트 훅 임포트

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const { showToast } = useToast(); // 2. 토스트 함수 가져오기

onMounted(() => {
  const token = route.query.token;
  const registrationToken = route.query.registrationToken;

  if (token) {
    console.log("Received token, logging in...");
    authStore.login(token);
    
    // 3. 로그인 성공 토스트 띄우기
    showToast('성공적으로 로그인되었습니다!', 'success');
    
    router.push('/'); // 메인 페이지로 이동
  } else if (registrationToken) {
    console.log("Received registration token, proceeding to social sign up...");
    authStore.setRegistrationToken(registrationToken);
    router.push('/social-signup'); // 소셜 회원가입 페이지로 이동
  } else {
    console.error("Social login error: No token received.");
    
    // 4. 실패 시 alert 대신 토스트 사용
    showToast('로그인에 실패했습니다. 다시 시도해주세요.', 'error');
    
    router.push('/login'); // 로그인 페이지로 리다이렉트
  }
});
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family: "Pretendard", sans-serif;
  background-color: #f5f7fb; /* 배경색을 앱 톤과 맞춤 */
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #3b82f6;
  animation: spin 1s ease infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>