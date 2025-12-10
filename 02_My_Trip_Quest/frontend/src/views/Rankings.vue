<template>
  <div class="rankings-page">
    <div class="content-container">
      
      <header class="page-header">
        <div class="badge">
          <span class="badge-dot"></span> LEADERBOARD
        </div>
        <h2 class="page-title">
          글로벌 랭킹
        </h2>
        <p class="page-desc">
          여행을 즐기며 코인을 모아 최고의 여행자가 되어보세요! 🏆
        </p>
      </header>

      <section class="my-rank-card">
        <div class="my-profile-section">
          <div class="avatar-circle">
            <span class="avatar-emoji">😊</span>
            <div class="status-indicator"></div>
          </div>
          <div class="text-group">
            <span class="label">나의 순위</span>
            <span class="username">{{ myData.name }}</span>
          </div>
        </div>

        <div class="divider-vertical"></div>

        <div class="my-stats-grid">
          <div class="stat-item">
            <span class="stat-label">RANK</span>
            <span class="stat-value rank-text">#{{ myData.rank }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">COINS</span>
            <span class="stat-value coin-text">{{ myData.coins.toLocaleString() }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">LEVEL</span>
            <span class="stat-value level-text">Lv.{{ myData.level }}</span>
          </div>
        </div>
      </section>

      <section class="leaderboard-card">
        <div class="table-header">
          <div class="col rank">순위</div>
          <div class="col player">플레이어</div>
          <div class="col coins">코인</div>
          <div class="col level">레벨</div>
          <div class="col missions desktop-only">미션</div>
        </div>

        <div class="table-body">
          <div 
            v-for="(user, index) in rankings" 
            :key="user.nickname" 
            class="table-row"
            :class="{ 'my-row': user.nickname === myData.name }"
          >
            <div class="col rank">
              <div v-if="user.rank === 1" class="rank-badge gold">1</div>
              <div v-else-if="user.rank === 2" class="rank-badge silver">2</div>
              <div v-else-if="user.rank === 3" class="rank-badge bronze">3</div>
              <span v-else class="rank-num">{{ user.rank }}</span>
            </div>

            <div class="col player">
              <div class="player-info">
                <div class="player-avatar-sm">{{ user.nickname.charAt(0) }}</div>
                <span class="player-name">{{ user.nickname }}</span>
                <span v-if="user.rank <= 3" class="crown-icon">👑</span>
              </div>
            </div>
            
            <div class="col coins">
              <span class="value">{{ user.points.toLocaleString() }}</span>
              <span class="unit">P</span>
            </div>
            
            <div class="col level">
              <span class="level-badge">Lv.{{ calculateLevel(user.totalXp) }}</span>
            </div>
            
            <div class="col missions desktop-only">
              <span class="mission-count">-</span>
            </div>
          </div>
        </div>
      </section>

      <footer class="info-footer">
        <p>💡 랭킹은 획득한 코인 총합을 기준으로 실시간 업데이트됩니다.</p>
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
  missions: 0
});

const rankings = ref([]);

const fetchRankings = async () => {
  try {
    const rankingsResponse = await getRankings(10);
    if (rankingsResponse.success) {
      rankings.value = rankingsResponse.data.filter(user => user.nickname !== 'UNKNOWN');
    } else {
      console.error('Failed to fetch rankings:', rankingsResponse.message);
    }
  } catch (error) {
    console.error('Error fetching rankings:', error);
  }
};

const fetchMyData = async () => {
  try {
    const myRankResponse = await getMyRank();
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
    myData.value.name = "Error";
  }
};

onMounted(async () => {
  await fetchRankings();
  await fetchMyData();
});
</script>

<style scoped>
/* 기본 폰트 및 배경 설정 */
.rankings-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
  min-height: 100%;
}

.content-container {
  max-width: 900px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}



/* --- Header --- */
.page-header {
  margin-bottom: 8px;
}

.badge {
  display: inline-flex;
  align-items: center;
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 12px;
}
.badge-dot {
  width: 6px;
  height: 6px;
  background-color: #4f46e5;
  border-radius: 50%;
  margin-right: 6px;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.page-desc {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

/* --- My Rank Card (Modern) --- */
.my-rank-card {
  background-color: #fff;
  border-radius: 20px;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #eef2ff;
  position: relative;
  overflow: hidden;
}

/* Accent top border */
.my-rank-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
}

.my-profile-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-circle {
  width: 64px;
  height: 64px;
  background: #eff6ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  position: relative;
}

.status-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  background: #22c55e;
  border: 2px solid #fff;
  border-radius: 50%;
}

.text-group {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 12px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.username {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
}

.divider-vertical {
  width: 1px;
  height: 50px;
  background-color: #e2e8f0;
  margin: 0 30px;
}

.my-stats-grid {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center; /* Center align for stats */
  gap: 4px;
}

.stat-label {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 700;
}

.stat-value {
  font-size: 20px;
  font-weight: 800;
}

.rank-text { color: #3b82f6; }
.coin-text { color: #f59e0b; }
.level-text { color: #8b5cf6; }

/* --- Leaderboard Table (Modern) --- */
.leaderboard-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  border: 1px solid #f1f5f9;
  overflow: hidden;
}

.table-header {
  background-color: #f8fafc;
  display: grid;
  grid-template-columns: 80px 2fr 1.2fr 0.8fr 0.8fr;
  padding: 16px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.table-header .col {
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
}

.table-row {
  display: grid;
  grid-template-columns: 80px 2fr 1.2fr 0.8fr 0.8fr;
  padding: 18px 24px;
  align-items: center;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background-color: #f8fafc;
}

/* Highlight my own row */
.table-row.my-row {
  background-color: #eff6ff;
}

/* Column Alignments */
.col.rank { text-align: center; display: flex; justify-content: center; }
.col.player { text-align: left; }
.col.coins { text-align: right; font-weight: 600; color: #1e293b; }
.col.level { text-align: center; }
.col.missions { text-align: center; color: #94a3b8; }

/* Rank Badges */
.rank-badge {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 14px;
  color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.rank-badge.gold { background: linear-gradient(135deg, #fbbf24, #d97706); }
.rank-badge.silver { background: linear-gradient(135deg, #cbd5e1, #94a3b8); }
.rank-badge.bronze { background: linear-gradient(135deg, #fdba74, #ea580c); }
.rank-num { font-weight: 700; color: #64748b; font-size: 16px; }

/* Player Info */
.player-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.player-avatar-sm {
  width: 32px;
  height: 32px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  color: #64748b;
}
.player-name {
  font-weight: 600;
  color: #334155;
  font-size: 15px;
}
.crown-icon { font-size: 14px; margin-left: 4px; }

/* Coins & Level */
.col.coins .value { font-feature-settings: "tnum"; } /* 숫자 등폭 정렬 */
.col.coins .unit { font-size: 10px; color: #94a3b8; margin-left: 2px; }

.level-badge {
  background: #f1f5f9;
  color: #475569;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

/* Footer */
.info-footer {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  margin-top: 10px;
}

/* 반응형 처리 */
@media (max-width: 768px) {
  .my-rank-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 20px;
  }
  .divider-vertical { display: none; }
  .my-stats-grid {
    width: 100%;
    justify-content: space-between;
    background: #f8fafc;
    padding: 16px;
    border-radius: 12px;
    gap: 0; /* space-between으로 처리 */
  }
  
  /* 테이블 반응형 */
  .table-header, .table-row {
    grid-template-columns: 50px 1fr 0.8fr 0.6fr; /* Missions 컬럼 제외 */
    padding: 16px;
  }
  .desktop-only { display: none !important; }
  
  .player-name { font-size: 14px; }
  .col.coins { font-size: 13px; }
}
</style>