<template>
  <div class="page-container">
    
    <div 
      v-if="isMobileMenuOpen" 
      class="mobile-overlay"
      @click="closeMobileMenu"
    ></div>

    <Sidebar 
      v-if="showLayout" 
      :is-collapsed="isCollapsed" 
      :class="{ 'mobile-open': isMobileMenuOpen }"
      @toggle-sidebar="toggleSidebar"
      @close-mobile-menu="closeMobileMenu" 
      :key="authStore.isLoggedIn"
    />
    
    <div v-if="showLayout" class="content-wrapper">
      <Header 
        :is-collapsed="isCollapsed" 
        @open-mobile-menu="openMobileMenu" 
        :key="authStore.isLoggedIn"
      />
      
      <div class="page-content" :class="{ 'no-scroll': isMapPage }">
        <RouterView />
      </div>
      <Footer :is-map-page="isMapPage" />
    </div>

    <RouterView v-else />

    <ToastContainer :toasts="toasts" @remove-toast="removeToast" />

  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, provide } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import Sidebar from '@/components/common/Sidebar.vue'
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
import ToastContainer from '@/components/ui/ToastContainer.vue'
import { useAuthStore } from '@/stores/auth'
import { getProfile } from '@/api/user'

// --- Toast Logic ---
const toasts = ref([]);
let toastId = 0;

const showToast = (message, type = 'info', duration = 3000) => {
  const id = toastId++;
  toasts.value.push({ id, message, type, duration });
};

const removeToast = (idToRemove) => {
  toasts.value = toasts.value.filter(toast => toast.id !== idToRemove);
};

provide('showToast', showToast);
// --------------------

const route = useRoute()
const isCollapsed = ref(false)
const isMobileMenuOpen = ref(false)

// --- Auth Check on Startup ---
const authStore = useAuthStore()
const checkAuthStatus = async () => {
  if (authStore.isLoggedIn) {
    try {
      // Try to fetch user profile to validate the token
      await getProfile();
      console.log("세션 유효성 검사 성공: 사용자가 로그인되어 있습니다.");
    } catch (error) {
      console.log("세션 유효성 검사 실패: 토큰이 만료되었거나 서버에 연결할 수 없습니다. 강제 로그아웃합니다.");
      authStore.logout();
    }
  }
}

onMounted(() => {
  checkAuthStatus();
});
// -----------------------------

// [추가] 퀘스트 맵 페이지인지 확인하는 변수
const isMapPage = computed(() => route.path === '/quest-map')

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const openMobileMenu = () => {
  isMobileMenuOpen.value = true
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

watch(route, () => {
  closeMobileMenu()
})

const showLayout = computed(() => {
  const hidePaths = ['/login', '/signup']
  return !hidePaths.includes(route.path)
})
</script>

<style>
@import url("https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css");

*, *::before, *::after {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  color: #333;
  background-color: #f5f7fb;
}

.page-container {
  font-family: "Pretendard", sans-serif;
  display: flex;
  min-height: 100vh;
  width: 100%;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* 실제 페이지 콘텐츠 영역 */
.page-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  
  /* [수정] 기본값: 스크롤 가능 (홈, 랭킹, 상점 등) */
  overflow-y: auto; 
  overflow-x: hidden;
}

/* [추가] 지도 페이지일 때만 적용되는 클래스: 스크롤 막음 */
.page-content.no-scroll {
  overflow: hidden;
}

.mobile-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: none;
}

@media (max-width: 768px) {
  .mobile-overlay {
    display: block;
  }
}
</style>