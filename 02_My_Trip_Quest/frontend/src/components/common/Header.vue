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
      
      <div class="notification-wrapper">
        <button v-if="isLoggedIn" class="icon-btn" title="알림" @click="toggleNotifications">
          <span v-if="hasNewNotifications" class="notification-dot"></span>
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 24px; height: 24px;">
            <path stroke-linecap="round" stroke-linejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
          </svg>
        </button>
        
        <div v-if="showNotifications" class="notification-overlay" @click="toggleNotifications"></div>
        <div v-if="showNotifications" class="notification-panel">
          <div class="panel-header">
            <h3>알림</h3>
            <button class="close-btn-mobile" @click="toggleNotifications">&times;</button>
          </div>
          <div class="panel-body">
            <div v-if="isLoading" class="loading-spinner">
              <p>로딩 중...</p>
            </div>
            <ul v-else-if="activityLogs.length > 0" class="notification-list">
              <li v-for="log in activityLogs" :key="log.logId" class="notification-item">
                <p class="log-message">{{ log.logMessage }}</p>
                <time class="log-time">{{ formatTime(log.createdAt) }}</time>
              </li>
            </ul>
            <div v-else class="empty-state">
              <p>새로운 알림이 없습니다.</p>
            </div>
          </div>
        </div>
      </div>

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
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';
import { getMyActivityLogs } from '@/api/activityLog';

const router = useRouter();
const authStore = useAuthStore();
const { isLoggedIn } = storeToRefs(authStore);

// 알림 패널 상태
const showNotifications = ref(false);
const activityLogs = ref([]);
const isLoading = ref(false);
const hasNewNotifications = ref(false); // 실제 새로운 알림 여부 로직 필요

defineProps({
  isCollapsed: Boolean
})

defineEmits(['open-mobile-menu'])

const goHome = () => {
  router.push('/');
}

const handleLogin = () => {
  router.push('/login');
};

const handleLogout = () => {
  if(confirm('로그아웃 하시겠습니까?')) {
    authStore.logout();
  }
};

const fetchActivityLogs = async () => {
  isLoading.value = true;
  try {
    const response = await getMyActivityLogs();
    console.log('Activity logs response:', response.data); // 응답 데이터 로깅
    activityLogs.value = response.data;
    // 여기에서 마지막 확인 시간과 비교하여 hasNewNotifications 설정 가능
  } catch (error) {
    console.error('알림을 불러오는 데 실패했습니다:', error);
    activityLogs.value = []; // 에러 발생 시 비워줌
  } finally {
    isLoading.value = false;
  }
};

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value;
};

// 알림 패널이 열릴 때 데이터를 가져옵니다.
watch(showNotifications, (newValue) => {
  if (newValue) {
    fetchActivityLogs();
  }
});

// 시간 포맷팅 함수
const formatTime = (isoString) => {
  const date = new Date(isoString);
  const now = new Date();
  const diffSeconds = Math.round((now - date) / 1000);
  const diffMinutes = Math.round(diffSeconds / 60);
  const diffHours = Math.round(diffMinutes / 60);
  const diffDays = Math.round(diffHours / 24);

  if (diffSeconds < 60) return '방금 전';
  if (diffMinutes < 60) return `${diffMinutes}분 전`;
  if (diffHours < 24) return `${diffHours}시간 전`;
  if (diffDays <= 7) return `${diffDays}일 전`;
  return date.toLocaleDateString('ko-KR');
};

</script>

<style scoped>
/* 기존 스타일 유지 */
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

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-logo {
  font-family: "Pretendard", sans-serif;
  font-size: 20px;
  font-weight: 900;
  color: #1e293b;
  letter-spacing: -0.5px;
  white-space: nowrap;
  cursor: pointer;
}

.brand-blue {
  color: #2563eb;
}

.hamburger-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  color: #334155;
}

.mobile-only { display: none; }

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

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

.text-btn.login {
  background-color: #2563eb;
  color: white;
  border: 1px solid #2563eb;
}
.text-btn.login:hover {
  background-color: #1d4ed8;
}

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

/* --- 알림 패널 스타일 --- */
.notification-wrapper {
  position: relative;
}

.notification-panel {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 12px;
  width: 360px;
  max-height: 480px;
  background-color: white;
  border-radius: 0 0 12px 12px; /* 상단 모서리는 평평하게, 하단 모서리만 둥글게 */
  box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  /* overflow: hidden; -> 패널 바디의 스크롤을 위해 제거 */
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.close-btn-mobile {
  display: none; /* 모바일에서만 보이도록 기본 숨김 */
  background: none;
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: #94a3b8;
  cursor: pointer;
}

.notification-overlay {
  display: none; /* 모바일에서만 활성화 */
}

.panel-body {
  overflow-y: auto;
  flex: 1;
}

.loading-spinner, .empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  color: #64748b;
  font-size: 14px;
}

.notification-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.notification-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background-color: #f8fafc;
}

.log-message {
  font-size: 14px;
  color: #334155;
  margin: 0 0 4px 0;
  line-height: 1.5;
}

.log-time {
  font-size: 12px;
  color: #94a3b8;
}


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

  .notification-overlay {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 90;
  }

  .notification-panel {
    position: fixed;
    top: 0;
    right: 0;
    width: 100%;
    height: 100%;
    margin-top: 0;
    border-radius: 0 0 12px 12px; /* 모바일에서 하단 모서리만 둥글게 */
    box-shadow: none;
    border: none;
    z-index: 100;
  }

  .close-btn-mobile {
    display: block;
  }
}
</style>