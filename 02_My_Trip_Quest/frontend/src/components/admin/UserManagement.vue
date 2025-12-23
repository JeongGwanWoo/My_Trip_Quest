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
              <select :value="user.role" @change="handleRoleChange(user.userId, $event.target.value)">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllUsers, updateUserRole } from '@/api/admin';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const users = ref([]);
const loading = ref(false);
const error = ref(null);
const authStore = useAuthStore();
const router = useRouter();

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
      alert('관리자 권한이 없습니다. 로그인 페이지로 이동합니다.');
      authStore.logout();
      router.push('/login');
    }
  } finally {
    loading.value = false;
  }
};

const handleRoleChange = async (userId, newRole) => {
  if (!confirm(`${userId}번 사용자의 역할을 ${newRole}(으)로 변경하시겠습니까?`)) {
    await fetchUsers(); 
    return;
  }

  try {
    await updateUserRole(userId, newRole);
    alert('사용자 역할이 성공적으로 변경되었습니다!');
    await fetchUsers();
  } catch (err) {
    console.error('역할 변경 실패:', err);
    alert('사용자 역할 변경에 실패했습니다.');
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
</style>
