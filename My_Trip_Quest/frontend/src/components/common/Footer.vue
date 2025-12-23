<template>
  <footer class="app-footer">
    <div class="footer-container">
      <div class="footer-logo">
        <span class="brand-blue">MYTRIP</span>QUEST
      </div>
      <div class="footer-links">
        <router-link to="/terms" class="footer-link">이용약관</router-link>
        <router-link to="/privacy" class="footer-link">개인정보처리방침</router-link>
      </div>
      <div class="footer-copyright">
        &copy; 2025 MyTripQuest. All Rights Reserved.
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.app-footer {
  background-color: #f8fafc;
  border-top: 1px solid #eef2ff;
  padding: 32px;
  color: #64748b;
  font-family: "Pretendard", sans-serif;
  font-size: 14px;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-logo {
  font-family: "Pretendard", sans-serif;
  font-size: 18px;
  font-weight: 900;
  color: #1e293b;
  letter-spacing: -0.5px;
  margin-bottom: 16px;
}

.brand-blue {
  color: #2563eb;
}

.footer-links {
  margin-bottom: 16px;
}

.footer-link {
  color: #64748b;
  text-decoration: none;
  margin: 0 12px;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #3b82f6;
}

.footer-copyright {
  font-size: 12px;
  color: #94a3b8;
}
</style>