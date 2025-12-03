<template>
  <div class="page">
    
    <div class="cloud cloud-1"></div>
    <div class="cloud cloud-2"></div>
    <div class="cloud cloud-3"></div>
    <div class="cloud cloud-4"></div>
    <div class="cloud cloud-5"></div>

    
    <main class="hero">
      <header class="hero-header">
        <span class="hero-logo">🗺️ MYTRIPQUEST</span>
      </header>

      <section class="hero-center">
        <p class="hero-label-top">TRAVEL QUEST</p>

        <h1 class="hero-title">
          MYTRIPQUEST
          <span class="hero-title-sub">COIN</span>
        </h1>

        <p class="hero-desc">
          여행하며 미션을 완료하고 코인을 획득하세요!<br />
          한국의 아름다운 도시를 탐험하고 캐릭터를 꾸며보세요! 👾
        </p>

        
        <div class="hero-bottom">
            <div class="hero-chip" @click="goNext">
            START
            </div>
        </div>
      </section>
    </main>
  </div>
</template>


<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goNext = () => {
  router.push('/main-menu')
}
</script>

<style scoped>

.page {
  width: 100%;
  min-height: 100vh;
  overflow: hidden;
  position: relative;

  display: flex;
  align-items: stretch;
  justify-content: center;

  
  background: linear-gradient(
    180deg,
    #4d2aa8 0%,
    #7c3bbf 20%,
    #ff5fa2 45%,
    #ff975c 70%,
    #ffe6a0 100%
  );
}


.hero {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 40px 48px;
  position: relative;
}


.hero::before {
  content: "";
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(#ffffffcc 1px, transparent 1px),
    radial-gradient(#ffffff88 1px, transparent 1px);
  background-size: 80px 80px, 120px 120px;
  background-position: 10px 10px, 40px 40px;
  opacity: 0.6;
  pointer-events: none;
}


.cloud {
  position: absolute;
  width: 160px;
  height: 60px;
  background: #ffffff;
  border-radius: 40px;
  box-shadow:
    30px 10px 0 #ffffff,
    60px 0 0 #ffffff;
  opacity: 0.9;
}

.cloud-1 { top: 12%; left: 8%; transform: scale(0.9); }
.cloud-2 { top: 18%; right: 10%; transform: scale(0.8); }
.cloud-3 { top: 35%; left: 18%; transform: scale(0.7); }
.cloud-4 { top: 42%; right: 20%; transform: scale(0.6); }
.cloud-5 { bottom: 12%; left: 60%; transform: scale(0.5); }


.hero-header {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: flex-start;
}

.hero-logo {
  padding: 6px 12px;
  border-radius: 999px;
  background: #00000066;
  color: #ffffff;
  font-size: 14px;
}

.hero-center {
  position: relative;
  z-index: 1;

  height: calc(100vh - 120px);
  max-height: 800px;
  margin-top: 12px;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.hero-label-top {
  font-size: 14px;
  letter-spacing: 0.3em;
  text-transform: uppercase;
  color: #ffe9ff;
  margin-bottom: 12px;
}

.hero-title {
  margin: 0 0 16px;
  font-size: clamp(40px, 7vw, 80px);
  font-weight: 900;
  text-transform: uppercase;
  color: #ffffff;

  text-shadow:
    0 4px 0 #2b135b,
    0 8px 0 #16092e;
}

.hero-title-sub {
  display: block;
  margin-top: 8px;
  font-size: 0.5em;
  letter-spacing: 0.3em;
  color: #ffe56f;
  text-shadow:
    0 3px 0 #2b135b,
    0 6px 0 #16092e;
}

.hero-desc {
  margin: 12px 0 24px;
  font-size: 15px;
  line-height: 1.8;
  color: #fff7ff;
}

.hero-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  margin-bottom: 40px;
  padding: 10px 32px;
  border-radius: 999px;

  background: #ffffffee;
  color: #3c1a6a;
  font-size: 14px;
  font-weight: 700;
  text-transform: uppercase;

  border: 3px solid #000000;
  box-shadow: 0 6px 0 #9d4b3b;
}


.hero-bottom {
  margin-top: 120px;
}

@media (max-width: 640px) {
  .hero-bottom {
    margin-top: 60px;
  }
}


.page {
  font-family: 'Press Start 2P', monospace;
  image-rendering: pixelated;
}

.hero-title {
  font-family: 'Press Start 2P', monospace;
  letter-spacing: 0.15em;
  text-shadow:
    3px 3px 0 #2b135b,
    6px 6px 0 #16092e;
}

.hero-title-sub {
  font-family: 'Press Start 2P', monospace;
}

.hero-desc,
.hero-label-top {
  font-family: 'Press Start 2P', monospace;
  font-size: 12px;
}

.hero-chip {
  font-family: 'Press Start 2P', monospace;
  font-size: 10px;
  border: 3px solid black;
  box-shadow:
    4px 4px 0 #000;
  background: #fff;
}

.feature-panel {
  border: 4px solid black;
  box-shadow:
    8px 8px 0 #000;
  background: #fff;
}

.feature-card {
  border: 3px solid black;
  box-shadow:
    4px 4px 0 #000;
}

.feature-title,
.feature-desc {
  font-family: 'Press Start 2P', monospace;
  font-size: 11px;
}

.cloud {
  filter: pixelate(2px);
  image-rendering: pixelated;
}


@media (max-width: 1024px) {
  .hero {
    padding: 24px 18px 32px;
  }

  .feature-panel {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .cloud { display: none; }

  .hero-center {
    height: auto;
    padding-top: 40px;
    padding-bottom: 40px;
  }

  .feature-panel {
    grid-template-columns: 1fr;
  }
}

.page::after {
  content: "";
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(
      0deg,
      rgba(255,255,255,0.04) 0px,
      rgba(255,255,255,0.04) 1px,
      transparent 1px,
      transparent 4px
    ),
    repeating-linear-gradient(
      90deg,
      rgba(255,255,255,0.04) 0px,
      rgba(255,255,255,0.04) 1px,
      transparent 1px,
      transparent 4px
    );
  pointer-events: none;
}

</style>
