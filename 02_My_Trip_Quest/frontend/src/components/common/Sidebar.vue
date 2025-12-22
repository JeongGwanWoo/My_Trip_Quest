<template>
  <aside class="sidebar" :class="{ 'collapsed': isCollapsed }">
    <div class="sidebar-top">
      
      <div class="header-row">
        <div class="logo-area" v-show="!isCollapsed" @click="goHome">
          <h1 class="logo-text">
            <span class="brand-blue">MYTRIP</span>QUEST
          </h1>
        </div>

        <button class="mobile-close-btn" @click="$emit('close-mobile-menu')" title="닫기">
          ✕
        </button>

        <button class="btn-toggle" @click="$emit('toggle-sidebar')" title="사이드바 토글">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="4" y="4" width="16" height="16" rx="3" stroke="#555" stroke-width="2"/>
            <line x1="9" y1="4" x2="9" y2="20" stroke="#555" stroke-width="2"/>
          </svg>
        </button>
      </div>

      <nav class="nav-menu">
        <router-link to="/" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="m2.25 12 8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25" />
            </svg>
          </span>
          <span class="text">메인</span>
        </router-link>

        <router-link to="/quest-map" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M9 6.75V15m6-6v8.25m.503 3.498 4.875-2.437c.381-.19.622-.58.622-1.006V4.82c0-.836-.88-1.38-1.628-1.006l-3.869 1.934c-.317.159-.69.159-1.006 0L9.503 3.252a1.125 1.125 0 0 0-1.006 0L3.622 5.689C3.24 5.88 3 6.27 3 6.695V19.18c0 .836.88 1.38 1.628 1.006l3.869-1.934c.317-.159.69-.159 1.006 0l4.994 2.497c.317.158.69.158 1.006 0Z" />
            </svg>
          </span>
          <span class="text">퀘스트</span>
        </router-link>

        <router-link to="/rankings" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 18.75h-9m9 0a3 3 0 0 1 3 3h-15a3 3 0 0 1 3-3m9 0v-3.375c0-.621-.504-1.125-1.125-1.125h-.871M7.5 18.75v-3.375c0-.621.504-1.125 1.125-1.125h.872m5.007 0V9.499a2.25 2.25 0 0 0-2.25-2.25H11.25a2.25 2.25 0 0 0-2.25 2.25v2.625m-6-4.688 1.803-3.376a1.5 1.5 0 0 1 1.492-1.298h9.696a1.5 1.5 0 0 1 1.492 1.298l1.803 3.376" />
            </svg>
          </span>
          <span class="text">랭킹</span>
        </router-link>

        <router-link to="/shop" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007ZM8.625 10.5a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm7.5 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z" />
            </svg>
          </span>
          <span class="text">상점</span>
        </router-link>

        <router-link to="/fitting-room" class="nav-item" v-if="isLoggedIn">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 0 1 6 3.75h2.25A2.25 2.25 0 0 1 10.5 6v2.25a2.25 2.25 0 0 1-2.25 2.25H6a2.25 2.25 0 0 1-2.25-2.25V6ZM3.75 15.75A2.25 2.25 0 0 1 6 13.5h2.25a2.25 2.25 0 0 1 2.25 2.25V18a2.25 2.25 0 0 1-2.25 2.25H6A2.25 2.25 0 0 1 3.75 18v-2.25ZM13.5 6a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 20.25 6v2.25A2.25 2.25 0 0 1 18 10.5h-2.25a2.25 2.25 0 0 1-2.25-2.25V6ZM13.5 15.75a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 18 20.25h-2.25A2.25 2.25 0 0 1 13.5 18v-2.25Z" />
            </svg>
          </span>
          <span class="text">옷장</span>
        </router-link>

        <router-link to="/profile" class="nav-item" v-if="isLoggedIn">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
            </svg>
          </span>
          <span class="text">프로필</span>
        </router-link>
      </nav>
    </div>

  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';

const router = useRouter();
const authStore = useAuthStore();
const { isLoggedIn } = storeToRefs(authStore);

defineProps({
  isCollapsed: Boolean
})
defineEmits(['toggle-sidebar', 'close-mobile-menu'])

// [추가] 로고 클릭 시 홈으로 이동
const goHome = () => {
  router.push('/');
}
</script>

<style scoped>
/* --- 사이드바 스타일 --- */
.sidebar {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, "Helvetica Neue", "Segoe UI", "Apple SD Gothic Neo", "Noto Sans KR", "Malgun Gothic", "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol", sans-serif;
  width: 250px;
  padding: 24px 20px;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-shrink: 0;
  background: #fff;
  transition: width 0.3s ease, padding 0.3s ease;
  overflow: visible; 
  z-index: 100;
}

.sidebar.collapsed {
  width: 80px;
  padding: 24px 10px;
}

/* 로고 영역 스타일 */
.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;
  height: 40px;
  transition: all 0.3s ease;
}

.sidebar.collapsed .header-row {
  justify-content: center;
  margin-bottom: 24px;
}

.logo-area {
  display: flex;
  align-items: center;
  cursor: pointer; /* [수정] 클릭 커서 추가 */
}

.logo-text {
  font-family: "Pretendard", sans-serif;
  font-size: 26px;
  font-weight: 900;
  color: #1e293b;
  margin: 0;
  letter-spacing: -1px;
  line-height: 1;
}

.brand-blue {
  color: #2563eb;
}

/* 버튼 스타일 */
.btn-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  color: #555;
}
.btn-toggle:hover {
  background-color: #f3f4f6;
}

/* 모바일 닫기 버튼 */
.mobile-close-btn {
  display: none;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #64748b;
  padding: 4px;
}

/* 네비게이션 메뉴 스타일 */
.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  text-decoration: none;
  color: #4b5563;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  transition: all 0.2s;
  height: 48px;
  overflow: hidden;
  white-space: nowrap;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 0;
  gap: 0;
}
.sidebar.collapsed .nav-item .text {
  display: none;
}
.nav-item .icon {
  font-size: 20px;
  min-width: 24px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}
.nav-item:hover {
  background-color: #f3f4f6;
  color: #111;
}
.nav-item.active {
  color: #2563eb;
  font-weight: 700;
  background-color: #eff6ff;
}

/* ------------------------------------------- */

/* ★ 모바일 반응형 스타일 (768px 이하) ★ */

/* ------------------------------------------- */

@media (max-width: 768px) {

  .sidebar {

    position: fixed;

    top: 0; left: 0; bottom: 0;

    width: 280px !important;

    transform: translateX(-100%);

    transition: transform 0.3s ease;

    box-shadow: 4px 0 20px rgba(0,0,0,0.1);

    z-index: 1000;

  }



  .sidebar.mobile-open {

    transform: translateX(0);

  }



  .btn-toggle {

    display: none;

  }



  .mobile-close-btn {

    display: block;

    position: absolute;

    top: 24px;

    right: 20px;

  }



  .sidebar.collapsed { width: 280px; padding: 24px 20px; }

  .sidebar.collapsed .logo-area { display: flex; }

  .sidebar.collapsed .nav-item .text { display: block; }

}

</style>
