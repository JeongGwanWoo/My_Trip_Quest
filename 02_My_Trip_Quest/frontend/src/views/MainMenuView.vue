<template>
  <div class="pixel-map-container">
    <header class="map-header">
      <div class="brand">
        <span class="icon">🏠</span> MAIN MENU
      </div>
      <div class="controls">
        <router-link to="/login" class="pixel-btn">Login</router-link>
        <router-link to="/signup" class="pixel-btn">Signup</router-link>
      </div>
    </header>

    <main class="map-content">
      
      <div class="menu-grid">
        <div 
          v-for="item in menuItems" 
          :key="item.id" 
          class="pixel-card" 
          @click="handleItemClick(item.id)"
        >
          <div class="img-box">
            <span class="emoji">{{ item.icon }}</span>
          </div>
          
          <span class="label">{{ item.label }}</span>
          
          <div v-if="item.id === 'rankings'" class="badge">SEASON 1</div>
        </div>
      </div>

    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const menuItems = ref([
  { id: 'map', label: 'MAP', icon: '🗺️' },
  { id: 'collection', label: 'COLLECTION', icon: '📚' },
  { id: 'rankings', label: 'RANKINGS', icon: '🏆' },
  { id: 'shop', label: 'SHOP', icon: '🛒' },
  { id: 'fittingroom', label: 'FITTINGROOM', icon: '👕' },
  { id: 'profile', label: 'PROFILE', icon: '👤' },
]);

const handleItemClick = (id) => {
  console.log(`Clicked: ${id}`);

  switch (id) {
    case 'map':
      router.push('/quest-map');
      break;
    case 'collection':
      router.push('/collection');
      break;
    case 'rankings':
      router.push('/rankings');
      break;
    case 'shop':
      router.push('/shop');
      break;
    case 'fittingroom':
      router.push('/fitting-room');
      break;
    case 'profile':
      router.push('/profile');
      break;
    default:
      alert(`${id.toUpperCase()} 메뉴는 준비 중입니다!`);
      break;
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.pixel-map-container {
  font-family: 'Press Start 2P', cursive;
  background-color: #fffdf5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  color: #2e1a47;
  
  
  background-image: 
    linear-gradient(45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(-45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(45deg, transparent 75%, #f7f5e6 75%), 
    linear-gradient(-45deg, transparent 75%, #f7f5e6 75%);
  background-size: 40px 40px;
}

.map-header {
  display: flex;
  justify-content: space-between;
  padding: 24px 32px;
  align-items: center;
}

.brand {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.controls {
  display: flex;
  gap: 12px;
}

.pixel-btn {
  background: white;
  border: 3px solid #2e1a47;
  padding: 8px 16px;
  font-family: inherit;
  font-size: 10px;
  cursor: pointer;
  box-shadow: 3px 3px 0px #2e1a47;
  transition: all 0.1s;
}

.pixel-btn:active {
  transform: translate(3px, 3px);
  box-shadow: none;
}

.map-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 60px 80px;
  max-width: 1000px;
  width: 100%;
}

.pixel-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s;
  position: relative;
}

.pixel-card:hover {
  transform: translateY(-8px);
}

.img-box {
  width: 100px;
  height: 100px;
  background: white;
  border: 4px solid #2e1a47;
  box-shadow: 6px 6px 0px rgba(46, 26, 71, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  position: relative;
}

.emoji {
  font-size: 48px;
}

.label {
  font-size: 12px;
  text-align: center;
  letter-spacing: 1px;
  font-weight: bold;
}

.badge {
  position: absolute;
  top: -10px;
  right: -10px;
  background: #ff6b6b;
  color: white;
  border: 2px solid #2e1a47;
  padding: 4px 6px;
  font-size: 8px;
  transform: rotate(5deg);
  box-shadow: 2px 2px 0px #2e1a47;
}

@media (max-width: 900px) {
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 40px;
  }
}

@media (max-width: 600px) {
  .menu-grid {
    grid-template-columns: repeat(1, 1fr);
  }
  .img-box {
    width: 80px;
    height: 80px;
  }
}
</style>