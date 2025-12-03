<template>
  <header class="pixel-header">
    <div class="header-left">
      <div class="logo-icon">🗺️</div> 
      <router-link to="/main-menu" class="logo-text-group clickable">
        <h1 class="logo-title">MYTRIPQUEST</h1>
        <span class="logo-subtitle">TRAVEL & EARN COINS</span>
      </router-link>
    </div>

    <div class="header-right">
      <div class="pixel-box coin-box">
        <span class="icon">🪙</span>
        <span class="value">5000</span>
      </div>

      <div class="pixel-box level-box">
        LV.12
      </div>

      <div class="pixel-box user-box">
        <span class="icon">😊</span>
        <span class="username">TRAVELMASTER</span>
      </div>

      <button class="pixel-btn dark-btn">
        🌜 DARK
      </button>

      <button class="pixel-btn logout-btn">
        ↪ LOGOUT
      </button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.pixel-header {
  font-family: 'Press Start 2P', cursive;
  background-color: #3b82f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  border-bottom: 4px solid #000;
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo-icon {
  font-size: 32px;
  filter: drop-shadow(4px 4px 0px rgba(0,0,0,0.2));
}

.logo-text-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.logo-text-group.clickable {
  cursor: pointer;
  text-decoration: none;
  color: inherit;
}

.logo-title {
  font-size: 20px;
  margin: 0;
  text-shadow: 3px 3px 0px #1e3a8a;
  letter-spacing: 2px;
}

.logo-subtitle {
  font-size: 8px;
  color: #fbbf24;
  text-transform: uppercase;
  letter-spacing: 1px;
  text-shadow: 1px 1px 0px #000;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pixel-box {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 10px;
  border: 3px solid #000;
  box-shadow: 3px 3px 0px rgba(0,0,0,0.3);
  height: 40px;
  box-sizing: border-box;
}

.coin-box {
  background-color: #fbbf24;
  color: #000;
  font-weight: bold;
}

.level-box {
  background-color: #8b5cf6;
  color: #fff;
}

.user-box {
  background-color: #1e293b;
  color: #fff;
  min-width: 140px;
  justify-content: flex-start;
}

.pixel-btn {
  font-family: inherit;
  font-size: 10px;
  border: 3px solid #000;
  cursor: pointer;
  padding: 0 16px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 3px 3px 0px #000;
  font-weight: bold;
  transition: transform 0.1s;
}

.pixel-btn:active {
  transform: translate(3px, 3px);
  box-shadow: none;
}

.dark-btn {
  background-color: #fbbf24;
  color: #000;
}

.logout-btn {
  background-color: #ef4444;
  color: #fff;
}

@media (max-width: 900px) {
  .logo-subtitle { display: none; }
  .user-box { display: none; }
}
</style>