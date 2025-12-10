<template>
  <svg 
    :width="width" 
    :height="height" 
    viewBox="0 0 180 40" 
    fill="none" 
    xmlns="http://www.w3.org/2000/svg"
    class="logo-svg"
  >
    <path d="M20 2C11.1634 2 4 9.16344 4 18C4 28.5 20 40 20 40C20 40 36 28.5 36 18C36 9.16344 28.8366 2 20 2Z" fill="url(#logo_gradient)"/>
    <path d="M20 11L22.5 16H28L23.5 19.5L25 25L20 21.5L15 25L16.5 19.5L12 16H17.5L20 11Z" fill="white"/>
    
    <text x="46" y="28" font-family="Pretendard, sans-serif" font-weight="800" font-size="22" :fill="textColor" letter-spacing="-0.5">
      MyTrip<tspan :fill="accentColor">Quest</tspan>
    </text>
    
    <defs>
      <linearGradient id="logo_gradient" x1="4" y1="2" x2="36" y2="40" gradientUnits="userSpaceOnUse">
        <stop :stop-color="accentColor"/>
        <stop offset="1" :stop-color="darkAccentColor"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  width: { type: [String, Number], default: 180 },
  height: { type: [String, Number], default: 40 },
  textColor: { type: String, default: '#1e293b' },   // 기본 검정색
  accentColor: { type: String, default: '#3b82f6' }  // 포인트 블루
});

// 그라데이션용 진한 색상 자동 생성 (간단하게 하드코딩)
const darkAccentColor = computed(() => '#2563EB'); 
</script>

<style scoped>
.logo-svg {
  display: block;
}
text {
  font-family: "Pretendard", sans-serif; /* 폰트 강제 적용 */
}
</style>