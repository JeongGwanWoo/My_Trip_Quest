<template>
  <BaseModal :show="true" @close="$emit('close')">
    <div class="modal-inner">
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
            
            <div v-if="photoQuestError" class="error-message">
              <i class="fa-solid fa-circle-exclamation"></i> {{ photoQuestError }}
            </div>

            <button 
              v-if="showGeolocationButton" 
              @click="handleGetLocationAndUpload" 
              :disabled="isCompleting" 
              class="btn-secondary"
            >
              <i class="fa-solid fa-location-dot"></i> 내 현재 위치로 인증하기
            </button>
          </div>
        </div>

        <div class="actions-section">
          <button @click="handleForfeitQuest" class="btn-danger">
            <i class="fa-solid fa-flag"></i> 퀘스트 포기하기
          </button>
        </div>
      </div>
    </div>

    <!-- Confirmation Modals -->
    <BaseModal :show="showCompleteArrivalModal" @close="showCompleteArrivalModal = false">
      <div class="modal-body">
        <h3 class="modal-title">도착 인증</h3>
        <p class="modal-text">현재 위치로 도착 인증을 진행하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showCompleteArrivalModal = false">취소</button>
          <button class="btn-confirm" @click="executeCompleteArrival">인증하기</button>
        </div>
      </div>
    </BaseModal>

    <BaseModal :show="showCompletePhotoModal" @close="showCompletePhotoModal = false">
      <div class="modal-body">
        <h3 class="modal-title">사진 제출</h3>
        <p class="modal-text">이 사진을 제출하여 퀘스트를 완료하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showCompletePhotoModal = false">취소</button>
          <button class="btn-confirm" @click="executeCompletePhoto">제출하기</button>
        </div>
      </div>
    </BaseModal>

    <BaseModal :show="showForfeitModal" @close="showForfeitModal = false">
      <div class="modal-body">
        <h3 class="modal-title">퀘스트 포기</h3>
        <p class="modal-text">정말로 이 퀘스트를 포기하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showForfeitModal = false">취소</button>
          <button class="btn-confirm-delete" @click="executeForfeitQuest">포기</button>
        </div>
      </div>
    </BaseModal>
  </BaseModal>
</template>

<script setup>
import { ref } from 'vue';
import { completeArrivalQuest, completePhotoQuest, forfeitQuest } from '@/api/quest';
import { useToast } from '@/utils/toast';
import BaseModal from '@/components/ui/BaseModal.vue';
import { compressImage } from '@/utils/imageCompression';

const props = defineProps({
  quest: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close', 'quest-updated']);
const { showToast } = useToast();

const isCompleting = ref(false);
const completionStatus = ref('');
const selectedFile = ref(null);
const fileInputRef = ref(null);
const filePreviewUrl = ref('');
const photoQuestError = ref(null);
const showGeolocationButton = ref(false);

// Modal State
const showCompleteArrivalModal = ref(false);
const showCompletePhotoModal = ref(false);
const showForfeitModal = ref(false);

const getQuestTypeName = (typeId) => {
  if (typeId === 1) return '도착 퀘스트';
  if (typeId === 2) return '사진 퀘스트';
  return '일반 퀘스트';
};

const handleCompleteArrival = () => {
  showCompleteArrivalModal.value = true;
};

const executeCompleteArrival = () => {
  showCompleteArrivalModal.value = false;
  isCompleting.value = true;
  completionStatus.value = 'GPS 정보를 가져오는 중...';

  if (!navigator.geolocation) {
    completionStatus.value = '오류: Geolocation이 지원되지 않는 브라우저입니다.';
    showToast(completionStatus.value, 'error');
    isCompleting.value = false;
    return;
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      completionStatus.value = '위치 확인 완료! 퀘스트 완료 요청 중...';
      try {
        await completeArrivalQuest(props.quest.questId, position.coords.latitude, position.coords.longitude);
        showToast('퀘스트를 성공적으로 완료했습니다!', 'success');
        emit('quest-updated');
        emit('close');
      } catch (error) {
        const message = error.response?.data?.message || '퀘스트 완료에 실패했습니다.';
        showToast(message, 'error');
        completionStatus.value = `오류: ${message}`;
      } finally {
        isCompleting.value = false;
      }
    },
    (error) => {
      completionStatus.value = `GPS 오류: ${error.message}`;
      showToast(completionStatus.value, 'error');
      isCompleting.value = false;
    }
  );
};

const triggerFileInput = () => {
  fileInputRef.value.click();
};

const handleFileSelect = async (event) => {
  const file = event.target.files[0];
  if (file) {
    try {
      // Compress immediately upon selection
      const compressedFile = await compressImage(file);
      selectedFile.value = compressedFile;
      filePreviewUrl.value = URL.createObjectURL(compressedFile);
      photoQuestError.value = null;
      showGeolocationButton.value = false;
    } catch (error) {
      console.error("Compression check failed, falling back to original:", error);
      selectedFile.value = file;
      filePreviewUrl.value = URL.createObjectURL(file);
    }
  }
};

const handleCompletePhoto = () => {
  if (!selectedFile.value) return;
  showCompletePhotoModal.value = true;
};

const executeCompletePhoto = async () => {
  if (!selectedFile.value) return;

  showCompletePhotoModal.value = false;
  isCompleting.value = true;
  photoQuestError.value = null; // Reset error
  
  try {
    await completePhotoQuest(props.quest.questId, selectedFile.value);
    showToast('사진 퀘스트를 성공적으로 완료했습니다!', 'success');
    emit('quest-updated');
    emit('close');
  } catch (error) {
    console.error('Photo upload error:', error);
    if (error.response?.data?.code === 'PHOTO_METADATA_MISSING') {
      photoQuestError.value = "사진에 위치 정보가 없습니다. 현재 위치로 인증해주세요.";
      showGeolocationButton.value = true;
    } else {
      const message = error.response?.data?.message || '사진 퀘스트 완료에 실패했습니다.';
      showToast(message, 'error');
      photoQuestError.value = message;
    }
  } finally {
    isCompleting.value = false;
  }
};

const handleGetLocationAndUpload = async () => {
  if (!navigator.geolocation) {
    photoQuestError.value = "이 브라우저에서는 위치 정보 서비스를 사용할 수 없습니다.";
    return;
  }
  
  isCompleting.value = true;
  photoQuestError.value = null;

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const { latitude, longitude } = position.coords;
      
      // 1. Validate Distance (Frontend Check)
      if (props.quest.latitude && props.quest.longitude) {
        const dist = calculateDistance(
          latitude, 
          longitude, 
          props.quest.latitude, 
          props.quest.longitude
        );
        const maxDist = props.quest.gpsVerifyRadius || 50; // Default 50m

        if (dist > maxDist) {
          photoQuestError.value = `위치가 너무 멉니다. (현재 거리: ${Math.round(dist)}m, 허용 반경: ${maxDist}m)`;
          showToast(`위치가 너무 멉니다. (거리: ${Math.round(dist)}m)`, 'error');
          isCompleting.value = false;
          return;
        }
      }

      try {
        await completePhotoQuest(props.quest.questId, selectedFile.value, latitude.toString(), longitude.toString());
        showToast('사진 퀘스트를 성공적으로 완료했습니다!', 'success');
        emit('quest-updated');
        emit('close');
      } catch (error) {
        const message = error.response?.data?.message || '인증에 실패했습니다.';
        showToast(message, 'error');
        photoQuestError.value = message;
      } finally {
        isCompleting.value = false;
      }
    },
    (error) => {
      console.error("Geolocation error:", error);
      photoQuestError.value = `위치 정보를 가져오는데 실패했습니다: ${error.message}`;
      isCompleting.value = false;
    }
  );
};

const calculateDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371e3; // metres
  const rad = Math.PI / 180;
  const φ1 = lat1 * rad;
  const φ2 = lat2 * rad;
  const Δφ = (lat2 - lat1) * rad;
  const Δλ = (lon2 - lon1) * rad;

  const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
            Math.cos(φ1) * Math.cos(φ2) *
            Math.sin(Δλ/2) * Math.sin(Δλ/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

  return R * c;
};

const handleForfeitQuest = () => {
  showForfeitModal.value = true;
};

const executeForfeitQuest = async () => {
  try {
    await forfeitQuest(props.quest.questId);
    showToast('퀘스트를 포기했습니다.', 'info');
    emit('quest-updated');
    emit('close');
  } catch (error) {
    showToast(`퀘스트 포기에 실패했습니다: ${error.response?.data?.message || error.message}`, 'error');
  } finally {
    showForfeitModal.value = false;
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

/* Scoped styles for nested modals */
.modal-body {
  padding: 16px 8px;
  text-align: center;
}
.modal-title {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 12px;
}
.modal-text {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 32px;
  line-height: 1.6;
}
.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.modal-actions button {
  flex: 1;
  border: none;
  border-radius: 12px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-confirm {
  background: #2563eb;
  color: white;
}
.btn-confirm:hover {
  background: #1d4ed8;
}
.btn-cancel {
  background: #e2e8f0;
  color: #475569;
}
.btn-cancel:hover {
  background: #cbd5e1;
}
.btn-confirm-delete {
  background: #ef4444;
  color: white;
}
.btn-confirm-delete:hover {
  background: #dc2626;
}

.actions-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

.btn-danger {
  width: 100%;
  background: #fee2e2;
  color: #ef4444;
  border: 1px solid #fecaca;
  padding: 14px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-danger:hover {
  background: #fecaca;
  color: #b91c1c;
}

.error-message {
  margin-top: 12px;
  color: #ef4444;
  font-size: 14px;
  background-color: #fef2f2;
  padding: 10px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-secondary {
  width: 100%;
  margin-top: 12px;
  background: #3b82f6; /* Blue to match primary action */
  color: white;
  border: none;
  padding: 14px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.btn-secondary:hover:not(:disabled) {
  background: #2563eb;
}
.btn-secondary:disabled {
  background-color: #94a3b8;
  cursor: not-allowed;
}
</style>
