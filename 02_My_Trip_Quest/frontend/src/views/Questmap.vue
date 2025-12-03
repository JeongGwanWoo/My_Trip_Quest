<template>
  <div class="quest-map-page">
    
    <div class="content-container">
      
      <section class="map-section">
        <h2 class="section-title">📍 QUEST MAP</h2>
        
        <div class="map-board">
          <div class="korea-map-shape">
            
            <div class="map-pin pin-seoul" style="top: 20%; left: 45%;">
              <div class="pin-head red">📍</div>
              <div class="pin-label">서울</div>
              <div class="pin-badge">3</div>
            </div>

            <div class="map-pin pin-gyeongju" style="top: 50%; left: 55%;">
              <div class="pin-head purple">📍</div>
              <div class="pin-label">경주</div>
              <div class="pin-badge">0</div>
            </div>

            <div class="map-pin pin-busan" style="top: 65%; left: 60%;">
              <div class="pin-head blue">📍</div>
              <div class="pin-label">부산</div>
              <div class="pin-badge">0</div>
            </div>

            <div class="map-pin pin-jeju" style="top: 85%; left: 30%;">
              <div class="pin-head green">📍</div>
              <div class="pin-label">제주</div>
              <div class="pin-badge">0</div>
            </div>

          </div>

          <div class="map-legend">
            <div class="legend-item">
              <span class="dot yellow"></span> IN PROGRESS
            </div>
            <div class="legend-item">
              <span class="dot green"></span> COMPLETED
            </div>
          </div>
        </div>
      </section>

      <section class="quest-list-section">
        <h2 class="section-title">🎯 AVAILABLE QUESTS</h2>

        <div class="quest-list">
          <div 
            v-for="quest in quests" 
            :key="quest.id" 
            class="quest-card"
            :class="quest.colorClass"
          >
            <div class="quest-info">
              <div class="quest-icon">{{ quest.icon }}</div> 
              <div class="quest-text">
                <div class="quest-name">{{ quest.name }}</div>
                <div class="quest-sub">{{ quest.completed }}/{{ quest.total }} QUESTS COMPLETED</div>
              </div>
            </div>
            
            <div class="quest-status">
              <div class="percentage-badge">{{ quest.percentage }}%</div>
              <button class="arrow-btn">⌄</button>
            </div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const quests = ref([
  { 
    id: 1, 
    name: '서울', 
    completed: 3, 
    total: 15, 
    percentage: 20, 
    colorClass: 'bg-red', 
    icon: '🏙️' 
  },
  { 
    id: 2, 
    name: '부산', 
    completed: 0, 
    total: 15, 
    percentage: 0, 
    colorClass: 'bg-blue',
    icon: '🌊' 
  },
  { 
    id: 3, 
    name: '제주', 
    completed: 0, 
    total: 15, 
    percentage: 0, 
    colorClass: 'bg-green',
    icon: '🌴' 
  },
  { 
    id: 4, 
    name: '경주', 
    completed: 0, 
    total: 15, 
    percentage: 0, 
    colorClass: 'bg-purple',
    icon: '🏛️' 
  }
]);
</script>

<style scoped>

.quest-map-page {
  width: 100%;
  display: flex;
  justify-content: center;
}

.content-container {
  max-width: 800px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.section-title {
  font-size: 16px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  text-shadow: 2px 2px 0px rgba(0,0,0,0.1);
  color: #1e1e1e;
}

.map-board {
  background: white;
  border: 4px solid #1e293b;
  padding: 40px;
  height: 400px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 6px 6px 0px rgba(0,0,0,0.2);
}

.korea-map-shape {
  width: 300px;
  height: 100%;
  background: linear-gradient(160deg, #475569 0%, #cbd5e1 100%);
  border-radius: 100px;
  position: relative;
  border: 4px solid #1e293b;
}

.map-pin {
  position: absolute;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  z-index: 10;
}

.pin-head {
  font-size: 24px;
  filter: drop-shadow(2px 2px 0px black);
  transition: transform 0.2s;
}

.map-pin:hover .pin-head {
  transform: scale(1.2) translateY(-5px);
}

.pin-label {
  background: white;
  border: 2px solid black;
  padding: 4px 6px;
  font-size: 8px;
  margin-top: 4px;
  box-shadow: 2px 2px 0px black;
}

.pin-head.red { color: #ef4444; }
.pin-head.blue { color: #3b82f6; }
.pin-head.green { color: #22c55e; }
.pin-head.purple { color: #a855f7; }

.pin-badge {
  position: absolute;
  top: -5px;
  right: -10px;
  background: #fbbf24;
  border: 2px solid black;
  font-size: 8px;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: black;
}

.map-legend {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  gap: 15px;
  font-size: 8px;
  color: #64748b;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.dot {
  width: 10px;
  height: 10px;
  border: 2px solid black;
  border-radius: 50%;
}
.dot.yellow { background: #fbbf24; }
.dot.green { background: #22c55e; }

.quest-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.quest-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border: 4px solid #1e293b;
  box-shadow: 4px 4px 0px rgba(0,0,0,0.2);
  color: white;
  cursor: pointer;
  transition: transform 0.1s;
}

.quest-card:hover {
  transform: translate(-2px, -2px);
  box-shadow: 6px 6px 0px rgba(0,0,0,0.2);
}

.bg-red { background-color: #ef4444; }
.bg-blue { background-color: #3b82f6; }
.bg-green { background-color: #22c55e; }
.bg-purple { background-color: #a855f7; }

.quest-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quest-icon {
  font-size: 24px;
  background: rgba(255,255,255,0.2);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(0,0,0,0.2);
  border-radius: 4px;
}

.quest-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quest-name {
  font-size: 14px;
  text-shadow: 2px 2px 0px rgba(0,0,0,0.3);
}

.quest-sub {
  font-size: 8px;
  opacity: 0.9;
}

.quest-status {
  display: flex;
  align-items: center;
  gap: 15px;
}

.percentage-badge {
  background: #fbbf24;
  color: black;
  border: 2px solid black;
  padding: 6px 10px;
  font-size: 10px;
  border-radius: 20px;
  box-shadow: 2px 2px 0px rgba(0,0,0,0.3);
}

.arrow-btn {
  background: transparent;
  border: none;
  color: white;
  font-family: inherit;
  font-size: 16px;
  cursor: pointer;
}

@media (max-width: 600px) {
  .quest-card { padding: 10px; }
  .quest-info { gap: 10px; }
}
</style>