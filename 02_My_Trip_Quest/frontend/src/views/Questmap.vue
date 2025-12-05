<template>
  <div class="quest-map-page">
    <div class="content-container">
      <section class="map-section">
        <h2 class="section-title">📍 QUEST MAP</h2>

        <div class="map-board">
          <MapComponent :areas="areas" />
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
                <div class="quest-sub">
                  {{ quest.completed }}/{{ quest.total }} QUESTS COMPLETED
                </div>
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
import { ref, onMounted } from "vue";
import MapComponent from "@/components/map/MapComponent.vue";
import api from "@/api";

const areas = ref([]); // 미션 지역 데이터를 저장할 ref

onMounted(async () => {
  try {
    const response = await api.get("/api/v1/quest-map/areas");
    areas.value = response.data.data; // 서버에서 받은 데이터 객체 내의 'data' 배열을 areas ref에 저장
    console.log("미션 지역 데이터:", areas.value);
  } catch (error) {
    console.error("미션 지역 데이터를 가져오는 중 오류 발생:", error);
  }
});

const quests = ref([
  {
    id: 1,
    name: "서울",
    completed: 3,
    total: 15,
    percentage: 20,
    colorClass: "bg-red",
    icon: "🏙️",
  },
  {
    id: 2,
    name: "부산",
    completed: 0,
    total: 15,
    percentage: 0,
    colorClass: "bg-blue",
    icon: "🌊",
  },
  {
    id: 3,
    name: "제주",
    completed: 0,
    total: 15,
    percentage: 0,
    colorClass: "bg-green",
    icon: "🌴",
  },
  {
    id: 4,
    name: "경주",
    completed: 0,
    total: 15,
    percentage: 0,
    colorClass: "bg-purple",
    icon: "🏛️",
  },
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
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.1);
  color: #1e1e1e;
}

.map-board {
  background: white;
  border: 4px solid #1e293b;
  padding: 40px;
  height: 600px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 6px 6px 0px rgba(0, 0, 0, 0.2);
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
.dot.yellow {
  background: #fbbf24;
}
.dot.green {
  background: #22c55e;
}

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
  box-shadow: 4px 4px 0px rgba(0, 0, 0, 0.2);
  color: white;
  cursor: pointer;
  transition: transform 0.1s;
}

.quest-card:hover {
  transform: translate(-2px, -2px);
  box-shadow: 6px 6px 0px rgba(0, 0, 0, 0.2);
}

.bg-red {
  background-color: #ef4444;
}
.bg-blue {
  background-color: #3b82f6;
}
.bg-green {
  background-color: #22c55e;
}
.bg-purple {
  background-color: #a855f7;
}

.quest-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quest-icon {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.quest-text {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quest-name {
  font-size: 14px;
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
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
  box-shadow: 2px 2px 0px rgba(0, 0, 0, 0.3);
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
  .quest-card {
    padding: 10px;
  }
  .quest-info {
    gap: 10px;
  }
}
</style>
