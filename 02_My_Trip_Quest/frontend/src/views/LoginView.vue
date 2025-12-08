<template>
  <div class="auth-page">
    <div class="auth-container">
      <h1 class="title">LOGIN</h1>
      
      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="email" class="form-label">EMAIL</label>
          <input type="email" id="email" v-model="email" class="form-input" required>
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">PASSWORD</label>
          <input type="password" id="password" v-model="password" class="form-input" required>
        </div>
        
        <button type="submit" class="pixel-btn">LOGIN</button>
      </form>
      
      <div class="links">
        <router-link to="/signup" class="link-text">Don't have an account? Signup</router-link>
      </div>
    </div>
     <router-link to="/main-menu" class="home-link pixel-btn">HOME</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router'; // useRouter 추가
import api from '@/api'; // api 인스턴스 추가

const authStore = useAuthStore();
const router = useRouter(); // useRouter 인스턴스화
const email = ref('');
const password = ref('');

const handleLogin = async () => {
  if (!email.value || !password.value) {
    alert('이메일과 비밀번호를 모두 입력해주세요.');
    return;
  }
  try {
    const response = await api.post('/api/v1/users/login', {
      email: email.value,
      password: password.value,
    });
    
    console.log('백엔드 응답:', response.data); // 응답 데이터 전체를 로깅

    // 백엔드 응답에서 토큰을 추출합니다. 실제 응답 구조에 따라 경로를 조정합니다.
    const token = response.data.data.token; 
    
    if (token) {
      authStore.login(token);
      router.push('/main-menu'); // 로그인 성공 후 메인 메뉴 페이지로 이동
    } else {
      alert('로그인에 실패했습니다: 토큰을 받을 수 없습니다.');
    }
  } catch (error) {
    console.error('로그인 중 오류 발생:', error);
    // 에러 응답이 있을 경우 상세 메시지를 표시
    if (error.response && error.response.data && error.response.data.message) {
      alert(`로그인 실패: ${error.response.data.message}`);
    } else {
      alert('로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.');
    }
  }
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Press+Start+2P&display=swap');

.auth-page {
  font-family: 'Press Start 2P', cursive;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #fffdf5;
  color: #2e1a47;
  background-image: 
    linear-gradient(45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(-45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(45deg, transparent 75%, #f7f5e6 75%), 
    linear-gradient(-45deg, transparent 75%, #f7f5e6 75%);
  background-size: 40px 40px;
  padding: 20px;
  box-sizing: border-box;
}

.auth-container {
  background: white;
  border: 4px solid #2e1a47;
  padding: 40px 60px;
  text-align: center;
  box-shadow: 8px 8px 0px rgba(46, 26, 71, 0.2);
  width: 100%;
  max-width: 450px;
}

.title {
  font-size: 24px;
  margin-bottom: 30px;
  letter-spacing: 2px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.form-group {
  text-align: left;
}

.form-label {
  font-size: 10px;
  display: block;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px;
  font-family: 'Press Start 2P', cursive;
  font-size: 12px;
  border: 3px solid #2e1a47;
  background-color: #f7f5e6;
  color: #2e1a47;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  background-color: white;
  box-shadow: 0 0 0 3px #3b82f6;
}

.pixel-btn {
  background: #3b82f6;
  color: white;
  border: 3px solid #2e1a47;
  padding: 14px 20px;
  font-family: inherit;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 4px 4px 0px #2e1a47;
  transition: all 0.1s;
  text-decoration: none;
  margin-top: 10px;
}

.pixel-btn:active {
  transform: translate(4px, 4px);
  box-shadow: none;
}

.links {
  margin-top: 30px;
}

.link-text {
  color: #2e1a47;
  font-size: 10px;
  text-decoration: none;
}

.link-text:hover {
  text-decoration: underline;
}
.home-link {
  margin-top: 20px;
  background-color: #ff6b6b;
}

@media (max-width: 600px) {
  .auth-container {
    padding: 25px 15px;
  }

  .title {
    font-size: 16px;
    margin-bottom: 25px;
  }

  .auth-form {
    gap: 15px;
  }

  .pixel-btn {
    padding: 10px 14px;
    font-size: 10px;
  }

  .form-input {
    padding: 10px;
    font-size: 10px;
  }

  .form-label {
    margin-bottom: 5px;
  }
}
</style>