<template>
  <div class="admin-location-editor">
    <div class="editor-container">
      <!-- Left: Interactive Map -->
      <div class="map-section">
        <div id="admin-map" style="width: 100%; height: 100%;"></div>
      </div>
      
      <!-- Right: Quest List + Edit Form -->
      <div class="list-section">
        <!-- Area Selector -->
        <div class="area-selector">
          <label>지역 선택:</label>
          <select v-model="selectedAreaCode" @change="fetchLocations">
            <option value="">지역을 선택하세요</option>
            <option value="1">서울</option>
            <option value="2">인천</option>
            <option value="3">대전</option>
            <option value="4">대구</option>
            <option value="5">광주</option>
            <option value="6">부산</option>
            <option value="7">울산</option>
            <option value="8">세종</option>
            <option value="31">경기</option>
            <option value="32">강원</option>
            <option value="33">충북</option>
            <option value="34">충남</option>
            <option value="35">경북</option>
            <option value="36">경남</option>
            <option value="37">전북</option>
            <option value="38">전남</option>
            <option value="39">제주</option>
          </select>
        </div>

        <!-- Quest List -->
        <div class="quest-list">
          <div v-if="loading" class="loading">로딩 중...</div>
          <div v-else-if="locations.length === 0" class="empty">지역을 선택하세요</div>
          <div
            v-else
            v-for="location in locations"
            :key="location.locationId"
            :class="['quest-item', { selected: selectedLocation?.locationId === location.locationId }]"
            @click="selectLocation(location)"
          >
            <div class="quest-title">{{ location.title }}</div>
            <div class="quest-info">
              <span>{{ location.latitude?.toFixed(6) }}, {{ location.longitude?.toFixed(6) }}</span>
              <span class="radius-badge">{{ location.gpsVerifyRadius }}m</span>
            </div>
          </div>
        </div>

        <!-- Edit Form -->
        <div v-if="selectedLocation" class="edit-form">
          <h3>위치 편집</h3>
          <div class="form-group">
            <label>관광지명</label>
            <input type="text" :value="selectedLocation.title" readonly />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>위도 (Latitude)</label>
              <input type="number" v-model.number="editedLat" step="0.000001" @input="updateMarkerPosition" />
            </div>
            <div class="form-group">
              <label>경도 (Longitude)</label>
              <input type="number" v-model.number="editedLng" step="0.000001" @input="updateMarkerPosition" />
            </div>
          </div>
          <div class="form-group">
            <label>인증 범위 (Radius)</label>
            <div class="radius-control">
              <input 
                type="range" 
                v-model.number="editedRadius" 
                min="50" 
                max="1000" 
                step="10"
                @input="updateCircleRadius"
              />
              <input 
                type="number" 
                v-model.number="editedRadius" 
                min="50" 
                max="1000"
                @input="updateCircleRadius"
              />
              <span>미터</span>
            </div>
          </div>
          <div class="form-actions">
            <button @click="cancelEdit" class="btn-cancel">취소</button>
            <button @click="saveChanges" class="btn-save" :disabled="saving">
              {{ saving ? '저장 중...' : '저장' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { getDbLocations, updateLocation } from '@/api/admin';

const selectedAreaCode = ref('');
const locations = ref([]);
const selectedLocation = ref(null);
const loading = ref(false);
const saving = ref(false);

const editedLat = ref(0);
const editedLng = ref(0);
const editedRadius = ref(100);

let map = null;
let marker = null;
let circle = null;

// Initialize Kakao Map
const initMap = () => {
  const container = document.getElementById('admin-map');
  if (!container) {
    console.error('Map container not found');
    return;
  }

  const options = {
    center: new kakao.maps.LatLng(36.5, 127.5),
    level: 13
  };
  
  map = new kakao.maps.Map(container, options);
  
  // Add zoom control
  const zoomControl = new kakao.maps.ZoomControl();
  map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
  
  console.log('Admin map initialized successfully');
};

// Fetch locations for selected area
const fetchLocations = async () => {
  if (!selectedAreaCode.value) {
    locations.value = [];
    return;
  }
  
  loading.value = true;
  try {
    const response = await getDbLocations({
      areaCode: selectedAreaCode.value,
      page: 0,
      size: 100
    });
    locations.value = response.data.data.content || [];
    console.log('Fetched locations:', locations.value.length);
  } catch (error) {
    console.error('Failed to fetch locations:', error);
    alert('관광지 목록을 불러오는데 실패했습니다.');
  } finally {
    loading.value = false;
  }
};

// Select location and show on map
const selectLocation = (location) => {
  selectedLocation.value = location;
  editedLat.value = parseFloat(location.latitude);
  editedLng.value = parseFloat(location.longitude);
  editedRadius.value = location.gpsVerifyRadius || 100;
  
  showLocationOnMap(location);
};

// Show location on map with draggable marker and circle
const showLocationOnMap = (location) => {
  if (!map) return;
  
  const position = new kakao.maps.LatLng(location.latitude, location.longitude);
  
  // Remove existing marker and circle
  if (marker) marker.setMap(null);
  if (circle) circle.setMap(null);
  
  // Create draggable marker
  marker = new kakao.maps.Marker({
    position: position,
    draggable: true
  });
  marker.setMap(map);
  
  // Create circle
  circle = new kakao.maps.Circle({
    center: position,
    radius: location.gpsVerifyRadius || 100,
    strokeWeight: 2,
    strokeColor: '#ef4444',
    strokeOpacity: 0.8,
    fillColor: '#ef4444',
    fillOpacity: 0.2
  });
  circle.setMap(map);
  
  // Center map on location
  map.setCenter(position);
  map.setLevel(6);
  
  // Listen to marker drag
  kakao.maps.event.addListener(marker, 'dragend', function() {
    const pos = marker.getPosition();
    editedLat.value = pos.getLat();
    editedLng.value = pos.getLng();
    circle.setPosition(pos);
  });
};

// Update marker position when coordinates change
const updateMarkerPosition = () => {
  if (!marker || !circle) return;
  
  const newPosition = new kakao.maps.LatLng(editedLat.value, editedLng.value);
  marker.setPosition(newPosition);
  circle.setPosition(newPosition);
  map.setCenter(newPosition);
};

// Update circle radius when slider changes
const updateCircleRadius = () => {
  if (!circle) return;
  circle.setRadius(editedRadius.value);
};

// Save changes to backend
const saveChanges = async () => {
  if (!selectedLocation.value) return;
  
  saving.value = true;
  try {
    await updateLocation(selectedLocation.value.locationId, {
      latitude: editedLat.value,
      longitude: editedLng.value,
      gpsVerifyRadius: editedRadius.value
    });
    
    alert('저장되었습니다!');
    
    // Update local data
    const index = locations.value.findIndex(l => l.locationId === selectedLocation.value.locationId);
    if (index !== -1) {
      locations.value[index].latitude = editedLat.value;
      locations.value[index].longitude = editedLng.value;
      locations.value[index].gpsVerifyRadius = editedRadius.value;
    }
  } catch (error) {
    console.error('Failed to save:', error);
    alert('저장에 실패했습니다.');
  } finally {
    saving.value = false;
  }
};

// Cancel edit
const cancelEdit = () => {
  if (selectedLocation.value) {
    editedLat.value = parseFloat(selectedLocation.value.latitude);
    editedLng.value = parseFloat(selectedLocation.value.longitude);
    editedRadius.value = selectedLocation.value.gpsVerifyRadius || 100;
    updateMarkerPosition();
    updateCircleRadius();
  }
};

onMounted(() => {
  // Wait for Kakao Maps API to be fully loaded
  if (window.kakao && window.kakao.maps) {
    kakao.maps.load(() => {
      initMap();
    });
  } else {
    console.error('Kakao Maps API not loaded');
    alert('지도 API가 로드되지 않았습니다. 페이지를 새로고침해주세요.');
  }
});
</script>

<style scoped>
.admin-location-editor {
  height: calc(100vh - 200px);
  min-height: 600px;
}

.editor-container {
  display: flex;
  gap: 20px;
  height: 100%;
}

.map-section {
  flex: 1;
  min-width: 0;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.list-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
}

.area-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.area-selector label {
  font-weight: 600;
  color: #374151;
}

.area-selector select {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.quest-list {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
  max-height: 300px;
}

.quest-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quest-item:hover {
  background: #f9fafb;
}

.quest-item.selected {
  background: #eff6ff;
  border-left: 4px solid #3b82f6;
}

.quest-title {
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.quest-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #6b7280;
}

.radius-badge {
  background: #dbeafe;
  color: #1e40af;
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 600;
}

.edit-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.edit-form h3 {
  margin: 0 0 16px 0;
  color: #1f2937;
  font-size: 18px;
}

.form-group {
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-group input[type="text"],
.form-group input[type="number"] {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.form-group input[readonly] {
  background: #f9fafb;
  color: #6b7280;
}

.radius-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.radius-control input[type="range"] {
  flex: 1;
}

.radius-control input[type="number"] {
  width: 80px;
  padding: 6px 8px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.btn-cancel,
.btn-save {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel {
  background: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-save {
  background: #3b82f6;
  color: white;
}

.btn-save:hover:not(:disabled) {
  background: #2563eb;
}

.btn-save:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading,
.empty {
  padding: 40px 20px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

/* Responsive */
@media (max-width: 768px) {
  .editor-container {
    flex-direction: column;
  }
  
  .map-section {
    height: 50vh;
  }
  
  .list-section {
    height: auto;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
