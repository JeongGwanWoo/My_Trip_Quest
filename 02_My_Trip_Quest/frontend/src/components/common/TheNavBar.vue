<template>
  <nav class="pixel-nav">
    <div 
      v-for="tab in tabs" 
      :key="tab.id" 
      class="nav-item" 
      :class="{ active: isActive(tab.path) }"
      @click="navigate(tab.path)"
    >
      <span class="nav-icon">{{ tab.icon }}</span>
      {{ tab.label }}
    </div>
  </nav>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const tabs = [
  { id: 'map', label: 'MAP', icon: '📍', path: '/quest-map' },
  { id: 'collection', label: 'COLLECTION', icon: '📦', path: '/collection' },
  { id: 'rankings', label: 'RANKINGS', icon: '🏆', path: '/rankings' },
  { id: 'shop', label: 'SHOP', icon: '🛍️', path: '/shop' },
  { id: 'fitting', label: 'FITTING ROOM', icon: '✨', path: '/fitting-room' },
  { id: 'profile', label: 'PROFILE', icon: '👤', path: '/profile' },
];

const isActive = (path) => {
  return route.path === path;
};

const navigate = (path) => {
  router.push(path);
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.pixel-nav {
  font-family: 'Press Start 2P', cursive;
  background-color: #1e293b;
  display: flex;
  justify-content: center;
  gap: 2px;
  padding: 0 20px;
  border-bottom: 4px solid #000;
  
  position: relative; 
  z-index: 10;
}

.nav-item {
  color: #94a3b8;
  padding: 15px 20px;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  text-transform: uppercase;
  transition: all 0.2s;
  border-top: 4px solid transparent;
}

.nav-item:hover {
  background-color: #334155;
  color: white;
}


.nav-item.active {
  background-color: #3b82f6;
  color: white;
  border: 2px solid #fff;
  border-bottom: none;
  margin-top: 4px;
  box-shadow: 0 -2px 0 rgba(0,0,0,0.2);
}


@media (max-width: 600px) {
  .nav-item span { display: none; }
  .nav-item { padding: 15px 10px; }
}
</style>