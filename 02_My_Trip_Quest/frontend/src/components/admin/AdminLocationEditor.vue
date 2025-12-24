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
          <button @click="toggleAddMode" class="btn-add-location" :class="{ active: isAddMode }">
            {{ isAddMode ? '취소' : '+ 관광지 추가' }}
          </button>
        </div>
        
        <!-- Show Existing Locations Toggle -->
        <div class="show-locations-toggle">
          <label class="checkbox-label">
            <input type="checkbox" v-model="showExistingLocations" @change="toggleExistingLocations" />
            기존 관광지 보기
          </label>
        </div>

        <!-- Add Location Form -->
        <div v-if="isAddMode" class="add-location-form">
          <h3>새 관광지 추가</h3>
          
          <div class="form-group">
            <label>관광지명 *</label>
            <input type="text" v-model="newLocation.title" placeholder="관광지 이름 입력" />
          </div>
          
          <div class="form-group">
            <label>지역 *</label>
            <select v-model="newLocation.areaCode">
              <option value="">지역 선택</option>
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
          
          <div class="form-group">
            <label>주소</label>
            <input type="text" v-model="newLocation.address" placeholder="선택사항" />
          </div>
          
          <div class="form-group">
            <label>좌표 (지도 클릭)</label>
            <div class="coords-display" :class="{ empty: !newLocation.latitude }">
              <span v-if="newLocation.latitude">Lat: {{ newLocation.latitude.toFixed(6) }}</span>
              <span v-if="newLocation.longitude">Lng: {{ newLocation.longitude.toFixed(6) }}</span>
              <span v-if="!newLocation.latitude" class="placeholder-text">지도를 클릭하여 위치를 설정하세요</span>
            </div>
          </div>
          
          <div class="form-group">
            <label>인증 범위</label>
            <div class="radius-control">
              <input type="range" v-model.number="newLocation.gpsVerifyRadius" 
                     min="50" max="1000" step="10" @input="updateTempCircle" />
              <input type="number" v-model.number="newLocation.gpsVerifyRadius" 
                     min="50" max="1000" @input="updateTempCircle" />
              <span>미터</span>
            </div>
          </div>
          
          <div class="form-group">
            <label>생성할 퀘스트</label>
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="newLocation.generateArrival" />
                도착 퀘스트
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="newLocation.generatePhoto" />
                사진 퀘스트
              </label>
            </div>
          </div>
          
          <div class="form-actions">
            <button @click="exitAddMode" class="btn-cancel">취소</button>
            <button @click="saveNewLocation" class="btn-save" :disabled="saving">
              {{ saving ? '저장 중...' : '저장' }}
            </button>
          </div>
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
import { getDbLocations, updateLocation, createLocation } from '@/api/admin';

const selectedAreaCode = ref('');
const locations = ref([]);
const selectedLocation = ref(null);
const loading = ref(false);
const saving = ref(false);

const editedLat = ref(0);
const editedLng = ref(0);
const editedRadius = ref(100);

// Add Location Mode
const isAddMode = ref(false);
const showExistingLocations = ref(false);
const existingMarkers = [];
const newLocation = ref({
  title: '',
  areaCode: '',
  address: '',
  latitude: 0,
  longitude: 0,
  gpsVerifyRadius: 150,
  generateArrival: true,
  generatePhoto: true
});

let map = null;
let marker = null;
let circle = null;
let tempMarker = null;
let geocoder = null;

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
  
  // Add map click event for Add Location mode
  kakao.maps.event.addListener(map, 'click', handleMapClick);
  
  // Initialize Geocoder
  geocoder = new kakao.maps.services.Geocoder();
  
  // console.log('Admin map initialized successfully');
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
    // console.log('Fetched locations:', locations.value.length);
  } catch (error) {
    console.error('Failed to fetch locations:', error);
    alert('관광지 목록을 불러오는데 실패했습니다.');
  } finally {
    loading.value = false;
  }
};

// Watch locations to update existing markers when area changes
watch(locations, () => {
  // Center map on first location and zoom to level 8 (always)
  if (locations.value.length > 0) {
    const firstLocation = locations.value[0];
    const centerPosition = new kakao.maps.LatLng(firstLocation.latitude, firstLocation.longitude);
    map.setCenter(centerPosition);
    map.setLevel(8);
  }
  
  // Update markers only if checkbox is enabled
  if (showExistingLocations.value) {
    // Clear existing markers
    existingMarkers.forEach(m => m.setMap(null));
    existingMarkers.length = 0;
    
    // Add markers for new locations
    locations.value.forEach(location => {
      const position = new kakao.maps.LatLng(location.latitude, location.longitude);
      
      const existingMarker = new kakao.maps.Marker({
        position: position,
        map: map
      });
      
      // Create info window with location name
      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:12px;">${location.title}</div>`
      });
      
      kakao.maps.event.addListener(existingMarker, 'mouseover', function() {
        infowindow.open(map, existingMarker);
      });
      
      kakao.maps.event.addListener(existingMarker, 'mouseout', function() {
        infowindow.close();
      });
      
      existingMarkers.push(existingMarker);
    });
  }
});

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
  map.setLevel(3);
  
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

// Toggle Add Location Mode
const toggleAddMode = () => {
  isAddMode.value = !isAddMode.value;
  
  if (!isAddMode.value) {
    // Exit add mode
    exitAddMode();
  } else {
    // Enter add mode
    selectedLocation.value = null;
    if (marker) marker.setMap(null);
    if (circle) circle.setMap(null);
  }
};

// Search address from coordinates
const searchDetailAddrFromCoords = (coords, callback) => {
  if (!geocoder) return;
  
  geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
};

// Map region name to area code
const getAreaCodeFromRegion = (regionName) => {
  if (!regionName) return '';
  if (regionName.includes('서울')) return '1';
  if (regionName.includes('인천')) return '2';
  if (regionName.includes('대전')) return '3';
  if (regionName.includes('대구')) return '4';
  if (regionName.includes('광주')) return '5';
  if (regionName.includes('부산')) return '6';
  if (regionName.includes('울산')) return '7';
  if (regionName.includes('세종')) return '8';
  if (regionName.includes('경기')) return '31';
  if (regionName.includes('강원')) return '32';
  if (regionName.includes('충북') || regionName.includes('충청북도')) return '33';
  if (regionName.includes('충남') || regionName.includes('충청남도')) return '34';
  if (regionName.includes('경북') || regionName.includes('경상북도')) return '35';
  if (regionName.includes('경남') || regionName.includes('경상남도')) return '36';
  if (regionName.includes('전북') || regionName.includes('전라북도')) return '37';
  if (regionName.includes('전남') || regionName.includes('전라남도')) return '38';
  if (regionName.includes('제주')) return '39';
  return '';
};

// Handle map click in Add Mode
const handleMapClick = (event) => {
  if (!isAddMode.value) return;
  
  const latlng = event.latLng;
  const lat = latlng.getLat();
  const lng = latlng.getLng();
  
  // Remove existing temp marker and circle
  if (tempMarker) tempMarker.setMap(null);
  if (circle) circle.setMap(null);
  
  // Create temporary marker
  tempMarker = new kakao.maps.Marker({
    position: latlng,
    draggable: true
  });
  tempMarker.setMap(map);
  
  // Create circle
  circle = new kakao.maps.Circle({
    center: latlng,
    radius: newLocation.value.gpsVerifyRadius,
    strokeWeight: 2,
    strokeColor: '#10b981',
    strokeOpacity: 0.8,
    fillColor: '#10b981',
    fillOpacity: 0.2
  });
  circle.setMap(map);
  
  // Update coordinates
  newLocation.value.latitude = lat;
  newLocation.value.longitude = lng;
  
  // Search address
  searchDetailAddrFromCoords(latlng, function(result, status) {
    if (status === kakao.maps.services.Status.OK) {
      if (result[0].road_address) {
        newLocation.value.address = result[0].road_address.address_name;
      } else {
        newLocation.value.address = result[0].address.address_name;
      }
      
      // Auto-select area code
      const regionName = result[0].address.region_1depth_name;
      const code = getAreaCodeFromRegion(regionName);
      if (code) {
        newLocation.value.areaCode = code;
      }
    }
  });
  
  // Center map
  map.setCenter(latlng);
  map.setLevel(3);
  
  // Listen to marker drag
  kakao.maps.event.addListener(tempMarker, 'dragend', function() {
    const pos = tempMarker.getPosition();
    newLocation.value.latitude = pos.getLat();
    newLocation.value.longitude = pos.getLng();
    circle.setPosition(pos);
    
    // Update address on drag end
    searchDetailAddrFromCoords(pos, function(result, status) {
      if (status === kakao.maps.services.Status.OK) {
        if (result[0].road_address) {
          newLocation.value.address = result[0].road_address.address_name;
        } else {
          newLocation.value.address = result[0].address.address_name;
        }
        
        // Auto-select area code
        const regionName = result[0].address.region_1depth_name;
        const code = getAreaCodeFromRegion(regionName);
        if (code) {
          newLocation.value.areaCode = code;
        }
      }
    });
  });
};

// Update temp circle radius
const updateTempCircle = () => {
  if (circle && isAddMode.value) {
    circle.setRadius(newLocation.value.gpsVerifyRadius);
  }
};

// Save new location
const saveNewLocation = async () => {
  if (!newLocation.value.title || !newLocation.value.areaCode) {
    alert('관광지명과 지역을 입력해주세요.');
    return;
  }
  
  if (!newLocation.value.latitude || !newLocation.value.longitude) {
    alert('지도를 클릭하여 위치를 선택해주세요.');
    return;
  }
  
  const questTypes = [];
  if (newLocation.value.generateArrival) questTypes.push('ARRIVAL');
  if (newLocation.value.generatePhoto) questTypes.push('PHOTO');
  
  if (questTypes.length === 0) {
    alert('최소 하나의 퀘스트를 선택해주세요.');
    return;
  }
  
  saving.value = true;
  try {
    await createLocation({
      title: newLocation.value.title,
      areaCode: newLocation.value.areaCode,
      address: newLocation.value.address,
      latitude: newLocation.value.latitude,
      longitude: newLocation.value.longitude,
      gpsVerifyRadius: newLocation.value.gpsVerifyRadius,
      questTypes: questTypes
    });
    
    alert('관광지가 생성되었습니다!');
    exitAddMode();
    
    // Refresh list if same area
    if (selectedAreaCode.value === newLocation.value.areaCode) {
      fetchLocations();
    }
  } catch (error) {
    console.error('Failed to create location:', error);
    alert('관광지 생성에 실패했습니다.');
  } finally {
    saving.value = false;
  }
};

// Toggle showing existing locations on map
const toggleExistingLocations = async () => {
  if (showExistingLocations.value) {
    // Show all existing locations on map
    if (locations.value.length === 0 && selectedAreaCode.value) {
      // Fetch locations if not already loaded
      await fetchLocations();
    }
    
    // Clear existing markers
    existingMarkers.forEach(m => m.setMap(null));
    existingMarkers.length = 0;
    
    // Add markers for all locations
    locations.value.forEach(location => {
      const position = new kakao.maps.LatLng(location.latitude, location.longitude);
      
      const existingMarker = new kakao.maps.Marker({
        position: position,
        map: map
      });
      
      // Create info window with location name
      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:12px;">${location.title}</div>`
      });
      
      kakao.maps.event.addListener(existingMarker, 'mouseover', function() {
        infowindow.open(map, existingMarker);
      });
      
      kakao.maps.event.addListener(existingMarker, 'mouseout', function() {
        infowindow.close();
      });
      
      existingMarkers.push(existingMarker);
    });
  } else {
    // Hide existing markers
    existingMarkers.forEach(m => m.setMap(null));
    existingMarkers.length = 0;
  }
};

// Exit Add Mode
const exitAddMode = () => {
  isAddMode.value = false;
  showExistingLocations.value = false;
  
  if (tempMarker) tempMarker.setMap(null);
  if (circle) circle.setMap(null);
  tempMarker = null;
  
  // Clear existing markers
  existingMarkers.forEach(m => m.setMap(null));
  existingMarkers.length = 0;
  
  // Reset form
  newLocation.value = {
    title: '',
    areaCode: '',
    address: '',
    latitude: 0,
    longitude: 0,
    gpsVerifyRadius: 150,
    generateArrival: true,
    generatePhoto: true
  };
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

/* Add Location Button */
.btn-add-location {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.btn-add-location:hover {
  background: #059669;
}

.btn-add-location.active {
  background: #ef4444;
}

.btn-add-location.active:hover {
  background: #dc2626;
}

/* Show Locations Toggle */
.show-locations-toggle {
  padding: 12px 16px;
  background: #f0fdf4;
  border-radius: 6px;
  margin-top: 12px;
  border: 1px solid #bbf7d0;
}

.show-locations-toggle .checkbox-label {
  margin: 0;
  color: #166534;
  font-weight: 600;
}


/* Add Location Form */
.add-location-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
  border: 2px solid #10b981;
}

.add-location-form h3 {
  margin: 0 0 16px 0;
  color: #10b981;
  font-size: 18px;
}

.add-location-form input[type="text"],
.add-location-form input[type="number"],
.add-location-form select {
  width: 100%;
  max-width: 360px; /* 모든 입력란 너비 통일 및 제한 */
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s ease;
  box-sizing: border-box; /* 패딩 포함 너비 계산 */
}

.add-location-form input[type="text"]:focus,
.add-location-form input[type="number"]:focus,
.add-location-form select:focus {
  outline: none;
  border-color: #10b981;
}

.coords-display {
  display: flex;
  gap: 16px;
  padding: 8px 12px;
  background: #f3f4f6;
  border-radius: 6px;
  font-size: 14px;
  color: #374151;
  font-family: monospace;
  min-height: 36px;
  align-items: center;
}

.coords-display.empty {
  background: #fff1f2;
  border: 1px dashed #f43f5e;
  justify-content: center;
}

.placeholder-text {
  color: #f43f5e;
  font-weight: 500;
  font-family: 'Pretendard', sans-serif;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  color: #374151;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
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
