<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="close-button" @click="$emit('close')">&times;</button>
      
      <div v-if="quest">
        <header class="modal-header">
          <span class="quest-type-badge">{{ getQuestTypeName(quest.questTypeId) }}</span>
          <h2 class="quest-title">{{ quest.title }}</h2>
          <p class="quest-description">{{ quest.description }}</p>
        </header>

        <div class="rewards-section">
          <div class="reward-item">
            <i class="fa-solid fa-star"></i>
            <span>{{ quest.rewardXp }} XP</span>
          </div>
          <div class="reward-item">
            <i class="fa-solid fa-coins"></i>
            <span>{{ quest.rewardPoints }} P</span>
          </div>
        </div>

        <div class="completion-section">
          <h3 class="section-title">퀘스트 완료하기</h3>

          <!-- Arrival Quest (Type 1) -->
          <div v-if="quest.questTypeId === 1" class="arrival-quest">
            <p class="completion-guide">퀘스트 장소에 도착했음을 인증해주세요.</p>
            <button @click="handleCompleteArrival" :disabled="isCompleting" class="btn-complete">
              <i v-if="!isCompleting" class="fa-solid fa-location-crosshairs"></i>
              <i v-else class="fa-solid fa-spinner fa-spin"></i>
              {{ isCompleting ? '인증 중...' : '현재 위치로 도착 인증' }}
            </button>
            <p v-if="completionStatus" class="completion-status">{{ completionStatus }}</p>
          </div>

          <!-- Photo Quest (Type 2) -->
          <div v-if="quest.questTypeId === 2" class="photo-quest">
            <p class="completion-guide">퀘스트를 증명할 사진을 업로드해주세요.</p>
            <input type="file" @change="handleFileSelect" accept="image/*" class="file-input" ref="fileInputRef"/>
            <button @click="triggerFileInput" class="btn-upload">
              <i class="fa-solid fa-camera"></i> 사진 선택하기
            </button>
            <div v-if="selectedFile" class="file-preview">
              <p>선택된 파일: {{ selectedFile.name }}</p>
              <img :src="filePreviewUrl" alt="Preview" class="image-preview"/>
            </div>
            <button @click="handleCompletePhoto" :disabled="!selectedFile || isCompleting" class="btn-complete">
              <i v-if="!isCompleting" class="fa-solid fa-upload"></i>
              <i v-else class="fa-solid fa-spinner fa-spin"></i>
              {{ isCompleting ? '업로드 중...' : '사진으로 완료' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { completeArrivalQuest, completePhotoQuest } from '@/api/quest';

const props = defineProps({
  quest: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close']);

const isCompleting = ref(false);
const completionStatus = ref('');
const selectedFile = ref(null);
const fileInputRef = ref(null);
const filePreviewUrl = ref('');

const getQuestTypeName = (typeId) => {
  if (typeId === 1) return '도착 퀘스트';
  if (typeId === 2) return '사진 퀘스트';
  return '일반 퀘스트';
};

const handleCompleteArrival = () => {
  isCompleting.value = true;
  completionStatus.value = 'GPS 정보를 가져오는 중...';

  if (!navigator.geolocation) {
    completionStatus.value = '오류: Geolocation이 지원되지 않는 브라우저입니다.';
    isCompleting.value = false;
    return;
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      completionStatus.value = '위치 확인 완료! 퀘스트 완료 요청 중...';
      try {
        const response = await completeArrivalQuest(props.quest.questId, position.coords.latitude, position.coords.longitude);
        if (response.data.success) {
          alert('퀘스트를 성공적으로 완료했습니다!');
          emit('close');
        }
      } catch (error) {
        const message = error.response?.data?.message || '퀘스트 완료에 실패했습니다.';
        alert(message);
        completionStatus.value = `오류: ${message}`;
      } finally {
        isCompleting.value = false;
      }
    },
    (error) => {
      completionStatus.value = `GPS 오류: ${error.message}`;
      isCompleting.value = false;
    }
  );
};

const triggerFileInput = () => {
  fileInputRef.value.click();
};

const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
    filePreviewUrl.value = URL.createObjectURL(file);
  }
};

const handleCompletePhoto = async () => {
  if (!selectedFile.value) return;

  isCompleting.value = true;
  try {
    // Note: completePhotoQuest API function needs to be created
    const response = await completePhotoQuest(props.quest.questId, selectedFile.value);
    if (response.data.success) {
      alert('사진 퀘스트를 성공적으로 완료했습니다!');
      emit('close');
    }
  } catch (error) {
    const message = error.response?.data?.message || '사진 퀘스트 완료에 실패했습니다.';
    alert(message);
  } finally {
    isCompleting.value = false;
  }
};

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 32px;
  border-radius: 24px;
  width: 90%;
  max-width: 500px;
  position: relative;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  animation: slide-up 0.3s ease-out;
}

@keyframes slide-up {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.close-button {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 28px;
  line-height: 1;
  color: #94a3b8;
  cursor: pointer;
}

.modal-header {
  text-align: center;
  margin-bottom: 24px;
}

.quest-type-badge {
  display: inline-block;
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 16px;
}

.quest-title {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.quest-description {
  font-size: 16px;
  color: #64748b;
  line-height: 1.6;
}

.rewards-section {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding: 16px;
  background-color: #f8fafc;
  border-radius: 16px;
  margin-bottom: 24px;
}
.reward-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #475569;
}
.reward-item i {
  color: #fbbf24;
}

.completion-section {
  border-top: 1px solid #f1f5f9;
  padding-top: 24px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 16px 0;
  color: #334155;
}
.completion-guide {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
}

.btn-complete, .btn-upload {
  width: 100%;
  padding: 14px;
  font-size: 16px;
  font-weight: 700;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.btn-complete {
  background-color: #3b82f6;
  color: white;
}
.btn-complete:hover:not(:disabled) {
  background-color: #2563eb;
}
.btn-complete:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}

.completion-status {
  margin-top: 12px;
  font-size: 13px;
  color: #475569;
  text-align: center;
}

.file-input {
  display: none;
}
.btn-upload {
  background-color: #f1f5f9;
  color: #334155;
  margin-bottom: 16px;
}
.file-preview {
  margin-top: 16px;
  text-align: center;
}
.image-preview {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
  margin-top: 8px;
}
</style>
