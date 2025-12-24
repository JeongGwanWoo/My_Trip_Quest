<template>
  <div class="quest-list-container">
    <div class="controls">
      <label>지역 선택:</label>
      <select v-model="selectedAreaCode" @change="fetchLocations">
          <option value="ALL">전체 보기 (ALL)</option>
          <option value="1">서울 (1)</option>
          <option value="2">인천 (2)</option>
          <option value="3">대전 (3)</option>
          <option value="4">대구 (4)</option>
          <option value="5">광주 (5)</option>
          <option value="6">부산 (6)</option>
          <option value="7">울산 (7)</option>
          <option value="8">세종 (8)</option>
          <option value="31">경기 (31)</option>
          <option value="32">강원 (32)</option>
          <option value="33">충북 (33)</option>
          <option value="34">충남 (34)</option>
          <option value="35">경북 (35)</option>
          <option value="36">경남 (36)</option>
          <option value="37">전북 (37)</option>
          <option value="38">전남 (38)</option>
          <option value="39">제주 (39)</option>
      </select>
      <button @click="fetchLocations" class="btn-refresh">새로고침</button>
      <button v-if="selectedLocationIds.length > 0" @click="batchRecalculate" class="btn-batch-ai" :disabled="recalculating">
        {{ recalculating ? 'AI 재산정 중...' : `선택 항목 AI 재산정 (${selectedLocationIds.length}개)` }}
      </button>
    </div>

    <div v-if="loading" class="loading">데이터 불러오는 중...</div>
    
    <div v-else-if="locations.length === 0" class="empty-state">
      등록된 퀘스트가 없습니다.
    </div>

    <table v-else class="quest-table">
      <thead>
        <tr>
          <th><input type="checkbox" @change="toggleAll" :checked="allSelected" /></th>
          <th>ID</th>
          <th>지역</th>
          <th>관광지명</th>
          <th>인증 반경</th>
          <th>연결된 퀘스트</th>
          <th>관리</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="loc in locations" :key="loc.locationId">
          <td><input type="checkbox" :value="loc.locationId" v-model="selectedLocationIds" /></td>
          <td>{{ loc.locationId }}</td>
          <td>{{ getAreaName(loc.areaCode) }}</td>
          <td>{{ loc.title }}</td>
          <td>{{ loc.gpsVerifyRadius }}m</td>
          <td>{{ loc.questCount }}개</td>
          <td>
            <button @click="openEditModal(loc)" class="btn-edit">수정 / 관리</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="hasMore" class="load-more">
        <button @click.prevent="loadMore" class="btn-secondary">더 보기 ({{ locations.length }}개 조회됨)</button>
    </div>

    <QuestEditModal 
      :isVisible="isModalOpen" 
      :location="selectedLocation"
      @close="closeModal"
      @refresh="fetchLocations"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue';
import { getDbLocations, batchRecalculateRadius } from '@/api/admin';
import QuestEditModal from './QuestEditModal.vue';

const selectedAreaCode = ref("ALL"); // 기본 전체 보기
const locations = ref([]);
const loading = ref(false);
const pageNo = ref(0);
const selectedLocationIds = ref([]);
const recalculating = ref(false);

const allSelected = computed(() => {
  return locations.value.length > 0 && selectedLocationIds.value.length === locations.value.length;
});
const hasMore = ref(false);

const isModalOpen = ref(false);
const selectedLocation = ref(null);

// 지역 코드 매핑
const areaNames = {
    "1": "서울", "2": "인천", "3": "대전", "4": "대구", "5": "광주", "6": "부산", "7": "울산", "8": "세종",
    "31": "경기", "32": "강원", "33": "충북", "34": "충남", "35": "경북", "36": "경남", "37": "전북", "38": "전남", "39": "제주"
};

const getAreaName = (code) => areaNames[code] || code;

const fetchLocations = async (reset = true) => {
    if (reset) {
        pageNo.value = 0;
        locations.value = [];
    }
    
    loading.value = true;
    try {
        const res = await getDbLocations({
            areaCode: selectedAreaCode.value,
            page: pageNo.value,
            size: 10 // 사용자 요청: 10개씩 조회
        });
        
        const content = res.data.data.content; // QuestLocationSliceDto.content
        const isLast = res.data.data.last;

        if (reset) {
            locations.value = content;
        } else {
            locations.value = [...locations.value, ...content];
        }
        
        hasMore.value = !isLast;
        if (!isLast) pageNo.value++;

    } catch (error) {
        console.error("데이터 로드 실패:", error);
    } finally {
        loading.value = false;
    }
};

const loadMore = async () => {
    // 현재 스크롤 위치 저장
    const scrollY = window.scrollY;
    
    await fetchLocations(false); // reset=false
    
    // 다음 틱에 스크롤 위치 복원
    await nextTick();
    window.scrollTo(0, scrollY);
};

const toggleAll = () => {
  if (allSelected.value) {
    selectedLocationIds.value = [];
  } else {
    selectedLocationIds.value = locations.value.map(loc => loc.locationId);
  }
};

const batchRecalculate = async () => {
  if (selectedLocationIds.value.length === 0) {
    alert('선택된 관광지가 없습니다.');
    return;
  }
  
  if (!confirm(`선택한 ${selectedLocationIds.value.length}개 관광지의 GPS 반경을 AI로 재산정하시겠습니까?`)) {
    return;
  }
  
  recalculating.value = true;
  try {
    const res = await batchRecalculateRadius(selectedLocationIds.value);
    if (res.data.success) {
      const result = res.data.data;
      alert(`AI 재산정 완료!\n업데이트: ${result.updated}/${result.total}개`);
      selectedLocationIds.value = [];
      await fetchLocations();
    } else {
      alert('AI 재산정 실패: ' + (res.data.message || '알 수 없는 오류'));
    }
  } catch (error) {
    console.error('AI 재산정 오류:', error);
    alert('AI 재산정 중 오류가 발생했습니다.');
  } finally {
    recalculating.value = false;
  }
};

const openEditModal = (loc) => {
    selectedLocation.value = loc;
    isModalOpen.value = true;
};

const closeModal = () => {
    isModalOpen.value = false;
    selectedLocation.value = null;
};

onMounted(() => {
    fetchLocations();
});
</script>

<style scoped>
.quest-list-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.controls {
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.controls label {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

.controls select {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 150px;
}

.controls select:hover {
  border-color: #3b82f6;
}

.controls select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.quest-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-top: 16px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.quest-table th {
  background: #f1f5f9;
  color: #334155;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  font-size: 13px;
  border-bottom: 2px solid #e2e8f0;
}

.quest-table td {
  padding: 12px;
  border-bottom: 1px solid #f1f5f9;
  color: #475569;
  font-size: 14px;
}

.quest-table tbody tr {
  transition: background 0.2s ease;
}

.quest-table tbody tr:hover {
  background: #f8fafc;
}

.quest-table tbody tr:last-child td {
  border-bottom: none;
}

.btn-edit {
  background-color: #3b82f6;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 13px;
  transition: background 0.2s ease;
}

.btn-edit:hover {
  background-color: #2563eb;
}

.btn-edit:active {
  background-color: #1d4ed8;
}

.btn-refresh {
  background-color: #10b981;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  transition: background 0.2s ease;
}

.btn-refresh:hover {
  background-color: #059669;
}

.btn-batch-ai {
  background: #8b5cf6;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 13px;
  transition: background 0.2s;
  margin-left: auto;
}

.btn-batch-ai:hover:not(:disabled) {
  background: #7c3aed;
}

.btn-batch-ai:disabled {
  background: #cbd5e1;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background-color: #e2e8f0;
}

.load-more {
  text-align: center;
  margin-top: 20px;
  padding: 16px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: #64748b;
  font-size: 14px;
  background: #f8fafc;
  border-radius: 6px;
  border: 1px dashed #cbd5e1;
}

.loading {
  padding: 40px;
  text-align: center;
  color: #3b82f6;
  font-weight: 600;
  font-size: 14px;
}

.loading::after {
  content: '...';
  animation: dots 1.5s steps(4, end) infinite;
}

@keyframes dots {
  0%, 20% { content: '.'; }
  40% { content: '..'; }
  60%, 100% { content: '...'; }
}
</style>
