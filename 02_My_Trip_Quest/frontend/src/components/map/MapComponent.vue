<template>
  <div id="map"></div>
</template>

<script setup>
import { onMounted, defineProps, watch, defineEmits } from "vue";

// 부모 컴포넌트로부터 areas 데이터를 받기 위한 props 정의
const props = defineProps({
  areas: {
    type: Array,
    required: true,
  },
});

let map = null; // 지도 인스턴스를 저장할 변수
let markers = []; // 마커(오버레이) 인스턴스를 저장할 배열

// 부모 컴포넌트로 이벤트를 보내기 위한 emit 함수 정의
const emit = defineEmits(['area-clicked']);

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
        // 퀘스트 완료 여부에 따라 핀 색상 결정
        const pinColorClass = area.incompleteLocationCount === 0 ? 'quest-pin-completed' : 'quest-pin-active';
        contentEl.innerHTML = `
          <div class="pin-body ${pinColorClass}"><span class="pin-text">Q</span></div>
          <div class="pin-tail"></div>
        `;
        contentEl.addEventListener('click', () => {
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

onMounted(() => {
  const loadAndInitMap = () => {
    kakao.maps.load(() => {
      initMap();
    });
  };

  if (window.kakao && window.kakao.maps) {
    // 카카오맵 스크립트가 이미 로드된 경우
    initMap();
  } else {
    // 카카오맵 스크립트가 로드되지 않은 경우
    const script = document.createElement("script");
    script.onload = () => {
      loadAndInitMap();
    };
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=4a721b099696ef1b4f34ca52a919cbee&libraries=services`;
    document.head.appendChild(script);
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

.pin-body.quest-pin-active {
  background-color: #fbbf24; /* 주황색 배경 */
}

.pin-body.quest-pin-completed {
  background-color: #22c55e; /* 초록색 배경 */
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
/* The .custom-marker class itself acts as the container. 
   The yAnchor in the script handles the positioning of the point. */
</style>

<style scoped>
#map {
  width: 100%;
  height: 600px;
  background-color: #a2d1ff; /* 지도 타일 로딩 중 보이는 배경색을 바다색과 유사하게 변경 */
}
</style>
