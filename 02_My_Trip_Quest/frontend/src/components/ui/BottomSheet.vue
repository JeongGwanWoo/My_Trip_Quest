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
  maxOpenHeight: { // 새 prop 추가 (예: '60%', '300px' 등)
    type: [String, Number],
    default: '80vh', // 기본값은 뷰포트 높이의 80%
  },
  peekHeight: { // peekHeight도 prop으로 설정
    type: Number,
    default: 100, // 기본값 100px
  }
});

const emit = defineEmits(['update:isOpen']);

const sheetElement = ref(null); // 바텀 시트 DOM 요소를 참조할 ref
const contentElement = ref(null); // 내용물 DOM 요소를 참조할 ref

const calculatedOpenHeight = ref(0); // 계산된 전체 시트 높이 (px)
const sheetY = ref(0); // 현재 시트의 Y 위치 (translateY)
const isDragging = ref(false);
const startDragY = ref(0);

// maxOpenHeight prop을 기반으로 실제 열린 높이를 계산
const updateCalculatedOpenHeight = () => {
  if (typeof props.maxOpenHeight === 'number') {
    calculatedOpenHeight.value = props.maxOpenHeight;
  } else if (typeof props.maxOpenHeight === 'string') {
    if (props.maxOpenHeight.endsWith('vh')) {
      calculatedOpenHeight.value = window.innerHeight * (parseFloat(props.maxOpenHeight) / 100);
    } else if (props.maxOpenHeight.endsWith('%')) {
      // 부모 요소 대비 % 계산을 위해 sheetElement가 마운트 되어야 함
      if (sheetElement.value && sheetElement.value.parentElement) {
        calculatedOpenHeight.value = sheetElement.value.parentElement.clientHeight * (parseFloat(props.maxOpenHeight) / 100);
      } else {
        // Fallback or warning if parent is not available yet
        calculatedOpenHeight.value = window.innerHeight * 0.8; // Fallback to 80vh
      }
    } else { // 'px' 또는 단위 없는 숫자
      calculatedOpenHeight.value = parseFloat(props.maxOpenHeight);
    }
  }
  // 최소 높이 제한 (peekHeight보다 작아지지 않게)
  calculatedOpenHeight.value = Math.max(calculatedOpenHeight.value, props.peekHeight + 50); // 최소한 peekHeight + @ px
};


const sheetStyle = computed(() => ({
  height: `${calculatedOpenHeight.value}px`, // 계산된 높이 사용
  transform: `translateY(${sheetY.value}px)`,
  transition: isDragging.value ? 'none' : 'transform 0.3s ease-out',
}));

const updateSheetY = (y) => {
  // 드래그 범위를 제한 (너무 위나 아래로 가지 않도록)
  // 0보다 작으면 안되고, 닫힌 상태 (openHeight - peekHeight)를 넘어서도 안됨
  sheetY.value = Math.max(0, Math.min(y, calculatedOpenHeight.value - props.peekHeight));
};

const snapToPosition = () => {
  // 열린 상태와 닫힌 상태의 중간 지점
  const middlePoint = (calculatedOpenHeight.value - props.peekHeight) / 2;
  const shouldOpen = sheetY.value < middlePoint;
  
  if (shouldOpen) {
    sheetY.value = 0; // Fully open
    emit('update:isOpen', true);
  } else {
    sheetY.value = calculatedOpenHeight.value - props.peekHeight; // Peek state
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
    sheetY.value = calculatedOpenHeight.value - props.peekHeight; // Close
  }
});

// maxOpenHeight prop 변경 시 높이 재계산
watch(() => props.maxOpenHeight, () => {
  updateCalculatedOpenHeight();
  // 높이 변경 후 현재 상태에 맞게 sheetY 조정
  if (props.isOpen) {
    sheetY.value = 0;
  } else {
    sheetY.value = calculatedOpenHeight.value - props.peekHeight;
  }
});


// 컴포넌트 마운트 시 초기 위치 설정
onMounted(() => {
  updateCalculatedOpenHeight(); // 초기 높이 계산
  sheetY.value = props.isOpen ? 0 : calculatedOpenHeight.value - props.peekHeight;
  
  // 창 크기 변경 시 높이 재계산 (vh 또는 % 단위 사용 시 유용)
  window.addEventListener('resize', updateCalculatedOpenHeight);
});

// 컴포넌트 언마운트 시 이벤트 리스너 정리
onBeforeUnmount(() => {
  window.removeEventListener('mousemove', onDrag);
  window.removeEventListener('touchmove', onDrag);
  window.removeEventListener('mouseup', endDrag);
  window.removeEventListener('touchend', endDrag);
  window.removeEventListener('resize', updateCalculatedOpenHeight);
});
</script>

<style scoped>
.bottom-sheet {
  position: absolute; /* fixed 대신 absolute 사용 */
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
  /* transition은 script에서 제어 */
}

.handle {
  padding: 15px;
  cursor: grab;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0; /* 핸들이 내용물에 의해 줄어들지 않도록 */
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
