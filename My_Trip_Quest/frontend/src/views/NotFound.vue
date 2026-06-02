<template>
  <div class="not-found-container">
    <div class="content">
      <div class="icon-area">
        <i class="fa-solid fa-compass"></i>
      </div>
      <h1>404</h1>
      <h2>페이지를 찾을 수 없습니다</h2>
      <p>
        요청하신 페이지를 찾을 수 없거나, 더 이상 제공되지 않는 페이지입니다.
      </p>
      
      <button @click="goHome" class="home-btn">
        메인으로 돌아가기
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const goHome = () => {
  router.push('/'); // 메인 페이지로 이동
};
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh; /* 화면의 80% 높이를 차지해서 중앙에 오게 함 */
  text-align: center;
  color: #333;
}

.icon-area {
  font-size: 5rem;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite; /* 둥둥 떠다니는 애니메이션 효과 */
}

h1 {
  font-size: 6rem;
  margin: 0;
  color: #ff6b6b; /* 경고 느낌의 붉은색 포인트 */
  font-weight: bold;
}

h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

p {
  color: #666;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.home-btn {
  padding: 12px 24px;
  font-size: 1rem;
  background-color: #3b82f6; /* 테마 파란색 */
  color: white;
  border: none;
  border-radius: 30px;
  cursor: pointer;
  transition: transform 0.2s, background-color 0.2s;
}

.home-btn:hover {
  background-color: #2563eb; /* 더 진한 파란색 */
  transform: translateY(-2px);
}

/* 둥둥 떠다니는 귀여운 효과 */
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

/* --- 반응형 미디어 쿼리 --- */
@media (max-width: 600px) {
  .not-found-container {
    height: 100vh; /* 모바일에서 전체 높이 사용 */
    padding: 20px; /* 좌우 여백 추가 */
  }

  .icon-area {
    font-size: 3.5rem; /* 아이콘 크기 축소 */
    margin-bottom: 15px;
  }

  h1 {
    font-size: 4rem; /* H1 크기 축소 */
  }

  h2 {
    font-size: 1.2rem; /* H2 크기 축소 */
  }

  p {
    font-size: 0.9rem; /* 본문 텍스트 크기 축소 */
    margin-bottom: 1.5rem;
  }

  .home-btn {
    padding: 10px 20px; /* 버튼 패딩 축소 */
    font-size: 0.9rem; /* 버튼 폰트 크기 축소 */
  }
}
</style>