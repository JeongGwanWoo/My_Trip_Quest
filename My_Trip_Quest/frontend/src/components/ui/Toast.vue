<template>
  <Transition name="toast-pop">
    <div :class="['toast-item', type]" v-if="visible">
      <div class="toast-icon">
        <i v-if="type === 'success'" class="fa-solid fa-circle-check"></i>
        <i v-else-if="type === 'error'" class="fa-solid fa-circle-xmark"></i>
        <i v-else-if="type === 'warning'" class="fa-solid fa-triangle-exclamation"></i>
        <i v-else class="fa-solid fa-circle-info"></i>
      </div>
      <div class="toast-content">
        <p class="toast-message">{{ message }}</p>
      </div>
      <button class="toast-close" @click="visible = false">
        <i class="fa-solid fa-xmark"></i>
      </button>
    </div>
  </Transition>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';

const props = defineProps({
  id: { type: Number, required: true },
  message: { type: String, required: true },
  type: { type: String, default: 'info' },
  duration: { type: Number, default: 3000 },
});

const emit = defineEmits(['after-leave']);
const visible = ref(false);

onMounted(() => {
  visible.value = true;
  if (props.duration > 0) {
    setTimeout(() => {
      visible.value = false;
    }, props.duration);
  }
});

watch(visible, (newValue) => {
  if (!newValue) {
    // 애니메이션이 끝난 후 제거 (0.3s)
    setTimeout(() => {
      emit('after-leave', props.id);
    }, 300);
  }
});
</script>

<style scoped>
.toast-item {
  display: flex;
  align-items: center;
  min-width: 340px;
  max-width: 480px;
  padding: 14px 18px;
  background-color: #ffffff;
  /* 상단 배치에 어울리는 부드러운 하단 그림자 */
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08); 
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  pointer-events: auto;
  position: relative;
  overflow: hidden;
}

/* 왼쪽 포인트 컬러 바 - 위아래 여백을 주어 더 세련되게 수정 */
.toast-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10px;
  bottom: 10px;
  width: 4px;
  border-radius: 0 4px 4px 0;
}

.toast-icon {
  font-size: 18px;
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.toast-content {
  flex: 1;
  text-align: left;
}

.toast-message {
  margin: 0;
  font-size: 14px;
  font-weight: 600; /* Pretendard 폰트에서 깔끔하게 보임 */
  color: #334155;
  line-height: 1.5;
}

.toast-close {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  font-size: 14px;
  margin-left: 8px;
  display: flex;
  align-items: center;
  transition: color 0.2s;
}

.toast-close:hover {
  color: #64748b;
}

/* 타입별 테마 컬러 */
.toast-item.success::before { background-color: #10b981; }
.toast-item.success .toast-icon { color: #10b981; }

.toast-item.error::before { background-color: #ef4444; }
.toast-item.error .toast-icon { color: #ef4444; }

.toast-item.warning::before { background-color: #f59e0b; }
.toast-item.warning .toast-icon { color: #f59e0b; }

.toast-item.info::before { background-color: #3b82f6; }
.toast-item.info .toast-icon { color: #3b82f6; }

/* 상단 배치 전용 애니메이션 (위에서 아래로) */
.toast-pop-enter-active {
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.15);
}
.toast-pop-leave-active {
  transition: all 0.3s ease;
}
.toast-pop-enter-from {
  opacity: 0;
  transform: translateY(-30px) scale(0.9); /* 위에서 아래로 슬라이드 */
}
.toast-pop-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
</style>