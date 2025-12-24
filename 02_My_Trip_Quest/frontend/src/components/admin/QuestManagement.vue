<template>
  <div class="quest-management">
    <h2>퀘스트 시스템 관리</h2>
    
    <div class="tabs">
      <button 
        :class="['tab-btn', { active: currentTab === 'generator' }]" 
        @click="currentTab = 'generator'"
      >
        퀘스트 생성 (TourAPI)
      </button>
      <button 
        :class="['tab-btn', { active: currentTab === 'list' }]" 
        @click="currentTab = 'list'"
      >
        퀘스트 목록 및 관리
      </button>
      <button 
        :class="['tab-btn', { active: currentTab === 'editor' }]" 
        @click="currentTab = 'editor'"
      >
        위치 편집 (지도)
      </button>
    </div>

    <div class="tab-content">
      <QuestGenerator v-if="currentTab === 'generator'" />
      <QuestList v-if="currentTab === 'list'" />
      <AdminLocationEditor v-if="currentTab === 'editor'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import QuestGenerator from './QuestGenerator.vue';
import QuestList from './QuestList.vue';
import AdminLocationEditor from './AdminLocationEditor.vue';

const currentTab = ref('generator');
</script>

<style scoped>
.quest-management { padding: 20px; }
.tabs { display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 2px solid #ddd; padding-bottom: 10px; }
.tab-btn {
  background: none; border: none; padding: 10px 20px; font-size: 16px; cursor: pointer; color: #666; font-weight: bold;
}
.tab-btn.active {
  color: #007bff; border-bottom: 3px solid #007bff; margin-bottom: -12px;
}
</style>
