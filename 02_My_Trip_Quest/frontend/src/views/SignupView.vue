<template>
  <div class="auth-page">
    <div class="auth-container">
      <h1 class="title">CREATE ACCOUNT</h1>
      
      <form @submit.prevent="handleSignup" class="auth-form">
        <div class="form-group">
          <label for="nickname" class="form-label">NICKNAME</label>
          <input type="text" id="nickname" v-model="nickname" class="form-input" required>
        </div>

        <div class="form-group">
          <label for="email" class="form-label">EMAIL</label>
          <input type="email" id="email" v-model="email" class="form-input" required>
        </div>
        
        <div class="form-group">
          <label for="password" class="form-label">PASSWORD</label>
          <input type="password" id="password" v-model="password" class="form-input" required>
        </div>

        <div class="form-group">
          <label for="confirmPassword" class="form-label">CONFIRM PASSWORD</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" class="form-input" required>
        </div>
        
        <button type="submit" class="pixel-btn">SIGNUP</button>
      </form>
      
      <div class="links">
        <router-link to="/login" class="link-text">Already have an account? Login</router-link>
      </div>
    </div>
    <router-link to="/main-menu" class="home-link pixel-btn">HOME</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api'; // api 인스턴스 추가

const router = useRouter();
const nickname = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

const handleSignup = async () => {
  if (!nickname.value || !email.value || !password.value || !confirmPassword.value) {
    alert('모든 필드를 입력해주세요.');
    return;
  }
  if (password.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  
  try {
    await api.post('/api/v1/users/register', {
      nickname: nickname.value,
      email: email.value,
      password: password.value,
    });
    
    alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
    router.push('/login');

  } catch (error) {
    console.error('회원가입 중 오류 발생:', error);
    if (error.response && error.response.data && error.response.data.message) {
      alert(`회원가입 실패: ${error.response.data.message}`);
    } else {
      alert('회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.');
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
  padding: 20px;
  box-sizing: border-box;
  background-image: 
    linear-gradient(45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(-45deg, #f7f5e6 25%, transparent 25%), 
    linear-gradient(45deg, transparent 75%, #f7f5e6 75%), 
    linear-gradient(-45deg, transparent 75%, #f7f5e6 75%);
  background-size: 40px 40px;
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
