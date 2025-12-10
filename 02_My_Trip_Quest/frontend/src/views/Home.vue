<template>
    <main class="main-content">
      <section class="text-section">
        <div class="badge">
          <span class="badge-dot"></span> TEXTOK
        </div>
        
        <h1 class="main-title">
          MyTripQuest<br />
          여행에서 또다른 즐거움이 생기다
        </h1>
        
        <p class="sub-desc">
          📍 위치 기반 퀘스트로 여행을 더 흥미롭게<br /><br />
          🗺️ 사용자가 직접 참여하는 여행 미션<br /><br />
          🎉 즐기면서 완성하는 나만의 여행 기록
        </p>

        <div class="stats-row">
          <div class="stat-item">
            <strong class="stat-num">1,000+</strong>
            <span class="stat-label">누적 사용자</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <strong class="stat-num">3,200+</strong>
            <span class="stat-label">완료된 퀘스트</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <strong class="stat-num">850+</strong>
            <span class="stat-label">등록된 여행 미션</span>
          </div>
        </div>

        <button class="btn-cta" @click="handleCtaClick">
          {{ isLoggedIn ? '여행 시작하기 🚀' : '로그인 하러가기 →' }}
        </button>
      </section>

      <section class="image-section">
        <img 
          :src="heroImage" 
          alt="Travel Image" 
          class="hero-image"
        />
      </section>
    </main>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth' // Auth 스토어 import
import heroImage from '@/assets/images/main-hero.png';

const router = useRouter()
const authStore = useAuthStore()

// 로그인 여부 확인
const isLoggedIn = computed(() => !!authStore.token)

// 버튼 클릭 핸들러
const handleCtaClick = () => {
  if (isLoggedIn.value) {
    // 로그인이 되어 있다면 -> 여행 지도(퀘스트 맵)로 이동
    router.push('/quest-map')
  } else {
    // 로그인이 안 되어 있다면 -> 로그인 페이지로 이동
    router.push('/login')
  }
}
</script>

<style scoped>
/* --- 메인 콘텐츠 (PC 기본) --- */
.main-content {
  flex: 1;
  display: flex;
  background-color: #f5f7fb;
  height: 100%; /* 부모 높이 상속 */
  overflow: hidden; /* 스크롤 방지 (필요 시 제거) */
}

.text-section {
  flex: 1;
  padding: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 720px;
}

.badge {
  display: inline-flex;
  align-items: center;
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 28px;
  width: fit-content;
}
.badge-dot {
  width: 6px;
  height: 6px;
  background-color: #4f46e5;
  border-radius: 50%;
  margin-right: 6px;
}

.main-title {
  font-size: 44px;
  font-weight: 800;
  line-height: 1.4;
  margin-bottom: 32px;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.sub-desc {
  font-size: 17px;
  line-height: 1.7;
  color: #4b5563;
  margin-bottom: 60px;
  font-weight: 400;
  word-break: keep-all; /* 한글 줄바꿈 개선 */
}

.stats-row {
  display: flex;
  align-items: center;
  margin-bottom: 80px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-right: 40px;
}
.stat-item:last-child {
  padding-right: 0;
}
.stat-num {
  font-size: 32px;
  font-weight: 800;
  color: #2563eb;
  letter-spacing: -1px;
}
.stat-label {
  font-size: 15px;
  color: #6b7280;
  font-weight: 500;
}
.stat-divider {
  width: 1px;
  height: 40px;
  background-color: #d1d5db;
  margin-right: 40px;
}

.btn-cta {
  background-color: #1e293b;
  color: white;
  padding: 18px 40px;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  width: fit-content;
  transition: background 0.2s;
  font-family: inherit;
}
.btn-cta:hover {
  background-color: #374151;
}

.image-section {
  flex: 0.8; /* 이미지 영역 비율 */
  position: relative;
  overflow: hidden;
}
.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ---------------------------------------------------- */
/* ★ 태블릿 및 작은 노트북 (1024px 이하) 반응형 수정 ★ */
/* ---------------------------------------------------- */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column; /* 세로 배치로 변경 */
    height: auto; /* 내용만큼 늘어나게 */
    overflow-y: auto; /* 세로 스크롤 허용 */
  }

  /* 이미지 섹션: 상단 배치 및 높이 조절 */
  .image-section {
    order: -1; /* 순서 맨 위로 */
    width: 100%;
    height: 40vh; /* 화면 높이의 40%만 차지 */
    flex: none;
  }

  /* 텍스트 섹션: 중앙 정렬 및 여백 축소 */
  .text-section {
    padding: 40px 24px 60px 24px;
    align-items: center; /* 가로 중앙 정렬 */
    text-align: center; /* 텍스트 중앙 정렬 */
    max-width: 100%;
  }

  /* 타이틀 크기 줄임 */
  .main-title {
    font-size: 32px; 
    margin-bottom: 20px;
  }

  .badge {
    margin-bottom: 20px;
  }

  .sub-desc {
    font-size: 15px;
    margin-bottom: 40px;
  }

  /* 통계 영역 간격 및 정렬 수정 */
  .stats-row {
    margin-bottom: 40px;
    justify-content: center; /* 중앙 정렬 */
  }
  
  .stat-item {
    padding-right: 0;
    align-items: center; /* 스탯 텍스트 중앙 */
  }
  
  .stat-divider {
    margin: 0 20px; /* 구분선 간격 축소 */
  }
  
  .stat-num {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 13px;
  }
}

/* 모바일 (600px 이하) 추가 최적화 */
@media (max-width: 600px) {
  .image-section {
    height: 35vh; /* 이미지를 좀 더 줄임 */
  }
  
  .main-title {
    font-size: 28px;
  }
  
  .stats-row {
    gap: 10px;
  }
  
  .stat-divider {
    margin: 0 10px;
  }
  
  .btn-cta {
    width: 100%; /* 버튼 꽉 차게 */
  }
}
</style>