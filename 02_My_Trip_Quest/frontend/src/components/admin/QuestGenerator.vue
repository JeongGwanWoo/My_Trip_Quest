<template>
  <div class="quest-generator">
    <h3>관광지 기반 퀘스트 자동 생성</h3>
    
    <div class="controls">
      <div class="control-group">
        <label>지역 선택:</label>
        <select v-model="selectedAreaCode" @change="fetchAttractions(false)">
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
      </div>

      <div class="control-group">
        <label>관광지 타입:</label>
        <select v-model="selectedContentTypeId" @change="fetchAttractions(false)">
          <option value="12">관광지 (12)</option>
          <option value="14">문화시설 (14)</option>
          <option value="15">축제/공연 (15)</option>
          <option value="28">레포츠 (28)</option>
        </select>
      </div>
      
      <button @click="fetchAttractions(false)" :disabled="loading" class="btn-primary">
        목록 조회
      </button>
    </div>

    <div v-if="attractions.length > 0 || loading" class="attraction-list-container">
      <div class="list-header">
        <label>
          <input type="checkbox" v-model="selectAll" @change="toggleSelectAll"> 전체 선택
        </label>
        <div class="header-status">
            <span v-if="loading" class="status-loading">데이터 불러오는 중...</span>
            <span>총 {{ attractions.length }}개 중 {{ selectedAttractions.length }}개 선택됨</span>
        </div>
      </div>
      
      <div class="attraction-list">
        <div v-for="item in attractions" :key="item.contentid" class="attraction-item">
          <label class="item-label">
            <input type="checkbox" :value="item" v-model="selectedAttractions">
            <div class="item-info">
              <span class="item-title">{{ item.title }}</span>
              <span class="item-addr">{{ item.addr1 }}</span>
            </div>
            <div class="item-image" v-if="item.firstimage">
               <img :src="item.firstimage" alt="Thumbnail" />
            </div>
          </label>
        </div>
      </div>
      
      <button v-if="hasMore && attractions.length > 0" @click="fetchAttractions(true)" :disabled="loading" class="btn-secondary">
        <span v-if="loading">로딩 중...</span>
        <span v-else>더 보기 +</span>
      </button>

      <div class="generation-options">
        <h4>생성 옵션</h4>
        <div class="option-group">
          <label>
            <input type="checkbox" v-model="questTypes" value="ARRIVAL" checked> 도착 인증 퀘스트 (XP: 50, Point: 5)
          </label>
          <label>
            <input type="checkbox" v-model="questTypes" value="PHOTO"> 사진 인증 퀘스트 (XP: 150, Point: 15)
          </label>
        </div>
        
        <button @click="handleGenerate" :disabled="selectedAttractions.length === 0 || questTypes.length === 0 || generating" class="btn-success">
          <span v-if="generating">생성 중...</span>
          <span v-else>선택한 {{ selectedAttractions.length }}개 관광지로 퀘스트 생성</span>
        </button>
      </div>
    </div>
    
    <div v-else-if="!loading && searched" class="no-data">
      조회된 관광지가 없습니다.
    </div>

    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { getTourApiAttractions, generateQuests } from '@/api/admin';

const selectedAreaCode = ref('1');
const selectedContentTypeId = ref('12');
const attractions = ref([]);
const selectedAttractions = ref([]);
const questTypes = ref(['ARRIVAL', 'PHOTO']);
const loading = ref(false);
const generating = ref(false);
const searched = ref(false);
const successMessage = ref('');
const errorMessage = ref('');

const selectAll = ref(false);

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedAttractions.value = [...attractions.value];
  } else {
    selectedAttractions.value = [];
  }
};

const pageNo = ref(1);
const hasMore = ref(true);

const fetchAttractions = async (isLoadMore = false) => {
  if (isLoadMore) {
    pageNo.value++;
  } else {
    pageNo.value = 1;
    attractions.value = [];
    selectedAttractions.value = [];
    selectAll.value = false;
    hasMore.value = true;
    searched.value = true;
  }
  
  loading.value = true;
  errorMessage.value = '';
  successMessage.value = '';

  try {
    const params = {
      areaCode: selectedAreaCode.value,
      contentTypeId: selectedContentTypeId.value,
      numOfRows: 10, 
      pageNo: pageNo.value
    };
    
    const response = await getTourApiAttractions(params);
    
    if (response.data && response.data.success) {
         const data = response.data.data;
         
         // TourAPI Structure: data.response.body.items.item
         if (data?.response?.body?.items?.item) {
             const items = data.response.body.items.item;
             const newItems = Array.isArray(items) ? items : [items];
             
             if (isLoadMore) {
               attractions.value = [...attractions.value, ...newItems];
             } else {
               attractions.value = newItems;
             }
             
             // 만약 가져온 데이터가 요청한 개수(10)보다 적으면 더 이상 데이터가 없는 것으로 간주
             if (newItems.length < 10) {
               hasMore.value = false;
             }
         } else {
             if (!isLoadMore) attractions.value = [];
             hasMore.value = false;
         }
    } else {
         errorMessage.value = response.data.message || '데이터 조회 실패';
    }

  } catch (error) {
    console.error(error);
    errorMessage.value = 'API 호출 중 오류가 발생했습니다.';
  } finally {
    loading.value = false;
  }
};

const handleGenerate = async () => {
  if (!confirm(`${selectedAttractions.value.length}개의 관광지에 대해 퀘스트를 생성하시겠습니까?`)) {
    return;
  }

  generating.value = true;
  successMessage.value = '';
  errorMessage.value = '';

  try {
    const payload = {
      items: selectedAttractions.value, // 선택된 객체들 그대로 전송 (Backend에서 mapx, mapy, title 등 추출 사용)
      questTypes: questTypes.value,
      areaCode: selectedAreaCode.value
    };

    const response = await generateQuests(payload);
    if (response.data && response.data.success) {
      // response.data.data는 생성된 퀘스트 수(int)
      successMessage.value = `성공적으로 ${response.data.data}개의 퀘스트가 생성되었습니다!`;
      selectedAttractions.value = [];
      selectAll.value = false;
    } else {
      errorMessage.value = response.data.message || '퀘스트 생성 실패';
    }
  } catch (error) {
    console.error(error);
    errorMessage.value = '퀘스트 생성 중 오류가 발생했습니다. (백엔드 로그 확인 필요)';
  } finally {
    generating.value = false;
  }
};
</script>

<style scoped>
.quest-generator {
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.controls {
  display: flex;
  gap: 20px;
  align-items: flex-end;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.control-group label {
  font-weight: 600;
  font-size: 14px;
}

select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 150px;
}

.btn-primary {
  padding: 8px 16px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  height: 38px;
}

.btn-success {
  padding: 12px 24px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  width: 100%;
  margin-top: 10px;
}

.btn-primary:disabled, .btn-success:disabled, .btn-secondary:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.btn-secondary {
  width: 100%;
  padding: 10px;
  background-color: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
}
.btn-secondary:hover:not(:disabled) {
  background-color: #e2e8f0;
}

.attraction-list-container {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 16px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
  font-weight: 600;
}

.header-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-loading {
  color: #3b82f6;
  font-size: 0.9em;
  font-weight: normal;
}


.attraction-list {
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attraction-item label {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.attraction-item label:hover {
  background-color: #f8fafc;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-title {
  font-weight: 600;
  color: #333;
}

.item-addr {
  font-size: 12px;
  color: #666;
}

.item-image img {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.generation-options {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.option-group {
  display: flex;
  gap: 20px;
  margin: 10px 0;
}

.loading, .no-data {
  text-align: center;
  padding: 40px;
  color: #64748b;
}

.success-message {
  margin-top: 20px;
  padding: 12px;
  background-color: #d1fae5;
  color: #065f46;
  border-radius: 6px;
  text-align: center;
}

.error-message {
  margin-top: 20px;
  padding: 12px;
  background-color: #fee2e2;
  color: #991b1b;
  border-radius: 6px;
  text-align: center;
}
</style>
