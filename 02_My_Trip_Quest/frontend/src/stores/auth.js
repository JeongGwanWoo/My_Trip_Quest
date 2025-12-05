import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter()
  
  // State
  const token = ref(localStorage.getItem('user_token') || null)
  
  // Getters
  const isLoggedIn = computed(() => !!token.value)

  // Actions
  function setToken(newToken) {
    localStorage.setItem('user_token', newToken)
    token.value = newToken
  }

  function login(newToken) {
    setToken(newToken)
    // On successful login, navigate to the main menu
    router.push('/main-menu')
  }

  function logout() {
    localStorage.removeItem('user_token')
    token.value = null
    // On logout, just reload the page. All components will re-render 
    // and check for the now-absent token, updating the UI correctly.
    window.location.reload();
  }

  return {
    token,
    isLoggedIn,
    login,
    logout,
  }
})
