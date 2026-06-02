<template>
  <div class="toast-container">
    <TransitionGroup name="toast-list">
      <Toast
        v-for="toast in toasts"
        :key="toast.id"
        :id="toast.id"
        :message="toast.message"
        :type="toast.type"
        :duration="toast.duration"
        @after-leave="(id) => emit('remove-toast', id)"
      />
    </TransitionGroup>
  </div>
</template>

<script setup>
import Toast from './Toast.vue';

defineProps({
  toasts: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['remove-toast']);
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 30px; /* 상단 여백 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column; /* 위에서 아래로 쌓임 */
  align-items: center;
  gap: 12px;
  pointer-events: none; /* 컨테이너 자체는 클릭 무시 */
}

/* 리스트 애니메이션 */
.toast-list-move,
.toast-list-enter-active,
.toast-list-leave-active {
  transition: all 0.4s ease;
}

.toast-list-enter-from,
.toast-list-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

/* 나가는 요소가 레이아웃의 흐름에서 빠지게 하여 부드럽게 이동 */
.toast-list-leave-active {
  position: absolute;
}
</style>