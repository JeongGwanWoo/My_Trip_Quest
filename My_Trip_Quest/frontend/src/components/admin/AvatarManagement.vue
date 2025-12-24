<template>
  <div class="avatar-management">
    <h2>아바타 아이템 관리</h2>

    <!-- 아이템 추가 폼 -->
    <div class="add-item-section">
      <h3>새 아바타 아이템 추가</h3>
      <form @submit.prevent="createItem" class="add-item-form">
        <div class="form-group">
          <label for="itemName">이름:</label>
          <input type="text" id="itemName" v-model="newItem.name" required />
        </div>
        <div class="form-group">
          <label for="itemSlot">슬롯:</label>
          <select id="itemSlot" v-model="newItem.slot" required>
            <option value="HAT">모자</option>
            <option value="HAIR">헤어</option>
            <option value="TOP">상의</option>
            <option value="BOTTOM">하의</option>
            <option value="SKIN">스킨</option>
            <option value="FACE">얼굴</option>
          </select>
        </div>
        <div class="form-group">
          <label for="itemPrice">가격:</label>
          <input type="number" id="itemPrice" v-model.number="newItem.price" required min="0" />
        </div>
        <div class="form-group checkbox-group">
          <input type="checkbox" id="isPurchasable" v-model="newItem.purchasable" />
          <label for="isPurchasable">구매 가능 아이템</label>
        </div>
        <div class="form-group">
          <label for="itemImage">이미지 파일:</label>
          <div class="file-upload-wrapper">
            <input type="file" id="itemImage" class="file-upload-input" @change="handleImageUpload" accept="image/*" required />
            <label for="itemImage" class="btn-file-upload">파일 선택</label>
            <span class="file-name-display">{{ newItemImageName || '선택된 파일 없음' }}</span>
          </div>
        </div>
        <button type="submit" class="btn-primary" :disabled="creatingItem">
          <span v-if="creatingItem">추가 중...</span>
          <span v-else>아이템 추가</span>
        </button>
      </form>
    </div>

    <hr />

    <!-- 아이템 목록 -->
    <div class="item-list-section">
      <h3>등록된 아바타 아이템</h3>
      <div v-if="loading" class="loading-indicator">아이템 목록 로딩 중...</div>
      <div v-if="error" class="error-message">오류 발생: {{ error }}</div>

      <div v-if="items.length > 0" class="item-table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>이미지</th>
              <th>이름</th>
              <th>슬롯</th>
              <th>가격</th>
              <th>구매 가능</th>
              <th>생성일</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in items" :key="item.itemId">
              <td>{{ item.itemId }}</td>
              <td class="item-thumbnail">
                <img :src="item.imageUrl" :alt="item.name" v-if="item.imageUrl" />
                <span v-else>No Image</span>
              </td>
              <td>{{ item.name }}</td>
              <td>{{ itemSlotMap[item.slot] || item.slot }}</td>
              <td>{{ item.price }}</td>
              <td>{{ item.purchasable ? 'Yes' : 'No' }}</td>
              <td>{{ formatDate(item.createdAt) }}</td>
              <td class="action-buttons">
                <button @click="openEditModal(item)" class="btn-edit">편집</button>
                <button @click="promptDeleteItem(item.itemId, item.name)" class="btn-delete">삭제</button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 0">이전</button>
          <span>페이지 {{ currentPage + 1 }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage >= totalPages - 1">다음</button>
        </div>
      </div>
      <div v-else-if="!loading && !error">
        <p>등록된 아바타 아이템이 없습니다.</p>
      </div>
    </div>

    <!-- Confirmation Modal -->
    <BaseModal :show="isConfirmModalVisible" @close="isConfirmModalVisible = false">
      <div class="modal-body">
        <h3>아이템 삭제 확인</h3>
        <p>"{{ itemToDeleteName }}" 아이템을 정말 삭제하시겠습니까?</p>
        <div class="modal-actions">
          <button @click="confirmDeleteItem" class="btn-confirm-delete">삭제</button>
          <button @click="isConfirmModalVisible = false" class="btn-cancel">취소</button>
        </div>
      </div>
    </BaseModal>

    <!-- Edit Item Modal -->
    <BaseModal :show="isEditModalVisible" @close="closeEditModal">
      <div class="modal-body">
        <h3>아이템 편집</h3>
        <form @submit.prevent="updateItem" class="edit-item-form">
          <div class="form-group">
            <label for="editItemName">이름:</label>
            <input type="text" id="editItemName" v-model="editingItem.name" required />
          </div>
          <div class="form-group">
            <label for="editItemSlot">슬롯:</label>
            <select id="editItemSlot" v-model="editingItem.slot" required>
              <option value="HAT">모자</option>
              <option value="HAIR">헤어</option>
              <option value="TOP">상의</option>
              <option value="BOTTOM">하의</option>
              <option value="SKIN">스킨</option>
              <option value="FACE">얼굴</option>
            </select>
          </div>
          <div class="form-group">
            <label for="editItemPrice">가격:</label>
            <input type="number" id="editItemPrice" v-model.number="editingItem.price" required min="0" />
          </div>
          <div class="form-group checkbox-group">
            <input type="checkbox" id="editIsPurchasable" v-model="editingItem.purchasable" />
            <label for="editIsPurchasable">구매 가능 아이템</label>
          </div>
          <div class="form-group">
            <label>현재 이미지:</label>
            <img :src="editingItem.imageUrl" alt="Current Image" class="current-item-image" v-if="editingItem.imageUrl"/>
            <span v-else>이미지 없음</span>
          </div>
          <div class="form-group">
            <label for="editItemImage">새 이미지 파일 (선택 사항):</label>
            <div class="file-upload-wrapper">
                <input type="file" id="editItemImage" class="file-upload-input" @change="handleEditImageUpload" accept="image/*" />
                <label for="editItemImage" class="btn-file-upload">파일 선택</label>
                <span class="file-name-display">{{ editingItemImageName || '선택된 파일 없음' }}</span>
            </div>
            <small class="hint">새 이미지를 선택하면 기존 이미지를 대체합니다.</small>
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn-confirm" :disabled="updatingItem">
              <span v-if="updatingItem">저장 중...</span>
              <span v-else>변경 사항 저장</span>
            </button>
            <button type="button" @click="closeEditModal" class="btn-cancel">취소</button>
          </div>
        </form>
      </div>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getAllAvatarItems, createAvatarItem, deleteAvatarItem, updateAvatarItem } from '@/api/admin';
import { useToast } from '@/utils/toast';
import BaseModal from '@/components/ui/BaseModal.vue';

const { showToast } = useToast();

const items = ref([]);
const loading = ref(false);
const error = ref(null);
const creatingItem = ref(false);
const updatingItem = ref(false); // Added

const newItem = ref({
  name: '',
  slot: 'HAT', // Default value
  price: 0,
  purchasable: true,
});
const newItemImage = ref(null);
const newItemImageName = ref(''); // For displaying the file name

// Pagination
const currentPage = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);
const totalElements = ref(0);

// Confirmation Modal State
const isConfirmModalVisible = ref(false);
const itemToDeleteId = ref(null);
const itemToDeleteName = ref('');

// Edit Modal State
const isEditModalVisible = ref(false); // Added
const editingItem = ref(null); // Added
const editingItemImage = ref(null); // Added
const editingItemImageName = ref(''); // Added
const editingItemImageChanged = ref(false); // Added

const itemSlotMap = {
  HAT: '모자',
  HAIR: '헤어',
  TOP: '상의',
  BOTTOM: '하의',
  SKIN: '스킨',
  FACE: '얼굴',
  // ACC: '악세사리', // Removed, as per backend enum
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR');
};

const fetchItems = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await getAllAvatarItems(currentPage.value, pageSize.value);
    items.value = response.data.data.content;
    totalPages.value = response.data.data.totalPages;
    totalElements.value = response.data.data.totalElements;
  } catch (err) {
    console.error('Failed to fetch avatar items:', err);
    error.value = '아바타 아이템 목록을 불러오는데 실패했습니다.';
    showToast(error.value, 'error');
  } finally {
    loading.value = false;
  }
};

const handleImageUpload = (event) => {
  const file = event.target.files[0];
  newItemImage.value = file;
  newItemImageName.value = file ? file.name : '';
};

const createItem = async () => {
  if (!newItemImage.value) {
    showToast('이미지 파일을 선택해주세요.', 'warn');
    return;
  }

  creatingItem.value = true;
  try {
    await createAvatarItem(newItem.value, newItemImage.value);
    showToast('아이템이 성공적으로 추가되었습니다!', 'success');
    resetNewItemForm();
    await fetchItems(); // Refresh the list
  } catch (err) {
    console.error('Failed to create item:', err);
    showToast('아이템 추가에 실패했습니다: ' + (err.response?.data?.message || err.message), 'error');
  } finally {
    creatingItem.value = false;
  }
};

const resetNewItemForm = () => {
  newItem.value = {
    name: '',
    slot: 'HAT',
    price: 0,
    purchasable: true,
  };
  newItemImage.value = null;
  newItemImageName.value = '';
  const fileInput = document.getElementById('itemImage');
  if (fileInput) fileInput.value = ''; // Clear file input
};

const promptDeleteItem = (itemId, itemName) => {
  itemToDeleteId.value = itemId;
  itemToDeleteName.value = itemName;
  isConfirmModalVisible.value = true;
};

const confirmDeleteItem = async () => {
  if (!itemToDeleteId.value) return;
  isConfirmModalVisible.value = false;
  
  try {
    await deleteAvatarItem(itemToDeleteId.value);
    showToast('아이템이 성공적으로 삭제되었습니다.', 'success');
    await fetchItems(); // Refresh the list
  } catch (err) {
    console.error('Failed to delete item:', err);
    showToast('아이템 삭제에 실패했습니다: ' + (err.response?.data?.message || err.message), 'error');
  } finally {
    itemToDeleteId.value = null;
    itemToDeleteName.value = '';
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
    fetchItems();
  }
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
    fetchItems();
  }
};

const openEditModal = (item) => { // Added
  editingItem.value = { ...item }; // Copy item data to editing form
  editingItemImage.value = null; // Clear previous image selection
  editingItemImageChanged.value = false; // Reset image changed flag
  isEditModalVisible.value = true;
};

const closeEditModal = () => { // Added
  isEditModalVisible.value = false;
  editingItem.value = null;
  editingItemImage.value = null;
  editingItemImageName.value = '';
  editingItemImageChanged.value = false;
  const fileInput = document.getElementById('editItemImage');
  if (fileInput) fileInput.value = ''; // Clear file input
};

const handleEditImageUpload = (event) => { // Added
  const file = event.target.files[0];
  editingItemImage.value = file;
  editingItemImageName.value = file ? file.name : '';
  editingItemImageChanged.value = true; // Set flag
};

const updateItem = async () => { // Added
  if (!editingItem.value) return;

  updatingItem.value = true;
  try {
    const itemDataToSend = {
      name: editingItem.value.name,
      slot: editingItem.value.slot,
      price: editingItem.value.price,
      purchasable: editingItem.value.purchasable,
      imageChanged: editingItemImageChanged.value // Send flag to backend
    };

    await updateAvatarItem(editingItem.value.itemId, itemDataToSend, editingItemImage.value);
    showToast('아이템이 성공적으로 업데이트되었습니다!', 'success');
    closeEditModal();
    await fetchItems(); // Refresh the list
  } catch (err) {
    console.error('Failed to update item:', err);
    showToast('아이템 업데이트에 실패했습니다: ' + (err.response?.data?.message || err.message), 'error');
  } finally {
    updatingItem.value = false;
  }
};

onMounted(() => {
  fetchItems();
});
</script>

<style scoped>
.avatar-management {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

h2 {
  color: #343a40;
  margin-bottom: 25px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 10px;
}

h3 {
  color: #495057;
  margin-top: 30px;
  margin-bottom: 20px;
  font-size: 1.3rem;
}

hr {
  border: none;
  border-top: 1px solid #e9ecef;
  margin: 40px 0;
}

/* Add Item Form */
.add-item-section {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

.add-item-form .form-group {
  margin-bottom: 15px;
}

.add-item-form label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #6c757d;
}

.add-item-form input[type="text"],
.add-item-form input[type="number"],
.add-item-form select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
}

/* Custom File Upload Styles */
.file-upload-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-upload-input {
  display: none;
}

.btn-file-upload {
  display: inline-block;
  padding: 10px 15px;
  background-color: #e9ecef;
  color: #343a40;
  border: 1px solid #ced4da;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.95rem;
  transition: background-color 0.2s ease;
  white-space: nowrap;
}

.btn-file-upload:hover {
  background-color: #dae0e5;
}

.file-name-display {
  color: #495057;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
/* End Custom File Upload Styles */

.add-item-form input[type="file"] {
  padding: 8px 0;
}

.add-item-form .checkbox-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.add-item-form .checkbox-group input[type="checkbox"] {
  width: auto;
  margin: 0;
}

.add-item-form .btn-primary {
  display: block;
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.add-item-form .btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.add-item-form .btn-primary:disabled {
  background-color: #a0c4ff;
  cursor: not-allowed;
}

/* Item List */
.loading-indicator, .error-message {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 5px;
  text-align: center;
}

.loading-indicator {
  background-color: #e7f5ff;
  color: #007bff;
}

.error-message {
  background-color: #ffe0e0;
  color: #dc3545;
}

.item-table-container {
  overflow-x: auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
}

th, td {
  border: 1px solid #dee2e6;
  padding: 12px 15px;
  text-align: left;
  font-size: 0.95rem;
}

th {
  background-color: #e9ecef;
  font-weight: 600;
  color: #495057;
}

tr:nth-child(even) {
  background-color: #f8f9fa;
}

.item-thumbnail img {
  width: 50px;
  height: 50px;
  object-fit: contain;
  border-radius: 3px;
  border: 1px solid #eee;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-edit {
  padding: 6px 12px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.btn-edit:hover {
  background-color: #5a6268;
}

.btn-delete {
  padding: 6px 12px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.btn-delete:hover {
  background-color: #c82333;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 25px;
  font-size: 1rem;
  color: #6c757d;
}

.pagination button {
  padding: 8px 15px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  background-color: #0056b3;
}

.pagination button:disabled {
  background-color: #a0c4ff;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 50px;
  color: #6c757d;
  font-size: 1.1rem;
}

/* Modal Styles - Reused from previous tasks for consistency */
.modal-body {
  text-align: center;
}

.modal-body h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.25rem;
  color: #343a40;
}

.modal-body p {
  margin-bottom: 1.5rem;
  color: #495057;
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

.btn-confirm-delete {
  background-color: #dc3545;
  color: white;
}

.btn-confirm-delete:hover {
  background-color: #c82333;
}

.btn-cancel {
  background-color: #e9ecef;
  color: #343a40;
}

.btn-cancel:hover {
  background-color: #dae0e5;
}

/* Edit Modal Specific Styles */
.edit-item-form {
  text-align: left; /* Align form content left */
}

.edit-item-form .form-group {
    margin-bottom: 15px;
}

.edit-item-form .current-item-image {
    display: block;
    margin-top: 10px;
    max-width: 100px; /* Adjust as needed */
    height: auto;
}

.edit-item-form .hint {
    font-size: 0.85rem;
    color: #6c757d;
    margin-top: 5px;
    display: block;
}

.edit-item-form .modal-actions {
    margin-top: 25px;
}

.btn-confirm {
  background-color: #28a745; /* Bootstrap 'success' color */
  color: white;
}

.btn-confirm:hover {
  background-color: #218838;
}
</style>
