<template>
  <div class="coin-history-page">
    <div class="content-container">
      <header class="page-header">
        <h2 class="page-title">코인 이용 내역</h2>
      </header>
      <div class="history-card">
        <div v-if="loading" class="loading-spinner">
          <i class="fa-solid fa-spinner fa-spin"></i>
          <span>로딩 중...</span>
        </div>

        <div v-else-if="history.length > 0" class="history-table-container">
          <table class="history-table">
            <thead>
              <tr>
                <th>날짜</th>
                <th>내용</th>
                <th>포인트</th>
                <th>잔액</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in history" :key="item.id">
                <td data-label="날짜">{{ formatDate(item.createdAt) }}</td>
                <td data-label="내용">{{ item.description }}</td>
                <td :class="item.amount > 0 ? 'text-green' : 'text-red'" data-label="포인트">
                  {{ item.amount > 0 ? '+' : '' }}{{ item.amount.toLocaleString() }}
                </td>
                <td data-label="잔액">{{ item.balance.toLocaleString() }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="no-history">
          <p>아직 코인 이용 내역이 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getCoinHistory } from '@/api/user.js';
import { useToast } from '@/utils/toast';

const { showToast } = useToast();
const history = ref([]);
const loading = ref(true);

const fetchHistory = async () => {
  try {
    const response = await getCoinHistory();
    if (response.success) {
      history.value = response.data;
    } else {
      showToast(response.message || '내역을 불러오는 데 실패했습니다.', 'error');
    }
  } catch (error) {
    showToast('코인 내역을 불러오는 중 오류가 발생했습니다.', 'error');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

onMounted(() => {
  fetchHistory();
});
</script>



<style scoped>

.coin-history-page {

  font-family: "Pretendard", -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;

  width: 100%;

  display: flex;

  justify-content: center;

  background-color: #f5f7fb;

  min-height: 100%;

}



.content-container {

  max-width: 800px;

  width: 100%;

  padding: 40px 20px;

}



.page-header {

  margin-bottom: 24px;

}



.page-title {

  font-size: 32px;

  font-weight: 800;

  color: #1e293b;

}



.history-card {

  background: #fff;

  border-radius: 20px;

  padding: 32px;

  border: 1px solid #eef2ff;

  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

}



.loading-spinner, .no-history {

  text-align: center;

  padding: 40px;

  color: #64748b;

  font-size: 16px;

}

.loading-spinner i {

  margin-right: 8px;

}



.history-table-container {

  overflow-x: auto;

}



.history-table {

  width: 100%;

  border-collapse: collapse;

  font-size: 14px;

}



.history-table th, .history-table td {

  padding: 16px;

  text-align: left;

  border-bottom: 1px solid #f1f5f9;

}



.history-table th {

  background-color: #f8fafc;

  color: #64748b;

  font-weight: 600;

  font-size: 12px;

  text-transform: uppercase;

}



.history-table tbody tr:last-child td {

  border-bottom: none;

}



.history-table tbody tr:hover {

  background-color: #f8fafc;

}



.text-green {

  color: #16a34a;

  font-weight: 600;

}



.text-red {

  color: #dc2626;

  font-weight: 600;

}



td:nth-child(1) {

  color: #64748b;

  font-size: 13px;

}



td:nth-child(3), td:nth-child(4) {

  text-align: right;

  font-weight: 600;

}



/* Responsive Styles */

@media (max-width: 768px) {

  .content-container {

    padding: 20px 15px;

  }



  .page-title {

    font-size: 24px;

  }



  .history-card {

    padding: 0;

    background-color: transparent;

    border: none;

    box-shadow: none;

  }



  .history-table-container {

    overflow-x: hidden;

  }



  .history-table {

    border: none;

  }



  .history-table thead {

    display: none;

  }



  .history-table tbody, .history-table tr, .history-table td {

    display: block;

    width: 100%;

  }



  .history-table tr {

    background: #fff;

    border-radius: 12px;

    margin-bottom: 12px;

    box-shadow: 0 2px 8px rgba(0,0,0,0.06);

    border: 1px solid #eef2ff;

  }



  .history-table td {

    display: flex;

    justify-content: space-between;

    align-items: center;

    padding: 12px 15px;

    border: none;

    border-bottom: 1px solid #f1f5f9;

    text-align: right;

    font-size: 14px;

  }



  .history-table tr td:last-child {

    border-bottom: none;

  }



  .history-table td::before {

    content: attr(data-label);

    font-weight: 600;

    color: #334155;

    text-align: left;

  }



  .history-table td:nth-child(1)::before { content: "날짜"; }

  .history-table td:nth-child(2)::before { content: "내용"; }

  .history-table td:nth-child(3)::before { content: "포인트"; }

  .history-table td:nth-child(4)::before { content: "잔액"; }



  .history-table td:nth-child(1) {

    font-size: 13px;

    color: #64748b;

  }

}

</style>
