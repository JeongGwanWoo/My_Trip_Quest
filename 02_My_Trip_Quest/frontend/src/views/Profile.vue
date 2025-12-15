<template>
  <div class="profile-page">
    <div class="content-container">
      
      <header class="page-header">
        <div class="badge">
          <span class="badge-dot"></span> MY PROFILE
        </div>
        <h2 class="page-title">내 여행 기록</h2>
      </header>

      <div class="dashboard-layout">
        
        <aside class="profile-column">
          <div class="profile-card">
            
            <button class="settings-btn" title="회원 정보 수정" @click="openEditModal">
              <i class="fa-solid fa-gear"></i>
            </button>

            <div class="avatar-area">
              <div class="avatar-circle-bg"></div>
              <div class="avatar-layers">
                <img :src="equippedItemsBySlot.SKIN?.imageUrl || '/assets/avatar/avatar-base.png'" alt="skin" class="layer skin">
                <img v-if="equippedItemsBySlot.BOTTOM" :src="equippedItemsBySlot.BOTTOM.imageUrl" class="layer bottom">
                <img v-if="equippedItemsBySlot.TOP" :src="equippedItemsBySlot.TOP.imageUrl" class="layer top">
                <img v-if="equippedItemsBySlot.FACE" :src="equippedItemsBySlot.FACE.imageUrl" class="layer face">
                <img v-if="equippedItemsBySlot.HAIR" :src="equippedItemsBySlot.HAIR.imageUrl" class="layer hair">
                <img v-if="equippedItemsBySlot.HAT" :src="equippedItemsBySlot.HAT.imageUrl" class="layer hat">
              </div>
            </div>

            <div class="user-info">
              <h2 class="username">{{ userProfile?.nickname || '여행자' }}</h2>
              <span class="user-email">{{ userProfile?.email || 'email@example.com' }}</span>
              <div class="joined-date">
                <span class="icon"><i class="fa-solid fa-calendar-days"></i></span> 가입일: {{ userJoined }}
              </div>
            </div>
          </div>
        </aside>

        <main class="stats-column">
          
          <section class="level-card">
            <div class="level-header">
              <div class="level-badge-group">
                <span class="level-icon"><i class="fa-solid fa-star"></i></span>
                <div class="level-text">
                  <span class="label">CURRENT LEVEL</span>
                  <span class="value">Lv.{{ levelInfo.currentLevel }}</span>
                </div>
              </div>
              <div class="xp-text">
                <span class="current">{{ userProfile?.totalXp || 0 }}</span>
                <span class="total">/ {{ levelInfo.xpForNextLevel }} XP</span>
              </div>
            </div>

            <div class="progress-container">
              <div class="progress-bar-bg">
                <div 
                  class="progress-bar-fill" 
                  :style="{ width: levelInfo.progressPercentage + '%' }"
                ></div>
              </div>
              <div class="progress-footer">
                <span class="next-level-tip">다음 레벨까지 {{ levelInfo.xpForNextLevel - (userProfile?.totalXp || 0) }} XP 남았습니다! 힘내세요! <i class="fa-solid fa-fire"></i></span>
                <span class="percentage">{{ levelInfo.progressPercentage }}%</span>
              </div>
            </div>
          </section>

          <section class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon-box yellow"><span class="icon"><i class="fa-solid fa-coins"></i></span></div>
              <div class="stat-info">
                <span class="label">보유 코인</span>
                <span class="value">{{ (userProfile?.points || 0).toLocaleString() }} P</span>
              </div>
            </div>
            <div class="stat-card green">
              <div class="stat-icon-box green"><span class="icon"><i class="fa-solid fa-trophy"></i></span></div>
              <div class="stat-info">
                <span class="label">완료 미션</span>
                <span class="value">3 <span class="sub">/ 60</span></span>
              </div>
            </div>
            <div class="stat-card blue">
              <div class="stat-icon-box blue"><span class="icon"><i class="fa-solid fa-chart-line"></i></span></div>
              <div class="stat-info">
                <span class="label">전체 달성률</span>
                <span class="value">5%</span>
              </div>
            </div>
            <div class="stat-card purple">
              <div class="stat-icon-box purple"><span class="icon"><i class="fa-solid fa-map-location-dot"></i></span></div>
              <div class="stat-info">
                <span class="label">방문 도시</span>
                <span class="value">1 <span class="sub">/ 4</span></span>
              </div>
            </div>
          </section>

          <section class="city-section">
            <h3 class="section-title">도시별 진행 현황</h3>
            <div class="city-list">
              <div v-for="city in cityProgress" :key="city.id" class="city-item">
                <div class="city-icon"><i :class="city.icon"></i></div>
                <div class="city-info">
                  <div class="city-header">
                    <span class="city-name">{{ city.name }}</span>
                    <span class="city-percent">{{ city.percentage }}%</span>
                  </div>
                  <div class="city-progress-bg">
                    <div 
                      class="city-progress-fill" 
                      :class="city.colorClass"
                      :style="{ width: city.percentage + '%' }"
                    ></div>
                  </div>
                  <div class="city-sub-text">{{ city.completed }} / {{ city.total }} 미션 완료</div>
                </div>
              </div>
            </div>
          </section>

        </main>
      </div>
    </div>

    <BaseModal :show="isEditModalOpen" @close="closeEditModal">
      <div class="modal-inner edit-modal">
        <h3 class="modal-title">회원 정보 수정</h3>
        
        <form @submit.prevent="handleUpdateProfile" class="edit-form">
          <div class="form-group">
            <label>닉네임</label>
            <input type="text" v-model="editForm.nickname" class="form-input" />
          </div>
          
          <div class="form-group">
            <label>현재 비밀번호</label>
            <input type="password" v-model="editForm.currentPassword" placeholder="비밀번호 변경 시 필수" class="form-input" />
          </div>

          <div class="form-group">
            <label>새 비밀번호</label>
            <input type="password" v-model="editForm.newPassword" placeholder="변경할 비밀번호" class="form-input" />
          </div>

          <div class="form-group">
            <label>새 비밀번호 확인</label>
            <input type="password" v-model="editForm.confirmNewPassword" placeholder="새 비밀번호 다시 입력" class="form-input" />
          </div>

          <button type="submit" class="btn-save">저장하기</button>
        </form>

        <div class="divider"></div>

        <button class="btn-withdraw" @click="handleWithdraw">
          회원 탈퇴하기
        </button>
      </div>
    </BaseModal>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getAvatar } from '@/api/avatar.js';
import { getProfile, updateProfile, deleteAccount } from '@/api/user.js';
import { getLevelProgress } from '@/utils/level.js';
import BaseModal from "@/components/ui/BaseModal.vue"; // 모달 컴포넌트 필수
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const userProfile = ref(null);
const equippedItemsList = ref([]);
const userJoined = ref('2024. 1. 15.'); 

// --- 수정 모달 관련 상태 ---
const isEditModalOpen = ref(false);
const editForm = ref({
  nickname: '',
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
});

const openEditModal = () => {
  editForm.value.nickname = userProfile.value?.nickname || '';
  editForm.value.currentPassword = '';
  editForm.value.newPassword = '';
  editForm.value.confirmNewPassword = '';
  isEditModalOpen.value = true;
};

const closeEditModal = () => {
  isEditModalOpen.value = false;
};

const handleUpdateProfile = async () => {
  if (editForm.value.newPassword && (editForm.value.newPassword !== editForm.value.confirmNewPassword)) {
    alert('새 비밀번호가 일치하지 않습니다.');
    return;
  }

  const payload = {};
  if (editForm.value.nickname !== userProfile.value.nickname) {
    payload.nickname = editForm.value.nickname;
  }

  if (editForm.value.newPassword) {
    if (!editForm.value.currentPassword) {
      alert('현재 비밀번호를 입력해야 비밀번호를 변경할 수 있습니다.');
      return;
    }
    payload.currentPassword = editForm.value.currentPassword;
    payload.newPassword = editForm.value.newPassword;
  }

  if (Object.keys(payload).length === 0) {
    alert('변경할 내용이 없습니다.');
    return;
  }

  try {
    const response = await updateProfile(payload);
    if (response.success) {
      alert('정보가 성공적으로 수정되었습니다.');
      await fetchUserProfileData();
      closeEditModal();
    }
  } catch (error) {
    const message = error.response?.data?.message || '프로필 수정에 실패했습니다.';
    alert(message);
    console.error('Error updating profile:', error);
  }
};

const handleWithdraw = async () => {
  if (confirm('정말로 탈퇴하시겠습니까? 탈퇴 시 모든 여행 기록이 삭제됩니다.')) {
    try {
      const response = await deleteAccount();
      if (response.success) {
        alert('회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.');
        authStore.logout();
      }
    } catch (error) {
      const message = error.response?.data?.message || '회원 탈퇴에 실패했습니다.';
      alert(message);
      console.error('Error deleting account:', error);
    }
  }
};
// -----------------------

const levelInfo = computed(() => {
  if (userProfile.value) {
    return getLevelProgress(userProfile.value.totalXp);
  }
  return {
    currentLevel: 1,
    xpForNextLevel: 1000,
    progressPercentage: 0,
  };
});

const equippedItemsBySlot = computed(() => {
  const slots = {
    SKIN: null, HAIR: null, HAT: null, TOP: null, BOTTOM: null, FACE: null,
  };
  for (const item of equippedItemsList.value) {
    if (item && item.slot) {
      slots[item.slot.toUpperCase()] = item;
    }
  }
  return slots;
});

const fetchUserProfileData = async () => {
  try {
    const response = await getProfile();
    if (response.success) {
      userProfile.value = response.data;
    }
  } catch (error) {
    console.error("Failed to fetch user profile data:", error);
  }
};

const fetchAvatarData = async () => {
  try {
    const response = await getAvatar(); 
    if (response.success) {
      equippedItemsList.value = response.data.equippedItems;
    }
  } catch (error) {
    console.error("Failed to fetch avatar data:", error);
  }
};

onMounted(async () => {
  await fetchUserProfileData();
  await fetchAvatarData(); 
});

const cityProgress = ref([
  { id: 1, name: '서울', icon: 'fa-solid fa-city', completed: 3, total: 15, percentage: 20, colorClass: 'bg-green' },
  { id: 2, name: '부산', icon: 'fa-solid fa-water', completed: 0, total: 15, percentage: 0, colorClass: 'bg-blue' },
  { id: 3, name: '제주', icon: 'fa-solid fa-tree', completed: 0, total: 15, percentage: 0, colorClass: 'bg-orange' },
  { id: 4, name: '경주', icon: 'fa-solid fa-archway', completed: 0, total: 15, percentage: 0, colorClass: 'bg-purple' },
]);
</script>

<style scoped>
/* 기본 설정 */
.profile-page {
  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
  width: 100%;
  display: flex;
  justify-content: center;
  background-color: #f5f7fb;
  min-height: 100%;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* --- Header --- */
.page-header {
  margin-bottom: 8px;
}

.badge {
  display: inline-flex;
  align-items: center;
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 12px;
}
.badge-dot {
  width: 6px;
  height: 6px;
  background-color: #4f46e5;
  border-radius: 50%;
  margin-right: 6px;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
  letter-spacing: -0.5px;
}

/* --- Dashboard Layout --- */
.dashboard-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
  align-items: start;
}

/* --- 1. Profile Column (Left) --- */
.profile-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #eef2ff;
  position: sticky;
  top: 24px;
}

/* 설정 버튼 스타일 */
.settings-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #94a3b8;
  transition: color 0.2s;
  padding: 4px;
}
.settings-btn:hover {
  color: #334155;
  transform: rotate(90deg);
  transition: transform 0.3s;
}

.avatar-area {
  position: relative;
  width: 160px;
  height: 160px;
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-circle-bg {
  position: absolute;
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, #fef3c7 0%, #fffbeb 70%);
  border-radius: 50%;
  border: 4px solid #fff;
  box-shadow: 0 0 0 2px #fcd34d; /* Yellow ring */
}

.avatar-layers {
  position: relative;
  width: 120px;
  height: 120px;
  z-index: 1;
}

.layer {
  position: absolute;
  top: 0; left: 0;
  width: 100%; height: 100%;
  object-fit: contain;
  filter: drop-shadow(0 4px 4px rgba(0,0,0,0.1));
}
.skin { z-index: 10; }
.bottom { z-index: 20; }
.top { z-index: 30; }
.face { z-index: 40; }
.hair { z-index: 50; }
.hat { z-index: 60; }

.user-info {
  text-align: center;
}
.username {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 4px 0;
}
.user-email {
  font-size: 14px;
  color: #64748b;
  display: block;
  margin-bottom: 16px;
}
.joined-date {
  font-size: 13px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: #f8fafc;
  padding: 8px 12px;
  border-radius: 8px;
}

/* --- 2. Stats Column (Right) --- */
.stats-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Level Card */
.level-card {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
  border: 1px solid #f1f5f9;
}

.level-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
}

.level-badge-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.level-icon { font-size: 28px; }
.level-text { display: flex; flex-direction: column; }
.level-text .label { font-size: 12px; color: #64748b; font-weight: 600; }
.level-text .value { font-size: 24px; font-weight: 800; color: #3b82f6; }

.xp-text { font-size: 14px; font-weight: 600; color: #64748b; }
.xp-text .current { color: #1e293b; }

.progress-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.progress-bar-bg {
  width: 100%;
  height: 12px;
  background: #f1f5f9;
  border-radius: 6px;
  overflow: hidden;
}
.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
  border-radius: 6px;
  transition: width 0.5s ease-out;
}

.progress-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
}
.percentage { font-weight: 700; color: #3b82f6; }

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 8px 12px rgba(0,0,0,0.05); }

.stat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.stat-icon-box.yellow { background: #fffbeb; color: #f59e0b; }
.stat-icon-box.green { background: #f0fdf4; color: #22c55e; }
.stat-icon-box.blue { background: #eff6ff; color: #3b82f6; }
.stat-icon-box.purple { background: #faf5ff; color: #a855f7; }

.stat-info { display: flex; flex-direction: column; }
.stat-info .label { font-size: 13px; color: #64748b; margin-bottom: 2px; }
.stat-info .value { font-size: 20px; font-weight: 800; color: #1e293b; }
.stat-info .sub { font-size: 14px; color: #cbd5e1; font-weight: 500; }

/* City Section */
.city-section {
  background: #fff;
  border-radius: 20px;
  padding: 24px;
  border: 1px solid #f1f5f9;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 20px 0;
}

.city-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.city-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
}

.city-icon {
  width: 40px;
  height: 40px;
  background: #fff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.03);
}

.city-info { flex: 1; }
.city-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.city-name { font-size: 14px; font-weight: 600; color: #334155; }
.city-percent { font-size: 13px; font-weight: 700; color: #64748b; }

.city-progress-bg {
  width: 100%; height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  margin-bottom: 6px;
  overflow: hidden;
}
.city-progress-fill { height: 100%; border-radius: 3px; }
.bg-green { background: #22c55e; }
.bg-blue { background: #3b82f6; }
.bg-orange { background: #f97316; }
.bg-purple { background: #a855f7; }

.city-sub-text { font-size: 11px; color: #94a3b8; }

/* 모달 관련 스타일 */
.edit-modal {
  padding: 10px;
  width: 100%;
  max-width: 320px;
  margin: 0 auto;
}

.modal-title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.edit-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

.form-input {
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
}
.form-input:focus {
  border-color: #3b82f6;
}

.btn-save {
  margin-top: 8px;
  padding: 12px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  cursor: pointer;
}
.btn-save:hover { background-color: #2563eb; }

.divider {
  height: 1px;
  background-color: #f1f5f9;
  margin: 24px 0 16px;
}

.btn-withdraw {
  width: 100%;
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 12px;
  text-decoration: underline;
  cursor: pointer;
}
.btn-withdraw:hover {
  color: #ef4444; 
}

/* Responsive */
@media (max-width: 900px) {
  .dashboard-layout { grid-template-columns: 1fr; }
  .profile-card {
    flex-direction: row;
    gap: 32px;
    align-items: center;
    justify-content: center;
  }
  .avatar-area { margin-bottom: 0; }
  .user-info { text-align: left; }
}

@media (max-width: 600px) {
  .profile-card { flex-direction: column; text-align: center; }
  .user-info { text-align: center; }
  .stats-grid { grid-template-columns: 1fr; }
  .city-list { grid-template-columns: 1fr; }
}
</style>