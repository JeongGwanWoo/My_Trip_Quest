<template>
  <div class="collection-page">
    
    <main class="collection-content">
      <h2 class="page-title">
        <span class="book-icon">📖</span> SELECT YOUR DESTINATION
      </h2>

      <div class="city-grid">
        <div 
          v-for="city in cities" 
          :key="city.id" 
          class="city-card"
        >
          <div class="card-body">
            <div class="city-icon-box">
              <span class="city-emoji">{{ city.emoji }}</span>
            </div>
            <h3 class="city-name">{{ city.name }}</h3>
            <p class="city-desc">{{ city.desc }}</p>
          </div>

          <div class="progress-section">
            <div class="progress-info">
              <span class="progress-label">PROGRESS</span>
              <span class="progress-count">{{ city.current }}/{{ city.total }}</span>
            </div>
            <div class="progress-bar-bg">
              <div 
                class="progress-bar-fill" 
                :style="{ width: (city.current / city.total) * 100 + '%' }"
              ></div>
            </div>
          </div>

          <div class="card-footer">
            <div class="footer-left">
              <span class="pin-icon">📍</span>
              <span>3 ATTRACTIONS</span>
            </div>
            <span class="arrow-icon">&gt;</span>
          </div>

        </div>
      </div>
    </main>

  </div>
</template>

<script setup>
import { ref } from 'vue';

const cities = ref([
  {
    id: 1,
    name: '서울',
    desc: '대한민국의 수도, 전통과 현대가 공존하는 도시',
    emoji: '🏙️',
    current: 3,
    total: 15
  },
  {
    id: 2,
    name: '부산',
    desc: '아름다운 해변과 항구 도시',
    emoji: '🌊',
    current: 0,
    total: 15
  },
  {
    id: 3,
    name: '제주',
    desc: '아름다운 자연 경관의 섬',
    emoji: '🌴',
    current: 0,
    total: 15
  },
  {
    id: 4,
    name: '경주',
    desc: '천년 고도, 신라의 수도',
    emoji: '🏛️',
    current: 0,
    total: 15
  }
]);
</script>

<style scoped>

.collection-page {
  width: 100%;
  display: flex;
  justify-content: center;
}

.collection-content {
  max-width: 1200px;
  width: 100%;
  padding: 40px 20px;
}

.page-title {
  font-size: 24px;
  color: white;
  text-shadow: 
    -2px -2px 0 #000,  
     2px -2px 0 #000,
    -2px  2px 0 #000,
     2px  2px 0 #000;
  margin-bottom: 40px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.city-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.city-card {
  background-color: #3b82f6;
  border: 4px solid #1e293b;
  box-shadow: 8px 8px 0px rgba(0,0,0,0.2);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 400px;
  transition: transform 0.2s;
  cursor: pointer;
}

.city-card:hover {
  transform: translateY(-5px);
}

.card-body {
  padding: 24px;
  flex: 1;
}

.city-icon-box {
  background: rgba(255, 255, 255, 0.2);
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 2px solid rgba(0,0,0,0.1);
}

.city-emoji {
  font-size: 32px;
}

.city-name {
  font-size: 20px;
  margin: 0 0 15px 0;
  text-shadow: 2px 2px 0 rgba(0,0,0,0.3);
}

.city-desc {
  font-size: 10px;
  line-height: 1.6;
  opacity: 0.9;
  min-height: 40px;
}

.progress-section {
  background-color: #1e293b;
  padding: 15px;
  margin: 0 20px;
  border: 2px solid #000;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 8px;
  margin-bottom: 8px;
  color: #fbbf24;
  font-weight: bold;
}

.progress-count {
  color: white;
}

.progress-bar-bg {
  width: 100%;
  height: 8px;
  background-color: #334155;
  position: relative;
}

.progress-bar-fill {
  height: 100%;
  background-color: #22c55e;
  transition: width 0.5s ease;
}

.card-footer {
  margin-top: 20px;
  padding: 15px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 10px;
  border-top: 2px solid rgba(0,0,0,0.1);
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.pin-icon {
  color: #ef4444;
}

.arrow-icon {
  color: #fbbf24;
}

@media (max-width: 1024px) {
  .city-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .city-grid {
    grid-template-columns: 1fr;
  }
}
</style>