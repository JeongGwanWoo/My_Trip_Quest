<template>
  <header class="pixel-header">
    <div class="header-left">
      <div class="logo-icon">🗺️</div> 
      <router-link to="/main-menu" class="logo-text-group clickable">
        <h1 class="logo-title">MYTRIPQUEST</h1>
        <span class="logo-subtitle">TRAVEL & EARN COINS</span>
      </router-link>
    </div>

    <!-- Right side: contains both desktop nav and mobile hamburger button -->
    <div class="header-right">
      
      <!-- Hamburger Button (Mobile Only) -->
      <button class="hamburger-btn" @click="toggleMenu">
        <span class="bar"></span>
        <span class="bar"></span>
        <span class="bar"></span>
      </button>

      <!-- Desktop Navigation / Mobile Menu Content -->
      <nav class="desktop-nav" :class="{ 'is-open': isMenuOpen }">
        <button v-if="isMenuOpen" class="close-menu-btn" @click="closeMenu">X</button>
        <!-- Logged-in View -->
        <template v-if="authStore.isLoggedIn">
          <div class="pixel-box coin-box">
            <span class="icon">🪙</span>
            <span class="value">5000</span>
          </div>
          <div class="pixel-box level-box">LV.12</div>
          <div class="pixel-box user-box">
            <span class="icon">😊</span>
            <span class="username">TRAVELMASTER</span>
          </div>
          <button class="pixel-btn dark-btn">🌜 DARK</button>
          <button class="pixel-btn logout-btn" @click="handleLogout">↪ LOGOUT</button>
        </template>

        <!-- Logged-out View -->
        <template v-else>
          <router-link to="/login" class="pixel-btn login-btn" @click="closeMenu">LOGIN</router-link>
          <router-link to="/signup" class="pixel-btn signup-btn" @click="closeMenu">SIGNUP</router-link>
        </template>
      </nav>
      
      <!-- Overlay for mobile menu -->
      <div v-if="isMenuOpen" class="menu-overlay" @click="closeMenu"></div>

    </div>
  </header>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const route = useRoute();

const isMenuOpen = ref(false);

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const closeMenu = () => {
  isMenuOpen.value = false;
};

const handleLogout = () => {
  authStore.logout();
  closeMenu();
};

// Close menu on route change
watch(() => route.path, () => {
  closeMenu();
});
</script>

<style scoped>
/* General styles from before */
.pixel-header {
  font-family: 'Press Start 2P', cursive;
  background-color: #3b82f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  border-bottom: 4px solid #000;
  color: white;
  position: relative;
  z-index: 20; /* Ensure header is above nav */
}

.header-left { display: flex; align-items: center; gap: 15px; }
.logo-icon { font-size: 32px; filter: drop-shadow(4px 4px 0px rgba(0,0,0,0.2)); }
.logo-text-group { display: flex; flex-direction: column; gap: 6px; }
.logo-text-group.clickable { cursor: pointer; text-decoration: none; color: inherit; }
.logo-title {
  font-size: 20px;
  margin: 0;
  text-shadow: 3px 3px 0px #1e3a8a;
  letter-spacing: 2px;
}
.logo-subtitle { font-size: 8px; color: #fbbf24; text-transform: uppercase; letter-spacing: 1px; text-shadow: 1px 1px 0px #000; }

/* Desktop Navigation styles */
.desktop-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pixel-box { display: flex; align-items: center; justify-content: center; gap: 8px; padding: 8px 12px; font-size: 10px; border: 3px solid #000; box-shadow: 3px 3px 0px rgba(0,0,0,0.3); height: 40px; box-sizing: border-box; }
.coin-box { background-color: #fbbf24; color: #000; font-weight: bold; }
.level-box { background-color: #8b5cf6; color: #fff; }
.user-box { background-color: #1e293b; color: #fff; min-width: 140px; justify-content: flex-start; }
.pixel-btn { font-family: inherit; font-size: 10px; border: 3px solid #000; cursor: pointer; padding: 0 16px; height: 40px; display: flex; align-items: center; justify-content: center; box-shadow: 3px 3px 0px #000; font-weight: bold; transition: transform 0.1s; text-decoration: none; color: white; }
.pixel-btn:active { transform: translate(3px, 3px); box-shadow: none; }
.dark-btn { background-color: #fbbf24; color: #000; }
.logout-btn { background-color: #ef4444; color: #fff; }
.login-btn { background-color: #22c55e; }
.signup-btn { background-color: #1e293b; }

/* Hamburger Menu styles - Mobile only */
.hamburger-btn {
  display: none; /* Hidden by default */
  flex-direction: column;
  gap: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  z-index: 101; /* Above overlay and nav */
}
.hamburger-btn .bar {
  width: 25px;
  height: 3px;
  background-color: white;
  border: 1px solid #000;
}

.close-menu-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: #ef4444; /* Red color */
  color: white;
  border: 2px solid #000;
  padding: 5px 10px;
  font-family: 'Press Start 2P', cursive;
  font-size: 12px;
  cursor: pointer;
  box-shadow: 2px 2px 0 #000;
  z-index: 102; /* Above the menu */
}
.close-menu-btn:active {
  transform: translate(2px, 2px);
  box-shadow: none;
}

.menu-overlay {
  display: none; /* Hidden by default */
}


/* MEDIUM SCREENS (Tablet) - Start hiding some items */
@media (max-width: 900px) {
  .desktop-nav {
    display: none; /* Hide desktop nav */
  }
  .hamburger-btn {
    display: flex; /* Show hamburger button */
  }

  .desktop-nav.is-open {
    display: flex;
    flex-direction: column;
    position: fixed;
    top: 0;
    right: 0;
    width: 250px;
    height: 100%;
    background-color: #1e293b;
    padding: 90px 20px 20px; /* Adjusted padding to make space for the close button */
    gap: 15px;
    align-items: stretch;
    border-left: 4px solid #000;
    box-shadow: -5px 0 15px rgba(0,0,0,0.5);
    z-index: 100;
  }
  .desktop-nav.is-open .pixel-box,
  .desktop-nav.is-open .pixel-btn {
    width: 100%;
    justify-content: center;
    padding: 10px 15px; /* Reduced padding */
    font-size: 10px; /* Smaller font size */
    height: 38px; /* Slightly reduced height */
    box-sizing: border-box; /* Fix overflow issue */
  }

  .menu-overlay {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    z-index: 99;
  }
}

/* SMALL SCREENS (Mobile) - Further adjustments */
@media (max-width: 600px) {
  .pixel-header {
    padding: 10px 15px;
  }
  .logo-icon {
    font-size: 24px;
  }
  .logo-title {
    font-size: 14px;
  }
  .header-left {
    gap: 10px;
  }
}
</style>