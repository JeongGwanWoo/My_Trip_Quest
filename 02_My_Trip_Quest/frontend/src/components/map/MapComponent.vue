<template>
  <div style="position: relative; width: 100%; height: 100%;">
    <div id="map" style="width: 100%; height: 100%;"></div>
    <button @click="resetMap" class="map-reset-btn" title="초기 위치로">
      ↻
    </button>
    <button @click="findMyLocation" class="map-locate-btn" title="내 위치 찾기">
      <i class="fa-solid fa-location-crosshairs"></i>
    </button>
  </div>
</template>

<script setup>
import { onMounted, defineProps, watch, defineEmits } from "vue";

// 줌 레벨 상수 정의
const BASE_ZOOM_LEVEL = 13;
const MARKER_VISIBLE_LEVEL = 10; // 기본(13) - 3
const CIRCLE_VISIBLE_LEVEL = 8;  // 기본(13) - 5

// 부모 컴포넌트로부터 areas 데이터를 받기 위한 props 정의
const props = defineProps({
  areas: {
    type: Array,
    required: true,
  },
  locations: {
    type: Array,
    default: () => [],
  },
});

let map = null; // 지도 인스턴스를 저장할 변수
let markers = []; // 마커(오버레이) 인스턴스를 저장할 배열
let locationMarkers = []; // 관광지 마커 인스턴스를 저장할 배열
let currentZoomLevel = 13; // 현재 줌 레벨
let currentCircle = null; // 현재 표시된 범위 원
let selectedLocationId = null; // 선택된 관광지 ID
let lastClickTime = 0; // 마지막 클릭 시간
let userLocationMarker = null; // 사용자 위치 마커

// 부모 컴포넌트로 이벤트를 보내기 위한 emit 함수 정의
const emit = defineEmits(['area-clicked', 'location-clicked', 'map-reset']);

// areas 데이터가 변경될 때 호출될 함수
const displayAreaMarkers = (newAreas) => {
  if (!map) return;

  // 기존에 생성된 마커(오버레이)들을 지도에서 모두 제거
  markers.forEach(marker => marker.setMap(null));
  markers = []; // 배열 초기화

  if (!newAreas || newAreas.length === 0) {
    return;
  }

  const geocoder = new kakao.maps.services.Geocoder();

  newAreas.forEach((area) => {
    geocoder.addressSearch(area.areaName, function (result, status) {
      if (status === kakao.maps.services.Status.OK) {
        const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

        const contentEl = document.createElement('div');
        contentEl.className = 'custom-marker';
        
        // 퀘스트 상태에 따라 핀 색상 결정
        let pinColorClass;
        switch (area.status) {
          case 'COMPLETED':
            pinColorClass = 'quest-pin-completed';
            break;
          case 'IN_PROGRESS':
            pinColorClass = 'quest-pin-inprogress';
            break;
          default:
            pinColorClass = 'quest-pin-not-started';
            break;
        }

        contentEl.innerHTML = `
          <div class="pin-body ${pinColorClass}"><span class="pin-text">●</span></div>
          <div class="pin-tail"></div>
          <div class="pin-tooltip">${area.areaName}</div>
        `;

        // 롱프레스 및 hover 이벤트 처리
        let longPressTimer = null;
        let isLongPress = false;
        const tooltip = contentEl.querySelector('.pin-tooltip');

        // 데스크톱: hover 이벤트
        contentEl.addEventListener('mouseenter', () => {
          tooltip.classList.add('visible');
        });

        contentEl.addEventListener('mouseleave', () => {
          tooltip.classList.remove('visible');
        });

        // 모바일: 롱프레스 이벤트 (500ms)
        contentEl.addEventListener('touchstart', (e) => {
          isLongPress = false;
          longPressTimer = setTimeout(() => {
            isLongPress = true;
            tooltip.classList.add('visible');
            // 3초 후 자동으로 사라짐
            setTimeout(() => {
              tooltip.classList.remove('visible');
            }, 3000);
          }, 500); // 500ms 길게 누르기
        });

        contentEl.addEventListener('touchend', (e) => {
          clearTimeout(longPressTimer);
          // 롱프레스가 아니었다면 클릭 이벤트 발생
          if (!isLongPress) {
            emit('area-clicked', area.areaCode);
          }
        });

        contentEl.addEventListener('touchmove', () => {
          clearTimeout(longPressTimer);
        });

        // 데스크톱: 클릭 이벤트 - 지역으로 줌인
        contentEl.addEventListener('click', () => {
          // 해당 지역 중심으로 지도 이동 및 줌인
          map.setCenter(coords);
          map.setLevel(MARKER_VISIBLE_LEVEL - 1); // 관광지가 보이는 레벨(9)로 확대
          emit('area-clicked', area.areaCode);
        });

        const customOverlay = new kakao.maps.CustomOverlay({
          position: coords,
          content: contentEl,
          clickable: true,
          yAnchor: 1.3, 
        });

        customOverlay.setMap(map);
        markers.push(customOverlay); // 생성된 오버레이를 배열에 추가하여 관리

      } else {
        console.error(`'${area.areaName}' 주소에 대한 지오코딩 실패:`, status);
      }
    });
  });
};

// 관광지 마커를 표시하는 함수
const displayLocationMarkers = (locations) => {
  if (!map) return;

  // 기존 관광지 마커 제거
  locationMarkers.forEach(marker => marker.setMap(null));
  locationMarkers = [];

  if (!locations || locations.length === 0) {
    return;
  }

  locations.forEach((location) => {
    // 위도/경도가 있는 경우에만 마커 표시
    if (location.latitude && location.longitude) {
      const coords = new kakao.maps.LatLng(location.latitude, location.longitude);

      const contentEl = document.createElement('div');
      contentEl.className = 'custom-marker location-marker';

      // 퀘스트 상태에 따라 핀 색상 결정
      let pinColorClass;
      switch (location.status) {
        case 'COMPLETED':
          pinColorClass = 'quest-pin-completed';
          break;
        case 'IN_PROGRESS':
          pinColorClass = 'quest-pin-inprogress';
          break;
        default:
          pinColorClass = 'quest-pin-not-started';
          break;
      }

      const pinTextEl = document.createElement('span');
      pinTextEl.className = 'pin-text';
      pinTextEl.textContent = '●';

      const pinBodyEl = document.createElement('div');
      pinBodyEl.className = `pin-body ${pinColorClass}`;
      pinBodyEl.appendChild(pinTextEl);

      const pinTailEl = document.createElement('div');
      pinTailEl.className = 'pin-tail';

      const tooltipEl = document.createElement('div');
      tooltipEl.className = 'pin-tooltip';
      tooltipEl.textContent = location.title;

      contentEl.innerHTML = '';
      contentEl.appendChild(pinBodyEl);
      contentEl.appendChild(pinTailEl);
      contentEl.appendChild(tooltipEl);

      // 마커 참조 저장 (아이콘 업데이트용)
      contentEl._pinTextEl = pinTextEl;
      contentEl._locationId = location.locationId;

      // 롱프레스 및 hover 이벤트 처리
      let longPressTimer = null;
      let isLongPress = false;
      const tooltip = contentEl.querySelector('.pin-tooltip');

      // 데스크톱: hover 이벤트
      contentEl.addEventListener('mouseenter', () => {
        tooltip.classList.add('visible');
      });

      contentEl.addEventListener('mouseleave', () => {
        tooltip.classList.remove('visible');
      });

      // 모바일: 롱프레스 이벤트 (500ms)
      contentEl.addEventListener('touchstart', (e) => {
        isLongPress = false;
        longPressTimer = setTimeout(() => {
          isLongPress = true;
          tooltip.classList.add('visible');
          setTimeout(() => {
            tooltip.classList.remove('visible');
          }, 3000);
        }, 500);
      });

      contentEl.addEventListener('touchend', (e) => {
        clearTimeout(longPressTimer);
        if (!isLongPress) {
          e.stopPropagation();
          
          const now = Date.now();
          const timeSinceLastClick = now - lastClickTime;
          
          console.log('Marker clicked:', location.locationId, 'Selected:', selectedLocationId, 'Circle visible:', currentCircle?.getMap() ? 'yes' : 'no', 'Time since last click:', timeSinceLastClick);
          
          // 너무 빠른 연속 클릭 방지 (300ms 이내)
          if (timeSinceLastClick < 300) {
            console.log('Click ignored (too fast)');
            return;
          }
          
          lastClickTime = now;
          
          // 2단계 클릭 로직
          if (selectedLocationId === location.locationId && currentCircle && currentCircle.getMap()) {
            console.log('Opening modal (2nd click)');
            map.panTo(coords); // 부드럽게 이동 (확대 없음)
            emit('location-clicked', location);
          } else {
            console.log('Showing circle (1st click or circle hidden)');
            map.panTo(coords); // 부드럽게 이동 (확대 없음)
            showVerificationCircle(location);
            updateMarkerIcons(); // 마커 아이콘 업데이트
          }
        }
      });

      contentEl.addEventListener('touchmove', () => {
        clearTimeout(longPressTimer);
      });

      // 데스크톱: 클릭 이벤트 - 관광지 퀘스트 모달 열기
      contentEl.addEventListener('click', (e) => {
        e.stopPropagation();
        
        const now = Date.now();
        const timeSinceLastClick = now - lastClickTime;
        
        console.log('Marker clicked:', location.locationId, 'Selected:', selectedLocationId, 'Circle visible:', currentCircle?.getMap() ? 'yes' : 'no', 'Time since last click:', timeSinceLastClick);
        
        // 너무 빠른 연속 클릭 방지 (300ms 이내)
        if (timeSinceLastClick < 300) {
          console.log('Click ignored (too fast)');
          return;
        }
        
        lastClickTime = now;
        
        // 2단계 클릭 로직
        // 2단계 클릭 로직
        if (selectedLocationId === location.locationId && currentCircle && currentCircle.getMap()) {
          console.log('Opening modal (2nd click)');
          map.panTo(coords); // 부드럽게 이동 (확대 없음)
          emit('location-clicked', location);
        } else {
          console.log('Showing circle (1st click or circle hidden)');
          map.panTo(coords); // 부드럽게 이동 (확대 없음)
          showVerificationCircle(location);
        }
      });

      const customOverlay = new kakao.maps.CustomOverlay({
        position: coords,
        content: contentEl,
        clickable: true,
        yAnchor: 1.3,
      });

      customOverlay.setMap(map);
      locationMarkers.push(customOverlay);
    }
  });
};

// 인증 범위 원을 표시하는 함수
const showVerificationCircle = (location) => {
  // 기존 원 제거
  removeVerificationCircle();

  if (!location.gpsVerifyRadius || !location.latitude || !location.longitude) {
    console.warn('Location missing radius or coordinates:', location);
    return;
  }

  // 새 원 생성
  const circle = new kakao.maps.Circle({
    center: new kakao.maps.LatLng(location.latitude, location.longitude),
    radius: location.gpsVerifyRadius, // 미터 단위
    strokeWeight: 2,
    strokeColor: '#3b82f6',
    strokeOpacity: 0.8,
    strokeStyle: 'solid',
    fillColor: '#3b82f6',
    fillOpacity: 0.15
  });

  circle.setMap(map);
  currentCircle = circle;
  selectedLocationId = location.locationId;
};

// 인증 범위 원을 제거하는 함수
const removeVerificationCircle = () => {
  console.log('Removing circle, selectedLocationId:', selectedLocationId);
  if (currentCircle) {
    currentCircle.setMap(null);
    currentCircle = null;
  }
  selectedLocationId = null;
  console.log('Circle removed, selectedLocationId now:', selectedLocationId);
  updateMarkerIcons(); // 마커 아이콘 업데이트
};

// 마커 아이콘을 업데이트하는 함수
const updateMarkerIcons = () => {
  console.log('updateMarkerIcons called, selectedLocationId:', selectedLocationId, 'circle visible:', currentCircle?.getMap() ? 'yes' : 'no');
  console.log('locationMarkers count:', locationMarkers.length);
  
  locationMarkers.forEach(marker => {
    const contentEl = marker.getContent();
    console.log('Checking marker, has _pinTextEl:', !!contentEl?._pinTextEl, '_locationId:', contentEl?._locationId);
    
    if (contentEl && contentEl._pinTextEl && contentEl._locationId) {
      if (contentEl._locationId === selectedLocationId && currentCircle && currentCircle.getMap()) {
        // 선택된 마커: Q 아이콘 (모달 열기 가능 상태)
        console.log('Setting Q icon for location:', contentEl._locationId);
        contentEl._pinTextEl.textContent = 'Q';
      } else {
        // 선택되지 않은 마커: ● 아이콘
        contentEl._pinTextEl.textContent = '●';
      }
    }
  });
};

// 지도를 초기 상태로 리셋하는 함수
const resetMap = () => {
  if (!map) return;
  
  // 범위 원 제거
  removeVerificationCircle();
  
  // 관광지 마커 제거
  locationMarkers.forEach(marker => marker.setMap(null));
  locationMarkers = [];
  
  // 모든 툴팁 숨기기 (즉시 + 지연 실행으로 확실하게)
  document.querySelectorAll('.pin-tooltip').forEach(tooltip => {
    tooltip.classList.remove('visible');
  });
  
  setTimeout(() => {
    document.querySelectorAll('.pin-tooltip').forEach(tooltip => {
      tooltip.classList.remove('visible');
    });
  }, 100);
  
  // 기본 중심과 줌 레벨로 복귀
  map.setCenter(new kakao.maps.LatLng(35.9, 127.8));
  map.setLevel(13);
  
  // 부모 컴포넌트에 리셋 이벤트 전달 (바텀시트 닫기용)
  emit('map-reset');
};

const findMyLocation = () => {
  if (!navigator.geolocation) {
    alert("현재 브라우저에서는 위치 정보를 사용할 수 없습니다.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      const locPosition = new kakao.maps.LatLng(lat, lng);

      // 1. 줌 레벨 변경 (먼저 상세 레벨로 변경하여 좌표 계산 정확도 확보)
      map.setLevel(4, { animate: false }); 
      
      // 2. 중심 이동 (즉시 이동하여 깜빡임 최소화)
      map.setCenter(locPosition);

      // 기존 사용자 위치 마커가 있다면 제거
      if (userLocationMarker) {
        userLocationMarker.setMap(null);
      }

      // 사용자 위치 마커 생성 (DOM 요소 직접 생성 및 인라인 스타일 적용)
      const markerRoot = document.createElement('div');
      markerRoot.className = 'user-location-marker'; // 애니메이션용 클래스 유지
      markerRoot.style.position = 'relative';
      markerRoot.style.width = '40px';
      markerRoot.style.height = '40px';
      markerRoot.style.display = 'flex';
      markerRoot.style.justifyContent = 'center';
      markerRoot.style.alignItems = 'center';

      const dot = document.createElement('div');
      dot.style.width = '16px';
      dot.style.height = '16px';
      dot.style.backgroundColor = '#3b82f6';
      dot.style.border = '3px solid white';
      dot.style.borderRadius = '50%';
      dot.style.boxShadow = '0 0 6px rgba(0,0,0,0.4)';
      dot.style.zIndex = '2';

      const pulse = document.createElement('div');
      pulse.className = 'user-pulse'; // 애니메이션용 클래스 유지
      pulse.style.position = 'absolute';
      pulse.style.width = '100%';
      pulse.style.height = '100%';
      pulse.style.backgroundColor = 'rgba(59, 130, 246, 0.4)';
      pulse.style.borderRadius = '50%';
      pulse.style.zIndex = '1';

      markerRoot.appendChild(dot);
      markerRoot.appendChild(pulse);

      userLocationMarker = new kakao.maps.CustomOverlay({
        position: locPosition,
        content: markerRoot,
        map: map,
        zIndex: 9999, // 최상위 표시
        xAnchor: 0.5,
        yAnchor: 0.5
      });
    },
    (error) => {
      console.error("Geolocation error:", error);
      let msg = "위치 정보를 가져올 수 없습니다.";
      if (error.code === 1) msg = "위치 정보 접근 권한이 거부되었습니다.";
      alert(msg);
    },
    {
      enableHighAccuracy: true,
      maximumAge: 0,
      timeout: 10000
    }
  );
};

// props.areas가 변경될 때 마커를 업데이트합니다.
// 지도가 초기화된 후에만 마커를 표시하도록 `map` 변수를 확인합니다.
watch(
  () => props.areas,
  (newAreas) => {
    if (map) {
      displayAreaMarkers(newAreas);
    }
  },
  { deep: true }
);

// props.locations가 변경될 때 관광지 마커를 업데이트합니다.
watch(
  () => props.locations,
  (newLocations) => {
    if (map && currentZoomLevel < MARKER_VISIBLE_LEVEL) {
      displayLocationMarkers(newLocations);
    }
  },
  { deep: true }
);

onMounted(() => {
  const loadAndInitMap = () => {
    kakao.maps.load(() => {
      initMap();
    });
  };

  if (window.kakao && window.kakao.maps) {
    // 카카오맵 스크립트가 이미 로드된 경우
    loadAndInitMap();
  } else {
    // 카카오맵 스크립트가 아직 로드 중인 경우 대기
    let retryCount = 0;
    const checkKakao = setInterval(() => {
      if (window.kakao && window.kakao.maps) {
        clearInterval(checkKakao);
        loadAndInitMap();
      } else if (retryCount++ > 20) {
        clearInterval(checkKakao);
        console.error('카카오 지도 스크립트 로드 실패');
      }
    }, 100);
  }
});

const initMap = () => {
  const container = document.getElementById("map");
  const options = {
    center: new kakao.maps.LatLng(35.9, 127.8),
    level: 13,
    tileAnimation: false,
  };
  map = new kakao.maps.Map(container, options);

  // 지도 드래그 제한 설정
  const bounds = new kakao.maps.LatLngBounds(
    new kakao.maps.LatLng(33, 124),
    new kakao.maps.LatLng(39, 132)
  );

  kakao.maps.event.addListener(map, 'dragend', function() {
    const center = map.getCenter();
    let newLat = center.getLat();
    let newLng = center.getLng();

    if (center.getLat() < bounds.getSouthWest().getLat()) newLat = bounds.getSouthWest().getLat();
    if (center.getLat() > bounds.getNorthEast().getLat()) newLat = bounds.getNorthEast().getLat();
    if (center.getLng() < bounds.getSouthWest().getLng()) newLng = bounds.getSouthWest().getLng();
    if (center.getLng() > bounds.getNorthEast().getLng()) newLng = bounds.getNorthEast().getLng();

    if (newLat !== center.getLat() || newLng !== center.getLng()) {
      map.setCenter(new kakao.maps.LatLng(newLat, newLng));
    }
  });

  // 줌 레벨 변경 이벤트 리스너
  kakao.maps.event.addListener(map, 'zoom_changed', function() {
    const level = map.getLevel();
    currentZoomLevel = level;
    
    // 줌 레벨에 따라 마커 표시 전환
    // 줌 레벨에 따라 마커 표시 전환
    // 레벨 10 미만(더 확대된 상태)일 때 관광지 마커 표시
    if (level < MARKER_VISIBLE_LEVEL) {
      // 줌인 상태: 지역 마커 숨기고 관광지 마커 표시
      markers.forEach(marker => marker.setMap(null));
      if (props.locations && props.locations.length > 0) {
        displayLocationMarkers(props.locations);
      }
    } else {
      // 줌아웃 상태: 관광지 마커 숨기고 지역 마커 표시
      locationMarkers.forEach(marker => marker.setMap(null));
      locationMarkers = [];
      markers.forEach(marker => marker.setMap(map));
    }

    // 줌 레벨에 따라 범위 원 표시/숨김
    // 레벨 8 이하일 때만 원 표시 (8 초과면 숨김)
    if (level > CIRCLE_VISIBLE_LEVEL) {
      // 줌아웃 상태: 범위 원 숨김
      if (currentCircle) {
        currentCircle.setMap(null);
      }
    } else {
      // 줌인 상태: 선택된 관광지가 있으면 범위 원 다시 표시
      if (currentCircle && selectedLocationId) {
        currentCircle.setMap(map);
      }
    }
  });

  // 지도 배경 클릭 시 범위 원 제거
  kakao.maps.event.addListener(map, 'click', function() {
    removeVerificationCircle();
  });

  // *** FIX ***
  // 지도가 초기화된 후, props.areas에 이미 데이터가 있다면 마커를 표시합니다.
  if (props.areas && props.areas.length > 0) {
    displayAreaMarkers(props.areas);
  }
};
</script>

<style>
/* Custom Marker에 대한 전역 스타일 */
.custom-marker {
  position: relative;
  width: 36px;
  height: 36px;
  display: flex;
  justify-content: center;
  align-items: center;
  animation: float 2s ease-in-out infinite;
}

/* 관광지 마커는 조금 더 작게 */
.custom-marker.location-marker {
  width: 30px;
  height: 30px;
}

/* 둥실둥실 떠다니는 애니메이션 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-8px);
  }
}
.pin-body {
  position: absolute;
  width: 100%;
  height: 100%;
  color: white;
  border-radius: 50% 50% 50% 0;
  transform: rotate(-45deg);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3);
  border: 2px solid white;
}

.pin-body.quest-pin-inprogress {
  background-color: #fbbf24; /* 주황색 배경 */
}

.pin-body.quest-pin-completed {
  background-color: #22c55e; /* 초록색 배경 */
}

.pin-body.quest-pin-not-started {
  background-color: #38bdf8; /* 하늘색 배경 */
}

.pin-body .pin-text {
  transform: rotate(45deg); /* 'Q' 글자 바로 세우기 */
  color: white; /* 텍스트 색상을 흰색으로 변경 */
  font-size: 16px; /* 'Q' 글자 크기 조정 */
}
.pin-tail {
  position: absolute;
  width: 0;
  height: 0;
}

/* 툴팁 스타일 */
.pin-tooltip {
  position: absolute;
  bottom: 45px; /* 핀 위에 표시 */
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(30, 41, 59, 0.95);
  color: white;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
  pointer-events: none;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

/* 툴팁 화살표 */
.pin-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: rgba(30, 41, 59, 0.95);
}

/* 툴팁 표시 */
.pin-tooltip.visible {
  opacity: 1;
  visibility: visible;
}

/* 지도 초기화 버튼 */
.map-reset-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 44px;
  height: 44px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 24px;
  color: #374151;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-reset-btn:hover {
  background: #f9fafb;
  border-color: #3b82f6;
  color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  transform: scale(1.05);
}

.map-reset-btn:active {
  transform: scale(0.95);
}


/* The .custom-marker class itself acts as the container. 
   The yAnchor in the script handles the positioning of the point. */
</style>

<style scoped>
#map {
  width: 100%;
  height: 600px;
  background-color: #a2d1ff; /* 지도 타일 로딩 중 보이는 배경색을 바다색과 유사하게 변경 */
}

/* 사용자 위치 마커 스타일 */
.user-location-marker {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-dot {
  width: 16px;
  height: 16px;
  background-color: #3b82f6;
  border: 3px solid white;
  border-radius: 50%;
  box-shadow: 0 0 6px rgba(0,0,0,0.4);
  z-index: 2;
}

.user-pulse {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(59, 130, 246, 0.4);
  border-radius: 50%;
  animation: pulse 2s infinite;
  z-index: 1;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(2.5);
    opacity: 0;
  }
}
/* 내 위치 찾기 버튼 - 초기화 버튼 아래 배치 (정렬 및 스타일 통일) */
.map-locate-btn {
  position: absolute;
  top: 74px; /* 20(top) + 44(height) + 10(gap) */
  right: 20px; /* 초기화 버튼과 정렬 */
  width: 44px; /* 크기 통일 */
  height: 44px; /* 크기 통일 */
  background: white;
  border: 2px solid #e5e7eb; /* 스타일 통일 */
  border-radius: 8px; /* 스타일 통일 */
  font-size: 20px; /* 아이콘 크기 조정 */
  color: #374151;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-locate-btn:hover {
  background-color: #f8f9fa;
  color: #333;
}
</style>
