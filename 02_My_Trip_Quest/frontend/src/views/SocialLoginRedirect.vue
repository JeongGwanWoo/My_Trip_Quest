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

onMounted(() => {
  const route = useRoute();
  const router = useRouter();
  const authStore = useAuthStore();

  const token = route.query.token;

  if (token) {
    console.log("Received token, logging in...");
    authStore.login(token);
    router.push('/'); // Redirect to home page
  } else {
    console.error("Social login error: No token received.");
    alert("로그인에 실패했습니다. 다시 시도해주세요.");
    router.push('/login'); // Redirect back to login page
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
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
