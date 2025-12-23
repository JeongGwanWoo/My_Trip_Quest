<template>
  <div>
    <h2>사용자 목록</h2>
    <div v-if="loading" class="loading-indicator">사용자 목록 로딩 중...</div>
    <div v-if="error" class="error-message">오류 발생: {{ error }}</div>

    <div v-if="users.length > 0" class="user-table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>이메일</th>
            <th>닉네임</th>
            <th>소셜 계정</th>
            <th>역할</th>
            <th>포인트</th>
            <th>가입일</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.nickname }}</td>
            <td>{{ user.provider || '일반' }}</td>
            <td>{{ user.role }}</td>
            <td>{{ user.points }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <select :value="user.role" @change="promptRoleChange(user.userId, $event.target.value)">
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div v-else-if="!loading && !error">
      <p>등록된 사용자가 없습니다.</p>
    </div>

    <!-- Confirmation Modal -->
    <BaseModal :show="isModalVisible" @close="cancelRoleChange">
      <div class="modal-body">
        <h3>역할 변경 확인</h3>
        <p>{{ modalMessage }}</p>
        <div class="modal-actions">
          <button @click="confirmRoleChange" class="btn-confirm">확인</button>
          <button @click="cancelRoleChange" class="btn-cancel">취소</button>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllUsers, updateUserRole } from '@/api/admin';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import { useToast } from '@/utils/toast';
import BaseModal from '@/components/ui/BaseModal.vue';

const users = ref([]);
const loading = ref(false);
const error = ref(null);
const authStore = useAuthStore();
const router = useRouter();
const { showToast } = useToast();

// Modal state
const isModalVisible = ref(false);
const modalMessage = ref('');
const pendingRoleChange = ref(null);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR');
};

const fetchUsers = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await getAllUsers();
    users.value = response.data;
  } catch (err) {
    console.error('사용자 목록 가져오기 실패:', err);
    error.value = '사용자 목록을 가져오는데 실패했습니다.';
    if (err.response && err.response.status === 403) {
      showToast('관리자 권한이 없습니다. 로그인 페이지로 이동합니다.', 'error');
      authStore.logout();
      router.push('/login');
    }
  } finally {
    loading.value = false;
  }
};

const promptRoleChange = (userId, newRole) => {
  modalMessage.value = `${userId}번 사용자의 역할을 ${newRole}(으)로 변경하시겠습니까?`;
  pendingRoleChange.value = { userId, newRole };
  isModalVisible.value = true;
};

const cancelRoleChange = async () => {
  isModalVisible.value = false;
  pendingRoleChange.value = null;
  await fetchUsers(); // Re-fetch to reset the dropdown
};

const confirmRoleChange = async () => {
  if (!pendingRoleChange.value) return;

  const { userId, newRole } = pendingRoleChange.value;
  isModalVisible.value = false;

  try {
    await updateUserRole(userId, newRole);
    showToast('사용자 역할이 성공적으로 변경되었습니다!', 'success');
  } catch (err) {
    console.error('역할 변경 실패:', err);
    showToast('사용자 역할 변경에 실패했습니다.', 'error');
  } finally {
    pendingRoleChange.value = null;
    await fetchUsers();
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
/* Scoped styles from the old Admin.vue related to the user table */
.loading-indicator, .error-message {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 5px;
}

.loading-indicator {
  background-color: #e0f7fa;
  color: #00796b;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
}

.user-table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background-color: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px 15px;
  text-align: left;
}

th {
  background-color: #f8f8f8;
  font-weight: bold;
  color: #333;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

tr:hover {
  background-color: #f1f1f1;
}

td select {
  padding: 8px;
  border-radius: 5px;
  border: 1px solid #ccc;
  background-color: #fff;
  cursor: pointer;
  font-size: 14px;
}

td select:focus {
  border-color: #2563eb;
  outline: none;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.2);
}

/* Modal Styles */
.modal-body {
  text-align: center;
}

.modal-body h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.modal-body p {
  margin-bottom: 1.5rem;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.modal-actions button {
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.btn-confirm {
  background-color: #2563eb;
  color: white;
}

.btn-confirm:hover {
  background-color: #1d4ed8;
}

.btn-cancel {
  background-color: #e2e8f0;
  color: #333;
}

.btn-cancel:hover {
  background-color: #cbd5e1;
}
</style>
