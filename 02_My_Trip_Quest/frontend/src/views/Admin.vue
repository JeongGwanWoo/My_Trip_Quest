<template>
  <div class="admin-page">
    <h1>관리자 대시보드</h1>
    
    <div class="admin-tabs">
      <button @click="activeTab = 'users'" :class="{ active: activeTab === 'users' }">사용자 관리</button>
      <button @click="activeTab = 'quests'" :class="{ active: activeTab === 'quests' }">퀘스트 관리</button>
      <button @click="activeTab = 'avatar'" :class="{ active: activeTab === 'avatar' }">아바타 관리</button>
      <button @click="activeTab = 'stats'" :class="{ active: activeTab === 'stats' }">통계</button>
    </div>

    <div class="admin-tab-content">
      <UserManagement v-if="activeTab === 'users'" />
      <QuestManagement v-if="activeTab === 'quests'" />
      <AvatarManagement v-if="activeTab === 'avatar'" />
      <StatisticsDashboard v-if="activeTab === 'stats'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import UserManagement from '@/components/admin/UserManagement.vue';
import QuestManagement from '@/components/admin/QuestManagement.vue';
import AvatarManagement from '@/components/admin/AvatarManagement.vue';
import StatisticsDashboard from '@/components/admin/StatisticsDashboard.vue';

const activeTab = ref('users'); // Default to user management
</script>

<style scoped>
.admin-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 20px;
}

.admin-tabs {
  display: flex;
  gap: 10px;
  border-bottom: 2px solid #eee;
  margin-bottom: 20px;
}

.admin-tabs button {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  color: #555;
  border-bottom: 3px solid transparent;
  transition: all 0.2s ease;
}

.admin-tabs button:hover {
  color: #000;
}

.admin-tabs button.active {
  color: #2563eb;
  font-weight: 700;
  border-bottom-color: #2563eb;
}

.admin-tab-content {
  padding: 20px 0;
}
</style>