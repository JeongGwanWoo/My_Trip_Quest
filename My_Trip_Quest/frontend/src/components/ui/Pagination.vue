<template>
  <nav class="pagination-nav" aria-label="Page navigation">
    <ul class="pagination">
      <!-- Previous Button -->
      <li class="page-item" :class="{ disabled: currentPage === 1 }">
        <button class="page-link" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
          &laquo;
        </button>
      </li>

      <!-- Page Numbers -->
      <li v-for="page in pages" :key="page" class="page-item" :class="{ active: page === currentPage }">
        <button class="page-link" @click="changePage(page)">
          {{ page }}
        </button>
      </li>

      <!-- Next Button -->
      <li class="page-item" :class="{ disabled: currentPage === totalPages }">
        <button class="page-link" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
          &raquo;
        </button>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  maxVisibleButtons: {
    type: Number,
    default: 5,
  },
});

const emit = defineEmits(['page-changed']);

const pages = computed(() => {
  const startPage = Math.floor((props.currentPage - 1) / props.maxVisibleButtons) * props.maxVisibleButtons + 1;
  const endPage = Math.min(startPage + props.maxVisibleButtons - 1, props.totalPages);
  
  const pageNumbers = [];
  for (let i = startPage; i <= endPage; i++) {
    pageNumbers.push(i);
  }
  return pageNumbers;
});

const changePage = (page) => {
  if (page < 1 || page > props.totalPages || page === props.currentPage) {
    return;
  }
  emit('page-changed', page);
};
</script>

<style scoped>
.pagination-nav {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.pagination {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.page-item .page-link {
  display: block;
  padding: 10px 15px;
  min-width: 40px;
  text-align: center;
  border: none;
  background-color: #fff;
  color: #334155;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
  border-right: 1px solid #f1f5f9;
}
.page-item:last-child .page-link {
    border-right: none;
}
.page-item .page-link:hover {
  background-color: #f8fafc;
}

.page-item.active .page-link {
  background-color: #3b82f6;
  color: #fff;
  cursor: default;
}

.page-item.disabled .page-link {
  color: #cbd5e1;
  cursor: not-allowed;
  background-color: #f8fafc;
}
</style>
