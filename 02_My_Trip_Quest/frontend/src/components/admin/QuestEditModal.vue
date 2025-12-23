<template>
  <div v-if="isVisible" class="modal-overlay">
    <div class="modal-content">
      <h3>관광지 및 퀘스트 수정</h3>
      
      <div v-if="loading" class="loading">로딩 중...</div>
      <div v-else>
        <!-- 관광지 정보 수정 -->
        <div class="section location-info">
          <h4>관광지 정보</h4>
          <div class="form-group">
            <label>관광지명:</label>
            <input type="text" v-model="localLocation.title" disabled class="input-disabled" />
          </div>
          <div class="form-group">
            <label>GPS 인증 반경 (m):</label>
            <div class="radius-group">
              <input type="number" v-model="localLocation.gpsVerifyRadius" />
              <button @click="autoCalculateRadius" class="btn-ai" :disabled="aiLoading">
                {{ aiLoading ? '계산 중...' : 'AI 자동 산출' }}
              </button>
            </div>
            <small class="hint">AI에게 이 관광지의 적절한 인증 반경을 물어봅니다.</small>
          </div>
          <button @click="saveLocation" class="btn-primary">정보 저장</button>
        </div>

        <hr />

        <!-- 퀘스트 목록 -->
        <div class="section quest-list">
          <h4>등록된 퀘스트 ({{ quests.length }})</h4>
          <ul>
            <li v-for="quest in quests" :key="quest.questId" class="quest-item">
              <div class="quest-info">
                <span class="quest-type">[{{ getQuestTypeName(quest.questTypeId) }}]</span>
                <span class="quest-title">{{ quest.title }}</span>
              </div>
              <button @click="deleteQuestItem(quest.questId)" class="btn-delete">삭제</button>
            </li>
          </ul>
        </div>

        <!-- 퀘스트 추가 -->
        <div class="section add-quest">
          <h4>새 퀘스트 추가</h4>
          <div class="add-form">
            <select v-model="newQuestType">
              <option value="1">도착 퀘스트</option>
              <option value="2">사진 퀘스트</option>
            </select>
            <input type="text" v-model="newQuestTitle" placeholder="퀘스트 제목" />
            <button @click="addNewQuest" class="btn-add">추가</button>
          </div>
        </div>

      </div>
      
      <button @click="close" class="btn-close">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { getDbQuests, updateLocationRadius, deleteQuest, addQuest, estimateLocationRadius } from '@/api/admin';

const props = defineProps({
  isVisible: Boolean,
  location: Object
});

const emit = defineEmits(['close', 'refresh']);

const loading = ref(false);
const aiLoading = ref(false);
const quests = ref([]);
const localLocation = ref({});

const newQuestType = ref("2"); // 기본 사진 퀘스트
const newQuestTitle = ref("");

watch(() => props.isVisible, async (newVal) => {
  if (newVal && props.location) {
    localLocation.value = { ...props.location };
    // fetch quests
    await fetchQuests();
  }
});

const fetchQuests = async () => {
    loading.value = true;
    try {
        const res = await getDbQuests(props.location.locationId);
        quests.value = res.data.data;
    } catch (error) {
        console.error("퀘스트 조회 실패:", error);
        alert("퀘스트 목록을 불러오지 못했습니다.");
    } finally {
        loading.value = false;
    }
};

const getQuestTypeName = (typeId) => {
    return typeId === 1 ? '도착' : (typeId === 2 ? '사진' : '기타');
};

const autoCalculateRadius = async () => {
    if (!localLocation.value.title) return;
    aiLoading.value = true;
    try {
        const res = await estimateLocationRadius({
            locationName: localLocation.value.title,
            address: "" // 주소 정보가 있다면 좋겠지만, 현재 DTO에는 없을 수 있음. 이름만으로 시도하거나 상위에서 받아와야 함.
        });
        if (res.data.success && res.data.data) {
            localLocation.value.gpsVerifyRadius = res.data.data;
            alert(`AI가 추천하는 반경은 ${res.data.data}m 입니다.`);
        } else {
            alert("AI 산출에 실패했습니다: " + (res.data.message || "알 수 없는 오류"));
        }
    } catch (error) {
        console.error("AI 산출 실패:", error);
        alert("AI 산출에 실패했습니다.");
    } finally {
        aiLoading.value = false;
    }
};

const saveLocation = async () => {
    try {
        await updateLocationRadius(localLocation.value.locationId, localLocation.value.gpsVerifyRadius);
        alert("관광지 정보가 수정되었습니다.");
        emit('refresh');
    } catch (error) {
        console.error("수정 실패:", error);
        alert("수정 실패");
    }
};

const deleteQuestItem = async (questId) => {
    if(!confirm("정말 이 퀘스트를 삭제하시겠습니까?")) return;
    try {
        await deleteQuest(questId);
        await fetchQuests();
        emit('refresh'); // 퀘스트 개수 갱신 위해
    } catch (error) {
        console.error("삭제 실패:", error);
        alert("삭제 실패");
    }
};

const addNewQuest = async () => {
    if (!newQuestTitle.value) return alert("제목을 입력하세요");
    
    // 간단한 퀘스트 객체 생성 (나머지는 기본값 등 처리)
    const questData = {
        title: newQuestTitle.value,
        questTypeId: parseInt(newQuestType.value),
        description: newQuestTitle.value + " (관리자 추가)",
        difficulty: 'NORMAL',
        rewardXp: newQuestType.value === "1" ? 50 : 150,
        rewardPoints: newQuestType.value === "1" ? 5 : 15,
        requireGpsVerify: newQuestType.value === "1"
    };

    try {
        await addQuest(localLocation.value.locationId, questData);
        alert("퀘스트가 추가되었습니다.");
        newQuestTitle.value = "";
        await fetchQuests();
        emit('refresh');
    } catch (error) {
        console.error("추가 실패:", error);
        alert("퀘스트 추가 실패");
    }
};

const close = () => {
    emit('close');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(2px);
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.modal-content h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #1e293b;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 12px;
}

.section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
}

.section h4 {
  margin-top: 0;
  margin-bottom: 12px;
  color: #334155;
  font-size: 16px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #475569;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-group input:disabled {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

.radius-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

.radius-group input {
  flex: 1;
}

.btn-ai {
  background-color: #8b5cf6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 13px;
  transition: background 0.2s ease;
  white-space: nowrap;
}

.btn-ai:hover {
  background-color: #7c3aed;
}

.btn-primary {
  background-color: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  transition: background 0.2s ease;
}

.btn-primary:hover {
  background-color: #2563eb;
}

.btn-delete {
  background-color: #ef4444;
  color: white;
  border: none;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: background 0.2s ease;
}

.btn-delete:hover {
  background-color: #dc2626;
}

.btn-add {
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

.btn-add:hover {
  background-color: #059669;
}

.btn-close {
  background-color: #64748b;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 16px;
  width: 100%;
  font-weight: 600;
  font-size: 14px;
  transition: background 0.2s ease;
}

.btn-close:hover {
  background-color: #475569;
}

.quest-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-bottom: 1px solid #e2e8f0;
  background: white;
  border-radius: 4px;
  margin-bottom: 6px;
  transition: background 0.2s ease;
}

.quest-item:hover {
  background: #f8fafc;
}

.quest-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.quest-item span {
  color: #334155;
  font-size: 14px;
}

.add-form {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.add-form input {
  flex: 1;
}

.add-form select {
  padding: 8px 12px;
  border: 1px solid #cbd5e1;
  border-radius: 4px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.add-form select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 스크롤바 스타일링 */
.modal-content::-webkit-scrollbar {
  width: 8px;
}

.modal-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
