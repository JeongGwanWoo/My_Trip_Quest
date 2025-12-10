<template>
  <header class="app-header">
    
    <div class="header-left">
      <button class="hamburger-btn" @click="$emit('open-mobile-menu')">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" style="width: 24px; height: 24px;">
          <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
        </svg>
      </button>

      <div v-if="isCollapsed" class="header-logo desktop-only" @click="goHome">
        <span class="brand-blue">MYTRIP</span>QUEST
      </div>
      
      <div class="header-logo mobile-only" @click="goHome">
        <span class="brand-blue">MYTRIP</span>QUEST
      </div>
    </div>

    <div class="header-right">
      
      <button v-if="isLoggedIn" class="icon-btn" title="알림">
        <span class="notification-dot"></span>
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 24px; height: 24px;">
          <path stroke-linecap="round" stroke-linejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
        </svg>
      </button>

      <div class="auth-btn-area">
        <template v-if="isLoggedIn">
          <button class="text-btn logout" @click="handleLogout">
            로그아웃
          </button>
        </template>
        
        <template v-else>
          <button class="text-btn login" @click="handleLogin">
            로그인
          </button>
        </template>
      </div>

    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const authStore = useAuthStore();

defineProps({
  isCollapsed: Boolean
})

defineEmits(['open-mobile-menu'])

const isLoggedIn = computed(() => !!authStore.token);

// [추가] 로고 클릭 시 홈으로 이동
const goHome = () => {
  router.push('/');
}

const handleLogin = () => {
  router.push('/login');
};

const handleLogout = () => {
  if(confirm('로그아웃 하시겠습니까?')) {
    authStore.logout();
    router.push('/login');
  }
};
</script>

<style scoped>
/* 헤더 기본 레이아웃 */
.app-header {
  height: 64px;
  background-color: #fff;
  border-bottom: 1px solid #eef2ff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 50;
  transition: padding 0.3s;
}

/* 왼쪽 영역 (햄버거 버튼 + 로고) */
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 로고 스타일 */
.header-logo {
  font-family: "Pretendard", sans-serif;
  font-size: 20px;
  font-weight: 900;
  color: #1e293b;
  letter-spacing: -0.5px;
  white-space: nowrap;
  
  cursor: pointer; /* [추가] 클릭 가능 표시 */
}

.brand-blue {
  color: #2563eb;
}

/* 햄버거 버튼 (기본 숨김) */
.hamburger-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #334155;
}

.mobile-only { display: none; }

/* 오른쪽 영역 (아이콘 + 버튼) */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 알림 아이콘 버튼 */
.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #64748b;
  position: relative;
  padding: 4px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}
.icon-btn:hover {
  color: #3b82f6;
}

.notification-dot {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 8px;
  height: 8px;
  background-color: #ef4444;
  border-radius: 50%;
  border: 2px solid #fff;
}

/* 텍스트 버튼 공통 */
.text-btn {
  font-family: "Pretendard", sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  padding: 8px 16px;
  transition: all 0.2s;
  white-space: nowrap;
}

/* 로그인 버튼 */
.text-btn.login {
  background-color: #2563eb;
  color: white;
  border: 1px solid #2563eb;
}
.text-btn.login:hover {
  background-color: #1d4ed8;
}

/* 로그아웃 버튼 */
.text-btn.logout {
  background-color: white;
  color: #64748b;
  border: 1px solid #e2e8f0;
}
.text-btn.logout:hover {
  background-color: #f8fafc;
  color: #1e293b;
  border-color: #cbd5e1;
}

/* --- 모바일 반응형 (768px 이하) --- */
@media (max-width: 768px) {
  .app-header {
    padding: 0 16px;
  }

  .hamburger-btn {
    display: block;
  }

  .desktop-only {
    display: none !important;
  }
  .mobile-only {
    display: block;
    font-size: 18px;
  }
}
</style>