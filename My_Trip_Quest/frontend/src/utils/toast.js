import { inject } from 'vue';

export function useToast() {
  const showToast = inject('showToast');
  if (!showToast) {
    console.error('Toast service not provided. Make sure ToastContainer is mounted in your App.vue.');
    return () => {}; // Return a no-op function to prevent errors
  }
  return { showToast };
}
