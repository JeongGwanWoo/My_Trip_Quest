import { ref, onMounted, onUnmounted, readonly } from 'vue';

export function useBreakpoints() {
  const isMobile = ref(false);

  const checkScreenSize = () => {
    isMobile.value = window.innerWidth <= 640;
  };

  onMounted(() => {
    checkScreenSize();
    window.addEventListener('resize', checkScreenSize);
  });

  onUnmounted(() => {
    window.removeEventListener('resize', checkScreenSize);
  });

  return {
    isMobile: readonly(isMobile),
  };
}
