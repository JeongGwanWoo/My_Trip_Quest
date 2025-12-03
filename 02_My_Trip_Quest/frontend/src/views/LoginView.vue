<template>
  <div class="pixel-page">
    <h1 class="pixel-text">Login</h1>
    <p class="pixel-text">This is the Login page.</p>
    <router-link to="/main-menu" class="pixel-btn">Go to Main Menu</router-link>
  </div>
</template>

<script setup>
</script>

<style scoped>
.pixel-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f0f0f0;
}

.pixel-text {
  font-family: 'Press Start 2P', cursive;
  color: #333;
  margin-bottom: 20px;
}

.pixel-btn {
  background: white;
  border: 3px solid #2e1a47;
  padding: 8px 16px;
  font-family: 'Press Start 2P', cursive;
  font-size: 10px;
  cursor: pointer;
  box-shadow: 3px 3px 0px #2e1a47;
  transition: all 0.1s;
  text-decoration: none;
  color: #2e1a47;
}

.pixel-btn:active {
  transform: translate(3px, 3px);
  box-shadow: none;
}
</style>