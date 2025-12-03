<template>
  <div class="rankings-page">
    <div class="content-container">
      
      <h2 class="page-title">
        <span class="title-icon">🏆</span> GLOBAL RANKINGS
      </h2>

      <section class="my-rank-card">
        <div class="my-info-left">
          <div class="avatar-circle">😊</div>
          <div class="text-group">
            <span class="label">YOUR RANK</span>
            <span class="username">{{ myData.name }}</span>
          </div>
        </div>

        <div class="my-info-right">
          <div class="stat-box yellow-box">
            <span class="icon">🪙</span>
            <span class="value">{{ myData.coins }}</span>
            <span class="unit">COINS</span>
          </div>
          <div class="stat-box blue-box">
            <span class="icon">⭐</span>
            <span class="value">{{ myData.level }}</span>
            <span class="unit">LEVEL</span>
          </div>
        </div>
      </section>

      <section class="leaderboard-container">
        
        <div class="table-header">
          <div class="col rank">RANK</div>
          <div class="col player">PLAYER</div>
          <div class="col coins">COINS</div>
          <div class="col level">LEVEL</div>
          <div class="col missions">MISSIONS</div>
        </div>

        <div class="table-body">
          <div 
            v-for="(user, index) in rankings" 
            :key="user.id" 
            class="table-row"
            :class="{ 'top-rank': index < 3 }"
          >
            <div class="col rank">
              <span v-if="user.rank === 1" class="trophy">🏆</span>
              <span v-else-if="user.rank === 2" class="medal">🥈</span>
              <span v-else-if="user.rank === 3" class="medal">🥉</span>
              <span v-else class="rank-num">{{ user.rank }}</span>
            </div>

            <div class="col player">{{ user.name }}</div>
            
            <div class="col coins">
              <span class="coin-icon">🪙</span> {{ user.coins.toLocaleString() }}
            </div>
            
            <div class="col level">LV.{{ user.level }}</div>
            
            <div class="col missions">{{ user.missions }}</div>
          </div>
        </div>

      </section>

      <footer class="info-footer">
        <span class="bulb">💡</span>
        COMPLETE MORE MISSIONS AND EARN COINS TO CLIMB THE RANKINGS! <br/>
        RANKINGS UPDATE IN REAL-TIME BASED ON YOUR TOTAL COINS.
      </footer>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const myData = ref({
  name: 'TRAVELMASTER',
  coins: 5000,
  level: 12
});

const rankings = ref([
  { id: 1, rank: 1, name: 'QUESTMASTER', coins: 15000, level: 25, missions: 48 },
  { id: 2, rank: 2, name: 'TRAVELNINJA', coins: 14200, level: 23, missions: 45 },
  { id: 3, rank: 3, name: 'ADVENTUREKING', coins: 12900, level: 21, missions: 42 },
  { id: 4, rank: 4, name: 'EXPLOREQUEEN', coins: 11500, level: 20, missions: 39 },
  { id: 5, rank: 5, name: 'WANDERERPRO', coins: 10800, level: 19, missions: 37 },
  { id: 6, rank: 6, name: 'JOURNEYSEEKER', coins: 9200, level: 17, missions: 34 },
  { id: 7, rank: 7, name: 'MAPEXPLORER', coins: 8500, level: 16, missions: 31 },
  { id: 8, rank: 8, name: 'ROADTRIPPER', coins: 7800, level: 15, missions: 28 },
  { id: 9, rank: 9, name: 'GLOBETREKKER', coins: 6800, level: 14, missions: 25 },
  { id: 10, rank: 10, name: 'TRAVELBUFF', coins: 6100, level: 13, missions: 23 },
]);
</script>

<style scoped>

.rankings-page {
  width: 100%;
  display: flex;
  justify-content: center;
  color: #1e1e1e;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.page-title {
  font-size: 24px;
  color: white;
  text-shadow: 2px 2px 0 #000;
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 0;
}

.my-rank-card {
  background-color: #8b5cf6;
  border: 4px solid #fbbf24;
  padding: 20px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 6px 6px 0px rgba(0,0,0,0.2);
  color: white;
}

.my-info-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-circle {
  width: 60px;
  height: 60px;
  background: #fbbf24;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  border: 3px solid #000;
}

.text-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 10px;
  color: #fbbf24;
  font-weight: bold;
}

.username {
  font-size: 24px;
  text-shadow: 2px 2px 0 #000;
}

.my-info-right {
  display: flex;
  gap: 15px;
}

.stat-box {
  width: 80px;
  height: 80px;
  border: 3px solid #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  box-shadow: 3px 3px 0 rgba(0,0,0,0.3);
}

.yellow-box { background: #fbbf24; color: black; }
.blue-box { background: #3b82f6; color: white; }

.stat-box .icon { font-size: 20px; }
.stat-box .value { font-size: 14px; font-weight: bold; }
.stat-box .unit { font-size: 8px; }

.leaderboard-container {
  border: 4px solid #1e293b;
  box-shadow: 6px 6px 0px rgba(0,0,0,0.2);
}

.table-header {
  background-color: #1e293b;
  color: #fbbf24;
  display: grid;
  grid-template-columns: 0.8fr 3fr 1.5fr 1fr 1fr;
  padding: 15px 20px;
  font-size: 10px;
  font-weight: bold;
  text-transform: uppercase;
}

.table-body {
  background-color: #3b82f6;
}

.table-row {
  display: grid;
  grid-template-columns: 0.8fr 3fr 1.5fr 1fr 1fr;
  padding: 15px 20px;
  align-items: center;
  border-bottom: 2px solid rgba(0,0,0,0.1);
  color: white;
  font-size: 12px;
  transition: background 0.2s;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background-color: #2563eb;
}

.top-rank .rank {
  font-size: 20px;
}

.col.rank { text-align: center; font-weight: bold; }
.col.player { text-align: left; text-shadow: 1px 1px 0 #000; }
.col.coins { text-align: right; color: #fbbf24; font-weight: bold; text-shadow: 1px 1px 0 #000; }
.col.level { text-align: right; }
.col.missions { text-align: right; color: #000; font-weight: bold; }

.info-footer {
  background-color: #1e293b;
  color: #64748b;
  padding: 20px;
  font-size: 8px;
  text-align: center;
  line-height: 1.6;
  border: 4px solid #000;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.bulb { font-size: 14px; margin-bottom: 5px; }

@media (max-width: 768px) {
  .my-rank-card {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }
  .my-info-right {
    align-self: flex-end;
  }
  
  .table-header, .table-row {
    grid-template-columns: 0.5fr 2fr 1.5fr;
  }
  .col.level, .col.missions {
    display: none;
  }
}
</style>