<template>
  <div class="travel-search-page">
    <div class="content-container">
      <section class="map-card-wrapper">
        <div class="map-frame">
          <div id="kakao-map" class="kakao-map"></div>
          
          <!-- 이 지역에서 재검색 플로팅 버튼 -->
          <transition name="fade">
            <button 
              v-if="showSearchInThisArea" 
              class="btn-search-this-area" 
              @click="handleLocationSearch"
            >
              🔄 이 지역에서 재검색
            </button>
          </transition>
          

        </div>

        <BottomSheet v-model:isOpen="isSheetOpen" :peekHeight="peekHeight" maxOpenHeight="80vh">
          <div class="sheet-content">
            <!-- 1. 검색 헤더 (입력창 + 필터 토글) -->
            <div class="search-header-row">
              <div class="search-input-wrapper">
                <input 
                  type="text" 
                  v-model="keyword" 
                  @keyup.enter="searchAttractions"
                  placeholder="관광지 검색 (예: 박물관)"
                  class="compact-input"
                />
                <button @click="searchAttractions" class="icon-search-btn">🔍</button>
              </div>
              <button 
                class="btn-filter-toggle" 
                :class="{ active: isFilterVisible }" 
                @click="isFilterVisible = !isFilterVisible"
              >
                옵션
                <span class="toggle-icon">{{ isFilterVisible ? '▲' : '▼' }}</span>
              </button>
              <button class="btn-close-sheet" @click="isSheetOpen = false" title="닫기">✕</button>
            </div>

            <!-- 2. 상세 필터 영역 (토글) -->
            <div class="filter-collapsible" v-show="isFilterVisible">
              <div class="filter-grid">
                <div class="filter-item">
                  <label>지역</label>
                  <select v-model="selectedArea" @change="handleAreaChange">
                    <option value="">전체</option>
                    <option v-for="area in areas" :key="area.code" :value="area.code">
                      {{ area.name }}
                    </option>
                  </select>
                </div>

                <div class="filter-item" v-if="selectedArea">
                  <label>시군구</label>
                  <select v-model="selectedSigungu">
                    <option value="">전체</option>
                    <option v-for="sigungu in sigunguList" :key="sigungu.code" :value="sigungu.code">
                      {{ sigungu.name }}
                    </option>
                  </select>
                </div>

                <div class="filter-item">
                  <label>카테고리</label>
                  <select v-model="selectedCat1" @change="handleCat1Change">
                    <option value="">전체</option>
                    <option v-for="cat in cat1List" :key="cat.code" :value="cat.code">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>
              </div>
              <button @click="searchAttractions" class="btn-apply-filters">필터 적용 검색</button>
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
        </BottomSheet>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { getAreaBasedList, getSigunguList, getCategoryList, searchKeyword, getLocationBasedList } from '@/api/travel';
import BottomSheet from '@/components/ui/BottomSheet.vue';

const keyword = ref('');
const selectedArea = ref('');
const selectedSigungu = ref('');
const selectedCat1 = ref('');
const selectedCat2 = ref('');
const isFilterVisible = ref(false); // 필터 토글 상태
const showSearchInThisArea = ref(false); // '이 지역에서 검색' 버튼 표시 여부
const searchMode = ref('filter'); // 'filter' (키워드/지역) or 'location' (지도 중심)

const areas = ref([
  { code: '1', name: '서울' },
  { code: '2', name: '인천' },
  { code: '3', name: '대전' },
  { code: '4', name: '대구' },
  { code: '5', name: '광주' },
  { code: '6', name: '부산' },
  { code: '7', name: '울산' },
  { code: '8', name: '세종' },
  { code: '31', name: '경기' },
  { code: '32', name: '강원' },
  { code: '33', name: '충북' },
  { code: '34', name: '충남' },
  { code: '35', name: '경북' },
  { code: '36', name: '경남' },
  { code: '37', name: '전북' },
  { code: '38', name: '전남' },
  { code: '39', name: '제주' }
]);

const sigunguList = ref([]);
const cat1List = ref([]);
const attractions = ref([]);
const currentPage = ref(1);
const isLastPage = ref(false);
const loading = ref(false);
const loadingMore = ref(false);

// BottomSheet 설정
const isSheetOpen = ref(false);
const peekHeight = ref(window.innerWidth >= 769 ? 450 : 120);

let map = null;
let markers = [];
let activeOverlay = null; // 커스텀 오버레이 (PC/Mobile 공용)

// PC 오버레이 닫기 함수 (전역)
window.closeCustomOverlay = () => {
  if (activeOverlay) {
    activeOverlay.setMap(null);
    activeOverlay = null;
  }
};

// 화면 크기 변경 감지하여 peekHeight 업데이트 (Questmap.vue와 동일한 로직)
const updatePeekHeight = () => {
  if (window.innerWidth <= 768) {
    peekHeight.value = 120; // 모바일
  } else {
    peekHeight.value = 450; // 데스크탑
  }
};

// 시군구 목록 조회
const handleAreaChange = async () => {
  selectedSigungu.value = '';
  if (selectedArea.value) {
    try {
      const response = await getSigunguList(selectedArea.value);
      if (response.response?.body?.items?.item) {
        const items = response.response.body.items.item;
        sigunguList.value = Array.isArray(items) ? items : [items];
      }
    } catch (error) {
      console.error('시군구 목록 조회 실패:', error);
    }
  } else {
    sigunguList.value = [];
  }
};

// 대분류 카테고리 조회
const loadCategories = async () => {
  try {
    const response = await getCategoryList();
    if (response.response?.body?.items?.item) {
      const items = response.response.body.items.item;
      cat1List.value = Array.isArray(items) ? items : [items];
    }
  } catch (error) {
    console.error('카테고리 조회 실패:', error);
  }
};

const handleCat1Change = () => {
  // 중분류 로직은 일단 생략 (필요 시 추가)
};

// 관광지 검색
const searchAttractions = async () => {
  currentPage.value = 1;
  isLastPage.value = false;
  attractions.value = [];
  
  // 일반 검색 모드로 설정
  searchMode.value = 'filter';
  
  await fetchAttractions();
  
  if (attractions.value.length > 0) {
    isSheetOpen.value = true;
  }
};

// 이 지역에서 재검색 핸들러
const handleLocationSearch = async () => {
  currentPage.value = 1;
  isLastPage.value = false;
  attractions.value = [];
  searchMode.value = 'location';
  showSearchInThisArea.value = false; // 버튼 숨김
  
  // 기존 마커 제거
  if (markers) {
    markers.forEach(m => m.setMap(null));
  }
  markers = [];
  
  await fetchAttractions();
  
  if (attractions.value.length > 0) {
    isSheetOpen.value = true;
  }
};

// 더보기
const loadMoreAttractions = async () => {
  if (loadingMore.value || isLastPage.value) return;
  currentPage.value++;
  await fetchAttractions(true);
};

// API 호출 공통 함수
const fetchAttractions = async (append = false) => {
  if (append) {
    loadingMore.value = true;
  } else {
    loading.value = true;
  }

  try {
    let response;
    
    // searchMode에 따른 API 호출 분기
    if (searchMode.value === 'location' && map) {
      const center = map.getCenter();
      response = await getLocationBasedList(
        center.getLng().toString(),
        center.getLat().toString(),
        2000,
        currentPage.value
      );
    } else if (keyword.value && keyword.value.trim()) {
      response = await searchKeyword(
        keyword.value.trim(),
        selectedArea.value || null,
        selectedCat1.value || null,
        currentPage.value
      );
    } else {
      response = await getAreaBasedList(
        selectedArea.value || null,
        selectedCat1.value || null,
        currentPage.value
      );
    }
    
    if (response.response?.body?.items?.item) {
      const items = response.response.body.items.item;
      const newAttractions = Array.isArray(items) ? items : [items];
      
      // 추가 모드면 기존 데이터에 append, 아니면 replace
      if (append) {
        attractions.value.push(...newAttractions);
      } else {
        attractions.value = newAttractions;
      }

      // 마지막 페이지 확인
      const numOfRows = response.response?.body?.numOfRows || 10;
      if (newAttractions.length < numOfRows) {
        isLastPage.value = true;
      }
      
      // 지도에 새 마커 표시
      if (map) {
        if (!append) {
             // 기존 마커 정리 (새 검색 시)
             markers.forEach(m => m.setMap(null));
             markers = [];
        }

        newAttractions.forEach(attraction => {
          if (attraction.mapx && attraction.mapy) {
            addMarker(attraction);
          }
        });

        // 지도 이동 (단, '이 지역에서 검색' 모드일 때는 이동하지 않음)
        if (!append && searchMode.value !== 'location' && newAttractions.length > 0 && newAttractions[0].mapx && newAttractions[0].mapy) {
          const firstAttraction = newAttractions[0];
          const moveLatLon = new window.kakao.maps.LatLng(
            firstAttraction.mapy,
            firstAttraction.mapx
          );
          map.setCenter(moveLatLon);
          map.setLevel(6);
        }
      }
    } else {
      isLastPage.value = true;
      if (!append) {
          attractions.value = [];
          markers.forEach(m => m.setMap(null));
          markers = [];
      }
    }
  } catch (error) {
    console.error('관광지 검색 실패:', error);
    if (!append) {
      alert('관광지 검색 중 오류가 발생했습니다.');
    }
  } finally {
    loading.value = false;
    loadingMore.value = false;
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

  window.kakao.maps.event.addListener(marker, 'click', () => {
    // 모든 환경(PC/Mobile)에서 핀 마커 위쪽에 오버레이 표시
    if (activeOverlay) activeOverlay.setMap(null);

    // HTML Content
    const imgUrl = attraction.firstimage || attraction.firstimage2 || '';
    const hasImage = !!imgUrl;
    const content = `
      <div class="pc-custom-overlay">
        <div class="pc-overlay-card">
          <div class="pc-overlay-content-row">
            <div class="pc-overlay-image-wrapper">
              ${hasImage ? `<img src="${imgUrl}" alt="${attraction.title}" />` : '<div class="pc-no-image">📷</div>'}
            </div>
            <div class="pc-overlay-info-wrapper">
               <div class="pc-overlay-header">
                 <h4 class="pc-overlay-title">${attraction.title}</h4>
                 <button class="pc-overlay-close" onclick="window.closeCustomOverlay()">×</button>
               </div>
               <p class="pc-overlay-addr">${attraction.addr1 || ''}</p>
            </div>
          </div>
          <div class="pc-overlay-arrow"></div>
        </div>
      </div>
    `;

    const overlay = new window.kakao.maps.CustomOverlay({
      content: content,
      position: position,
      xAnchor: 0.5, // 수평 중앙
      yAnchor: 1.3, // 마커 위쪽으로 배치 (마커 높이 고려 + 살짝 더 위로)
      zIndex: 300
    });
    
    overlay.setMap(map);
    activeOverlay = overlay;
    
    // 맵 이동 (핀 위쪽 오버레이가 잘 보이도록)
    map.panTo(position);
  });

  markers.push(marker);
};

// 지도에 관광지 표시
const showAttractionOnMap = (attraction) => {
  if (!map || !attraction.mapx || !attraction.mapy) return;

  const moveLatLon = new window.kakao.maps.LatLng(attraction.mapy, attraction.mapx);
  map.setCenter(moveLatLon);
  map.setLevel(3);
  
  // 결과 클릭 시 시트 닫기 (지도 확인을 위해)
  isSheetOpen.value = false;

  const marker = markers.find(m => {
    const pos = m.getPosition();
    const lat = parseFloat(attraction.mapy);
    const lng = parseFloat(attraction.mapx);
    // 부동소수점 오차 고려하여 비교
    return Math.abs(pos.getLat() - lat) < 0.0000001 && Math.abs(pos.getLng() - lng) < 0.0000001;
  });

  if (marker) {
    window.kakao.maps.event.trigger(marker, 'click');
  }
};

const initMap = () => {
  if (window.kakao && window.kakao.maps) {
    window.kakao.maps.load(() => {
        const container = document.getElementById('kakao-map');
        if (!container) return;
        
        container.innerHTML = ''; 
        
        const options = {
          center: new window.kakao.maps.LatLng(37.5665, 126.9780),
          level: 8
        };
        map = new window.kakao.maps.Map(container, options);

        window.kakao.maps.event.addListener(map, 'dragend', () => {
          showSearchInThisArea.value = true;
        });
        window.kakao.maps.event.addListener(map, 'zoom_changed', () => {
          showSearchInThisArea.value = true;
        });
    });
  }
};

onMounted(() => {
  initMap();
  loadCategories();
  window.addEventListener('resize', updatePeekHeight);
  updatePeekHeight();
});

onBeforeUnmount(() => {
  if (markers) {
    markers.forEach(marker => marker.setMap(null));
  }
  window.removeEventListener('resize', updatePeekHeight);
});
</script>

<style scoped>
/* 페이지 레이아웃 */
.travel-search-page {
  font-family: "Pretendard", sans-serif;
  width: 100%;
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fb;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
}

.content-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  min-height: 0;
}

/* 지도 카드 래퍼 */
.map-card-wrapper {
  flex-grow: 1;
  position: relative;
  width: 100%;
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #eef2ff;
  display: flex;
  flex-direction: column;
}

/* 지도 프레임 */
.map-frame {
  flex-grow: 1;
  position: relative;
  overflow: hidden;
}

.kakao-map {
  width: 100%;
  height: 100%;
}

/* Sheet Content */
.sheet-content {
  padding: 24px;
  padding-top: 0;
}

/* Results Section */
.results-section {
  padding-bottom: 20px;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #64748b;
}

.spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #e2e8f0;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.attraction-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.attraction-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.attraction-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #e2e8f0;
}

.attraction-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8fafc;
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
  font-size: 24px;
}

.attraction-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.attraction-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-info .address {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attraction-info .category {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  background: #eff6ff; 
  color: #3b82f6;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  width: fit-content;
  margin-top: 4px;
}

/* --------------- New UI Styles (Mobile Optimization) --------------- */

/* Search Header Row */
.search-header-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input-wrapper {
  flex-grow: 1;
  position: relative;
  display: flex;
  align-items: center;
}

.compact-input {
  width: 100%;
  padding: 12px 16px;
  padding-right: 48px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  font-size: 15px;
  background: #f8fafc;
  transition: all 0.2s;
}

.compact-input:focus {
  background: #fff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
  outline: none;
}

.icon-search-btn {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 8px;
  display: flex; 
  align-items: center;
  justify-content: center;
}

/* Filter Toggle Button */
.btn-filter-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-filter-toggle.active {
  background: #eff6ff;
  color: #3b82f6;
  border-color: #3b82f6;
}

.toggle-icon {
  font-size: 10px;
  margin-left: 2px;
}

/* Collapsible Filter Section */
.filter-collapsible {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #f1f5f9;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); 
  gap: 16px;
  margin-bottom: 16px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.filter-item select {
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 14px;
  background-color: #f8fafc;
  width: 100%;
  box-sizing: border-box;
}

.filter-item select:focus {
  outline: none;
  border-color: #3b82f6;
}

.btn-apply-filters {
  width: 100%;
  padding: 12px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  font-size: 15px;
  transition: background 0.2s;
}

.btn-apply-filters:hover {
  background: #2563eb;
}

/* Floating "Search in this area" Button */
.btn-search-this-area {
  position: absolute;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  padding: 10px 20px;
  background: #fff;
  color: #3b82f6;
  border: 1px solid #3b82f6;
  border-radius: 50px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  white-space: nowrap; 
}

.btn-search-this-area:hover {
  background: #eff6ff;
  transform: translateX(-50%) translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.btn-search-this-area:active {
  transform: translateX(-50%) translateY(0);
}

/* Transition */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Centered/Bottom Modal Styles removed */

/* Load More Button matching Questmap .btn-load-more */
.btn-load-more {
  width: 100%;
  padding: 14px;
  border: none;
  background: #eff6ff; 
  color: #3b82f6;
  font-weight: 600;
  font-size: 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 8px;
}

.btn-load-more:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-load-more:hover:not(:disabled) {
  background: #dbeafe;
}
</style>

<!-- Non-scoped styles for PC Custom Overlay -->
<style>
.pc-custom-overlay {
  /* Kakao Maps handles positioning */
  pointer-events: auto;
}

.pc-overlay-card {
  width: 320px; /* PC는 좀 더 넓게 */
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: row;
  position: relative;
  overflow: visible; /* 화살표 표시 */
  animation: pcPopup 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes pcPopup {
  from { opacity: 0; transform: translateX(10px) scale(0.95); }
  to { opacity: 1; transform: translateX(0) scale(1); }
}

.pc-overlay-content-row {
  display: flex;
  width: 100%;
  overflow: hidden;
  border-radius: 12px; /* 카드 자체 radius */
}

.pc-overlay-image-wrapper {
  width: 100px;
  height: 100px; /* 정사각형 */
  background: #f1f5f9;
  flex-shrink: 0;
}

.pc-overlay-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pc-no-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #cbd5e1;
}

.pc-overlay-info-wrapper {
  flex: 1;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.pc-overlay-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 6px;
}

.pc-overlay-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 8px;
}

.pc-overlay-close {
  width: 20px;
  height: 20px;
  background: none;
  border: none;
  font-size: 20px;
  color: #94a3b8;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.pc-overlay-close:hover {
  color: #64748b;
}

.pc-overlay-addr {
  font-size: 13px;
  color: #64748b;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pc-overlay-arrow {
  position: absolute;
  bottom: -6px;
  left: 50%;
  width: 12px;
  height: 12px;
  background: white;
  transform: translateX(-50%) rotate(45deg);
  border-bottom: 1px solid rgba(0,0,0,0.05);    /* Arrow Bottom */
  border-right: 1px solid rgba(0,0,0,0.05);  /* Arrow Right */
  border-radius: 0 0 4px 0;
  z-index: 10;
}

.btn-close-sheet {
  min-width: 32px;
  height: 32px;
  background: none;
  border: none;
  border-radius: 50%;
  font-size: 20px;
  color: #94a3b8;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0; 
  margin-left: 8px; /* 간격 조금 더 벌림 */
  margin-right: -8px; /* 오른쪽 끝으로 붙이기 위한 음수 마진 */
}

.btn-close-sheet:hover {
  background: #f1f5f9;
  color: #1e293b;
  border: none;
}
</style>
