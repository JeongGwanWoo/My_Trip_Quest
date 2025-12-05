<template>
  <div class="bottom-sheet" :style="sheetStyle">
    <div class="handle" 
         @mousedown="startDrag"
         @touchstart="startDrag">
      <div class="handle-bar"></div>
    </div>
    <div class="content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:isOpen']);

const sheetHeight = ref(0); // 전체 시트 높이
const sheetY = ref(0); // 현재 시트의 Y 위치 (translateY)
const isDragging = ref(false);
const startDragY = ref(0);

// 시트가 열렸을 때의 최대 높이 (예: 화면의 80%)
const openHeight = window.innerHeight * 0.8;
// 시트가 닫혔을 때 (살짝 보일 때)의 높이
const peekHeight = 100;

const sheetStyle = computed(() => ({
  height: `${openHeight}px`,
  transform: `translateY(${sheetY.value}px)`,
  transition: isDragging.value ? 'none' : 'transform 0.3s ease-out',
}));

const updateSheetY = (y) => {
  // 드래그 범위를 제한 (너무 위나 아래로 가지 않도록)
  sheetY.value = Math.max(0, Math.min(y, openHeight - peekHeight));
};

const snapToPosition = () => {
  const shouldOpen = sheetY.value < (openHeight - peekHeight) / 2;
  if (shouldOpen) {
    sheetY.value = 0; // Fully open
    emit('update:isOpen', true);
  } else {
    sheetY.value = openHeight - peekHeight; // Peek state
    emit('update:isOpen', false);
  }
};

const startDrag = (event) => {
  isDragging.value = true;
  startDragY.value = 'touches' in event ? event.touches[0].clientY : event.clientY;
  
  // 드래그 중 텍스트 선택 방지
  event.preventDefault();

  window.addEventListener('mousemove', onDrag);
  window.addEventListener('touchmove', onDrag);
  window.addEventListener('mouseup', endDrag);
  window.addEventListener('touchend', endDrag);
};

const onDrag = (event) => {
  if (!isDragging.value) return;
  
  const currentY = 'touches' in event ? event.touches[0].clientY : event.clientY;
  const deltaY = currentY - startDragY.value;
  
  const newSheetY = sheetY.value + deltaY;
  updateSheetY(newSheetY);
  
  startDragY.value = currentY;
};

const endDrag = () => {
  if (!isDragging.value) return;
  isDragging.value = false;
  
  snapToPosition();

  window.removeEventListener('mousemove', onDrag);
  window.removeEventListener('touchmove', onDrag);
  window.removeEventListener('mouseup', endDrag);
  window.removeEventListener('touchend', endDrag);
};

// isOpen prop을 감시하여 부모 컴포넌트로부터의 변경에 대응
watch(() => props.isOpen, (newValue) => {
  if (newValue) {
    sheetY.value = 0; // Open
  } else {
    sheetY.value = openHeight - peekHeight; // Close
  }
});

// 컴포넌트 마운트 시 초기 위치 설정
onMounted(() => {
  sheetY.value = props.isOpen ? 0 : openHeight - peekHeight;
  // 초기 높이 계산 (여기서는 openHeight로 고정)
  sheetHeight.value = openHeight;
});

// 컴포넌트 언마운트 시 이벤트 리스너 정리
onBeforeUnmount(() => {
  window.removeEventListener('mousemove', onDrag);
  window.removeEventListener('touchmove', onDrag);
  window.removeEventListener('mouseup', endDrag);
  window.removeEventListener('touchend', endDrag);
});
</script>

<style scoped>
.bottom-sheet {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -5px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.handle {
  padding: 15px;
  cursor: grab;
  display: flex;
  justify-content: center;
  align-items: center;
}

.handle-bar {
  width: 40px;
  height: 5px;
  background-color: #d1d5db; /* gray-300 */
  border-radius: 2.5px;
}

.content {
  overflow-y: auto;
  flex-grow: 1;
  padding: 0 20px 20px 20px;
}
</style>
