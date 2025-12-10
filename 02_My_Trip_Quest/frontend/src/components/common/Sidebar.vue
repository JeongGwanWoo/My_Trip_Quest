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

      <div class="search-wrapper">
        <span class="search-icon">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" style="width: 18px; height: 18px;">
            <path stroke-linecap="round" stroke-linejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
          </svg>
        </span>
        <input type="text" placeholder="검색" class="search-input" />
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
          <span class="text">여행</span>
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

        <router-link to="/fitting-room" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6A2.25 2.25 0 0 1 6 3.75h2.25A2.25 2.25 0 0 1 10.5 6v2.25a2.25 2.25 0 0 1-2.25 2.25H6a2.25 2.25 0 0 1-2.25-2.25V6ZM3.75 15.75A2.25 2.25 0 0 1 6 13.5h2.25a2.25 2.25 0 0 1 2.25 2.25V18a2.25 2.25 0 0 1-2.25 2.25H6A2.25 2.25 0 0 1 3.75 18v-2.25ZM13.5 6a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 20.25 6v2.25A2.25 2.25 0 0 1 18 10.5h-2.25a2.25 2.25 0 0 1-2.25-2.25V6ZM13.5 15.75a2.25 2.25 0 0 1 2.25-2.25H18A2.25 2.25 0 0 1 18 20.25h-2.25A2.25 2.25 0 0 1 13.5 18v-2.25Z" />
            </svg>
          </span>
          <span class="text">옷장</span>
        </router-link>

        <a href="#" @click.prevent="showComingSoonAlert" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M7.5 8.25h9m-9 3H12m-9.75 1.51c0 1.6 1.123 2.994 2.707 3.227 1.129.166 2.27.293 3.423.379.35.026.67.21.865.501L12 21l2.755-4.133a1.14 1.14 0 0 1 .865-.501 48.172 48.172 0 0 0 3.423-.379c1.584-.233 2.707-1.626 2.707-3.228V6.741c0-1.602-1.123-2.995-2.707-3.228A48.394 48.394 0 0 0 12 3c-2.392 0-4.744.175-7.043.513C3.373 3.746 2.25 5.14 2.25 6.741v6.018Z" />
            </svg>
          </span>
          <span class="text">커뮤니티</span>
        </a>

        <router-link to="/profile" class="nav-item">
          <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
            </svg>
          </span>
          <span class="text">프로필</span>
        </router-link>
      </nav>
    </div>

    <div class="sidebar-bottom" ref="moreMenuRef">
      <Transition name="fade">
        <div v-if="showMoreMenu" class="more-menu-popover">
          <div class="more-menu-grid">
            <div class="more-menu-col">
              <h4 class="menu-header">회사</h4>
              <a href="#">소개</a>
              <a href="#">채용</a>
              <a href="#">블로그</a>
            </div>
            <div class="more-menu-col">
              <h4 class="menu-header">더 보기</h4>
              <a href="#">공지사항</a>
              <a href="#">이벤트</a>
              <a href="#">고객센터</a>
            </div>
            <div class="more-menu-col">
              <h4 class="menu-header">법률</h4>
              <a href="#">이용약관</a>
              <a href="#">개인정보처리방침</a>
              <a href="#">쿠키 정책</a>
            </div>
          </div>
          <div class="more-menu-footer">
            <button class="footer-icon-btn" title="테마 설정">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width:20px;height:20px;">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v2.25m6.364.386-1.591 1.591M21 12h-2.25m-.386 6.364-1.591-1.591M12 18.75V21m-4.773-4.227-1.591 1.591M5.25 12H3m4.227-4.773L5.636 5.636M15.75 12a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0Z" />
              </svg>
            </button>
            <button class="footer-icon-btn" title="도움말">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width:20px;height:20px;">
                <path stroke-linecap="round" stroke-linejoin="round" d="M9.879 7.519c1.171-1.025 3.071-1.025 4.242 0 1.172 1.025 1.172 2.687 0 3.712-.203.179-.43.326-.67.442-.745.361-1.45.999-1.45 1.827v.75M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0Zm-9 5.25h.008v.008H12v-.008Z" />
              </svg>
            </button>
          </div>
        </div>
      </Transition>

      <a href="#" class="nav-item more-item" @click.prevent="toggleMoreMenu">
        <span class="icon">
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width: 20px; height: 20px;">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM12.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM18.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" />
            </svg>
        </span>
        <span class="text">더보기</span>
      </a>
    </div>

  </aside>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router'; // [추가]

const router = useRouter(); // [추가]

defineProps({
  isCollapsed: Boolean
})
defineEmits(['toggle-sidebar', 'close-mobile-menu'])

const showComingSoonAlert = () => {
  alert('준비중인 페이지입니다.');
}

// [추가] 로고 클릭 시 홈으로 이동
const goHome = () => {
  router.push('/');
}

// --- 더보기 메뉴 로직 ---
const showMoreMenu = ref(false);
const moreMenuRef = ref(null);

const toggleMoreMenu = () => {
  showMoreMenu.value = !showMoreMenu.value;
}

const handleClickOutside = (event) => {
  if (moreMenuRef.value && !moreMenuRef.value.contains(event.target)) {
    showMoreMenu.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
})
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

/* 검색창 스타일 */
.search-wrapper {
  position: relative;
  margin-bottom: 24px;
  width: 100%;
  height: 44px;
}

.search-input {
  width: 100%;
  height: 100%;
  padding: 0 12px 0 42px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  font-size: 15px;
  outline: none;
  transition: all 0.3s;
  font-family: inherit; 
}
.search-input::placeholder {
  font-family: inherit;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  transition: left 0.3s;
}

.sidebar.collapsed .search-input {
  padding: 0;
  opacity: 0;
  width: 0;
  border: none;
  background: transparent;
}
.sidebar.collapsed .search-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f9fafb;
  border-radius: 50%;
  width: 44px;
  margin: 0 auto 24px;
  cursor: pointer;
}
.sidebar.collapsed .search-icon {
  position: static;
  transform: none;
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

/* 하단 영역 스타일 */
.sidebar-bottom {
  margin-top: auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative; /* 팝업 위치 기준 */
}

/* --- 더보기 팝업 메뉴 스타일 --- */
.more-menu-popover {
  position: absolute;
  bottom: 100%; /* 더보기 버튼 바로 위 */
  left: 0;
  width: 280px; /* 사이드바보다 약간 넓게 팝업 */
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  padding: 20px;
  margin-bottom: 10px;
  z-index: 200;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 그리드 레이아웃 */
.more-menu-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
}

.more-menu-col {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.menu-header {
  font-size: 13px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.more-menu-col a {
  text-decoration: none;
  font-size: 13px;
  color: #64748b;
  transition: color 0.2s;
}
.more-menu-col a:hover {
  color: #2563eb;
}

/* 하단 아이콘 바 */
.more-menu-footer {
  border-top: 1px solid #f1f5f9;
  padding-top: 16px;
  display: flex;
  gap: 12px;
}

.footer-icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #64748b;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}
.footer-icon-btn:hover {
  background-color: #f1f5f9;
  color: #1e293b;
}

/* 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
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
  .sidebar.collapsed .search-input { opacity: 1; width: 100%; padding: 0 12px 0 42px; border: 1px solid #e5e7eb; }
  .sidebar.collapsed .search-wrapper { width: 100%; border-radius: 0; background: transparent; }
  .sidebar.collapsed .search-icon { position: absolute; transform: translateY(-50%); }
}
</style>