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
          <div class="stat-box green-box">
            <span class="icon">#</span>
            <span class="value">{{ myData.rank }}</span>
            <span class="unit">RANK</span>
          </div>
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
                      :key="user.nickname" 
                      class="table-row"
                      :class="{ 'top-rank': user.rank <= 3 }"
                    >
                      <div class="col rank">
                        <span v-if="user.rank === 1" class="trophy">🏆</span>
                        <span v-else-if="user.rank === 2" class="medal">🥈</span>
                        <span v-else-if="user.rank === 3" class="medal">🥉</span>
                        <span v-else class="rank-num">{{ user.rank }}</span>
                      </div>
          
                      <div class="col player">{{ user.nickname }}</div>
                      
                      <div class="col coins">
                        <span class="coin-icon">🪙</span> {{ user.points.toLocaleString() }}
                      </div>
                      
                      <div class="col level">LV.{{ calculateLevel(user.totalXp) }}</div>
                      
                      <div class="col missions">0</div> <!-- Placeholder for missions -->          </div>
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
import { ref, onMounted } from 'vue';
import { getRankings, getMyRank } from '@/api/ranking.js';
import { calculateLevel } from '@/utils/level.js';

const myData = ref({
  name: 'Loading...',
  rank: '-',
  coins: 0,
  level: 0,
  missions: 0 // Placeholder
});

const rankings = ref([]);

const fetchRankings = async () => {
  try {
    const rankingsResponse = await getRankings(10); // Fetch top 10 rankings

    if (rankingsResponse.success) {
      rankings.value = rankingsResponse.data;
      // Filter out 'UNKNOWN' nicknames if any
      rankings.value = rankings.value.filter(user => user.nickname !== 'UNKNOWN');
    } else {
      console.error('Failed to fetch rankings:', rankingsResponse.message);
    }
  } catch (error) {
    console.error('Error fetching rankings:', error);
  }
};

const fetchMyData = async () => {
  try {
    const myRankResponse = await getMyRank(); // Call without userId
    if (myRankResponse.success) {
      const rankData = myRankResponse.data;
      myData.value.name = rankData.nickname;
      myData.value.rank = rankData.rank;
      myData.value.coins = rankData.points;
      myData.value.level = rankData.level;
    } else {
      myData.value.name = "Rank not found";
    }
  } catch (error) {
    console.error('Error fetching my data:', error);
    myData.value.name = "Error loading rank";
  }
};

onMounted(async () => {
  await fetchRankings();
  await fetchMyData(); // Fetch current user's data
});

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
.green-box { background: #22c55e; color: white; }

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
    flex-wrap: nowrap; /* Prevent wrapping for stat boxes */
    justify-content: flex-end;
    gap: 8px; /* Reduced gap between stat boxes */
  }
  .stat-box {
    width: 50px; /* Reduced width */
    height: 50px; /* Reduced height */
    border-width: 2px;
    gap: 3px;
  }
  .stat-box .icon { font-size: 16px; }
  .stat-box .value { font-size: 10px; }
  .stat-box .unit { font-size: 6px; }

  .table-header, .table-row {
    display: grid; /* Back to grid */
    grid-template-columns: 0.3fr 1fr 0.8fr 0.6fr 0.6fr; /* Aggressively re-proportioned */
    font-size: 7px; /* Extremely small font */
    padding: 5px 3px; /* Minimal padding */
    gap: 2px; /* Minimal gap */
  }
  .col.player {
    word-break: break-all; /* Ensure long names wrap */
    text-shadow: none; /* Remove text shadow for better readability at small size */
  }
  .col.rank, .col.coins, .col.level, .col.missions {
    text-shadow: none; /* Remove text shadow for better readability at small size */
  }
  .col.level, .col.missions {
    display: flex; /* Make them visible again */
    justify-content: flex-end; /* Align to the right, consistent with coins */
    flex-direction: column; /* Stack level and unit if space is very tight */
    align-items: flex-end; /* Align stacked items to the right */
  }
  .col.level span, .col.missions span {
    line-height: 1; /* Tighten line height for stacked elements */
  }
}

@media (max-width: 480px) { /* Even smaller screens */
  .my-rank-card {
    flex-direction: column; /* Revert to column for very small screens */
    align-items: center;
  }
  .my-info-left {
    flex-grow: 0;
  }
  .my-info-right {
    justify-content: center;
    align-self: center;
  }
}
</style>