<template>
  <div class="travel-search-page">
    <div class="content-container">
      <section class="map-card-wrapper">
        <div class="map-frame">
          <div id="kakao-map" class="kakao-map"></div>
        </div>

        <div class="search-panel">
          <div class="panel-header">
            <h2>관광지 검색</h2>
          </div>

          <div class="filter-section">
            <div class="filter-group">
              <label>지역</label>
              <select v-model="selectedArea" @change="handleAreaChange">
                <option value="">전체</option>
                <option v-for="area in areas" :key="area.code" :value="area.code">
                  {{ area.name }}
                </option>
              </select>
            </div>

            <div class="filter-group" v-if="selectedArea">
              <label>시군구</label>
              <select v-model="selectedSigungu">
                <option value="">전체</option>
                <option v-for="sigungu in sigunguList" :key="sigungu.code" :value="sigungu.code">
                  {{ sigungu.name }}
                </option>
              </select>
            </div>

            <div class="filter-group">
              <label>카테고리</label>
              <select v-model="selectedCat1" @change="handleCat1Change">
                <option value="">전체</option>
                <option v-for="cat in cat1List" :key="cat.code" :value="cat.code">
                  {{ cat.name }}
                </option>
              </select>
            </div>

            <button @click="searchAttractions" class="btn-search">검색</button>
          </div>

          <div class="results-section">
            <div v-if="loading" class="loading-state">
              <div class="spinner"></div>
              <p>검색 중...</p>
            </div>

            <div v-else-if="attractions.length === 0" class="empty-state">
              <p>검색 결과가 없습니다.</p>
            </div>

            <div v-else class="attraction-list">
              <div
                v-for="attraction in attractions"
                :key="attraction.contentid"
                class="attraction-card"
                @click="showAttractionOnMap(attraction)"
              >
                <div class="attraction-image">
                  <img
                    v-if="attraction.firstimage"
                    :src="attraction.firstimage"
                    :alt="attraction.title"
                  />
                  <div v-else class="no-image">📷</div>
                </div>
                <div class="attraction-info">
                  <h3>{{ attraction.title }}</h3>
                  <p class="address">{{ attraction.addr1 }}</p>
                  <span class="category">{{ attraction.cat3 || attraction.cat2 || attraction.cat1 }}</span>
                </div>
              </div>

              <button
                v-if="!isLastPage"
                @click="loadMoreAttractions"
                :disabled="loadingMore"
                class="btn-load-more"
              >
                {{ loadingMore ? '로딩 중...' : '더보기' }}
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { getAreaBasedList, getSigunguList, getCategoryList } from '@/api/travel';

const selectedArea = ref('');
const selectedSigungu = ref('');
const selectedCat1 = ref('');
const selectedCat2 = ref('');

const areas = ref([
  { code: '1', name: '서울' },
  { code: '2', name: '인천' },
  { code: '3', name: '대전' },
  { code: '4', name: '대구' },
  { code: '5', name: '광주' },
  { code: '6', name: '부산' },
  { code: '7', name: '울산' },
  { code: '8', name: '세종' },
  { code: '31', name: '경기도' },
  { code: '32', name: '강원도' },
  { code: '33', name: '충청북도' },
  { code: '34', name: '충청남도' },
  { code: '35', name: '경상북도' },
  { code: '36', name: '경상남도' },
  { code: '37', name: '전북특별자치도' },
  { code: '38', name: '전라남도' },
  { code: '39', name: '제주도' }
]);

const sigunguList = ref([]);
const cat1List = ref([]);
const cat2List = ref([]);
const attractions = ref([]);
const loading = ref(false);
const loadingMore = ref(false);
const currentPage = ref(1);
const isLastPage = ref(false);

let map = null;
let markers = [];

// 카카오 지도 초기화
const initMap = () => {
  if (window.kakao && window.kakao.maps) {
    window.kakao.maps.load(() => {
      const container = document.getElementById('kakao-map');
      const options = {
        center: new window.kakao.maps.LatLng(37.5665, 126.9780), // 서울 중심
        level: 8
      };
      map = new window.kakao.maps.Map(container, options);
    });
  }
};

// 지역 변경 시 시군구 로드
const handleAreaChange = async () => {
  selectedSigungu.value = '';
  sigunguList.value = [];
  
  if (selectedArea.value) {
    try {
      const response = await getSigunguList(selectedArea.value);
      if (response.response?.body?.items?.item) {
        const items = response.response.body.items.item;
        sigunguList.value = Array.isArray(items) ? items.map(item => ({
          code: item.code,
          name: item.name
        })) : [{
          code: items.code,
          name: items.name
        }];
      }
    } catch (error) {
      console.error('시군구 조회 실패:', error);
    }
  }
};

// 카테고리 1 변경
const handleCat1Change = async () => {
  selectedCat2.value = '';
  cat2List.value = [];
};

// 카테고리 로드
const loadCategories = async () => {
  try {
    const response = await getCategoryList();
    if (response.response?.body?.items?.item) {
      const items = response.response.body.items.item;
      cat1List.value = Array.isArray(items) ? items.map(item => ({
        code: item.code,
        name: item.name
      })) : [{
        code: items.code,
        name: items.name
      }];
    }
  } catch (error) {
    console.error('카테고리 조회 실패:', error);
  }
};

// 관광지 검색
const searchAttractions = async () => {
  loading.value = true;
  attractions.value = [];
  currentPage.value = 1;
  isLastPage.value = false;
  
  // 기존 마커 제거
  markers.forEach(marker => marker.setMap(null));
  markers = [];

  await fetchAttractions();
  loading.value = false;
};

// 더보기
const loadMoreAttractions = async () => {
  if (isLastPage.value || loadingMore.value) return;
  
  loadingMore.value = true;
  currentPage.value++;
  await fetchAttractions(true);
  loadingMore.value = false;
};

// 관광지 데이터 가져오기
const fetchAttractions = async (append = false) => {
  try {
    const response = await getAreaBasedList(
      selectedArea.value || null,
      selectedCat1.value || null,
      currentPage.value
    );
    
    if (response.response?.body?.items?.item) {
      const items = response.response.body.items.item;
      const newAttractions = Array.isArray(items) ? items : [items];
      
      // 추가 모드면 기존 데이터에 append, 아니면 replace
      if (append) {
        attractions.value.push(...newAttractions);
      } else {
        attractions.value = newAttractions;
      }

      // 마지막 페이지 확인 (응답 개수가 요청한 numOfRows보다 적으면 마지막)
      const numOfRows = response.response?.body?.numOfRows || 10;
      if (newAttractions.length < numOfRows) {
        isLastPage.value = true;
      }
      
      // 지도에 새 마커 표시 (append 모드가 아닐 때만)
      if (map && !append) {
        newAttractions.forEach(attraction => {
          if (attraction.mapx && attraction.mapy) {
            addMarker(attraction);
          }
        });

        // 첫 번째 결과로 지도 이동
        if (newAttractions.length > 0 && newAttractions[0].mapx && newAttractions[0].mapy) {
          const firstAttraction = newAttractions[0];
          const moveLatLon = new window.kakao.maps.LatLng(
            firstAttraction.mapy,
            firstAttraction.mapx
          );
          map.setCenter(moveLatLon);
          map.setLevel(6);
        }
      } else if (map && append) {
        // append 모드일 때는 새로운 항목에만 마커 추가
        newAttractions.forEach(attraction => {
          if (attraction.mapx && attraction.mapy) {
            addMarker(attraction);
          }
        });
      }
    } else {
      isLastPage.value = true;
    }
  } catch (error) {
    console.error('관광지 검색 실패:', error);
    if (!append) {
      alert('관광지 검색 중 오류가 발생했습니다.');
    }
  }
};

// 마커 추가
const addMarker = (attraction) => {
  if (!map || !attraction.mapx || !attraction.mapy) return;

  const position = new window.kakao.maps.LatLng(attraction.mapy, attraction.mapx);
  const marker = new window.kakao.maps.Marker({
    position: position,
    map: map
  });

  // 인포윈도우
  const infowindow = new window.kakao.maps.InfoWindow({
    content: `<div style="padding:5px;font-size:12px;">${attraction.title}</div>`
  });

  window.kakao.maps.event.addListener(marker, 'click', () => {
    infowindow.open(map, marker);
  });

  markers.push(marker);
};

// 지도에 관광지 표시
const showAttractionOnMap = (attraction) => {
  if (!map || !attraction.mapx || !attraction.mapy) return;

  const moveLatLon = new window.kakao.maps.LatLng(attraction.mapy, attraction.mapx);
  map.setCenter(moveLatLon);
  map.setLevel(3);

  // 마커 애니메이션 (bounce effect)
  const marker = markers.find(m => {
    const pos = m.getPosition();
    return pos.getLat() === attraction.mapy && pos.getLng() === attraction.mapx;
  });

  if (marker) {
    // 마커를 클릭한 것처럼 인포윈도우 표시
    window.kakao.maps.event.trigger(marker, 'click');
  }
};

onMounted(() => {
  initMap();
  loadCategories();
});

onBeforeUnmount(() => {
  if (markers) {
    markers.forEach(marker => marker.setMap(null));
  }
});
</script>

<style scoped>
.travel-search-page {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.map-card-wrapper {
  display: flex;
  gap: 20px;
  height: calc(100vh - 40px);
}

.map-frame {
  flex: 1;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.kakao-map {
  width: 100%;
  height: 100%;
}

.search-panel {
  width: 400px;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header h2 {
  margin: 0 0 24px 0;
  font-size: 24px;
  color: #333;
}

.filter-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
}

.filter-group select {
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.filter-group select:focus {
  outline: none;
  border-color: #667eea;
}

.btn-search {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-search:hover {
  transform: translateY(-2px);
}

.btn-search:active {
  transform: translateY(0);
}

.results-section {
  flex: 1;
  overflow-y: auto;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #999;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.attraction-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attraction-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.attraction-card:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.attraction-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.attraction-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ddd;
  font-size: 32px;
}

.attraction-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.attraction-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-info .address {
  margin: 0;
  font-size: 12px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-info .category {
  display: inline-block;
  padding: 2px 8px;
  background: #667eea;
  color: white;
  font-size: 11px;
  border-radius: 4px;
  width: fit-content;
}

.btn-load-more {
  width: 100%;
  padding: 12px;
  margin-top: 12px;
  background: white;
  border: 2px solid #667eea;
  color: #667eea;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-load-more:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.btn-load-more:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .map-card-wrapper {
    flex-direction: column;
    height: auto;
  }

  .map-frame {
    height: 400px;
  }

  .search-panel {
    width: 100%;
    max-height: 600px;
  }
}
</style>
