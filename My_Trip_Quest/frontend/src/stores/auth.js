import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { jwtDecode } from 'jwt-decode'
import { getProfile } from '@/api/user' // 제공해주신 파일의 함수명 사용

export const useAuthStore = defineStore('auth', () => {
  // --- State ---
  const token = ref(localStorage.getItem('user_token') || null)
  const user = ref(null) // 유저 정보를 저장할 반응형 변수
  const registrationToken = ref(sessionStorage.getItem('registration_token') || null)

  // --- Getters ---
  const isLoggedIn = computed(() => !!token.value)
  const userInfo = computed(() => user.value) // 컴포넌트에서 유저 정보를 쓸 때 사용

  const isAdmin = computed(() => {
    if (!token.value) {
      return false
    }
    try {
      const decodedToken = jwtDecode(token.value);
      // 백엔드(JwtTokenProvider)에서 "role"이라는 이름으로 클레임을 생성했으므로 "role"을 사용합니다.
      return decodedToken.role === 'ADMIN';
    } catch (error) {
      console.error("Invalid token:", error);
      return false;
    }
  });

  // --- Actions ---

  // [중요] 유저 정보를 서버에서 가져와 State에 저장하는 함수
  async function fetchUserProfile() {
    if (!token.value) return
    try {
      // user.js의 getProfile 호출
      // 백엔드 ApiResponse 구조가 { success: true, data: { ... } } 라면 response.data를 씁니다.
      const response = await getProfile()
      user.value = response.data // 서버 응답 데이터에서 실제 유저 객체를 추출하여 할당
    } catch (error) {
      console.error('프로필 로드 실패:', error)
      logout() 
    }
  }

  // 로그인/가입 완료 시 호출되는 함수
  async function login(newToken) {
    localStorage.setItem('user_token', newToken)
    token.value = newToken
    
    // ⭐ 새로고침을 대신해주는 핵심 로직: 즉시 프로필 정보를 가져옴
    await fetchUserProfile()
  }

  function setRegistrationToken(newToken) {
    sessionStorage.setItem('registration_token', newToken)
    registrationToken.value = newToken
  }

  function clearRegistrationToken() {
    sessionStorage.removeItem('registration_token')
    registrationToken.value = null
  }

  function logout() {
    localStorage.removeItem('user_token')
    token.value = null
    user.value = null
    window.location.reload()
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    userInfo,
    registrationToken,
    login,
    logout,
    fetchUserProfile,
    setRegistrationToken,
    clearRegistrationToken,
  }
})