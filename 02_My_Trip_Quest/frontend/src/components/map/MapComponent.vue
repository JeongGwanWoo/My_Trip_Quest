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

// 부모 컴포넌트로 이벤트를 보내기 위한 emit 함수 정의
const emit = defineEmits(['area-clicked']);

// areas 데이터가 변경될 때 호출될 함수
const displayAreaMarkers = (newAreas) => {
  if (!map || !newAreas || newAreas.length === 0) {
    return;
  }

  const geocoder = new kakao.maps.services.Geocoder();

  newAreas.forEach((area) => {
    geocoder.addressSearch(area.areaName, function (result, status) {
      if (status === kakao.maps.services.Status.OK) {
        const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

        // 커스텀 오버레이를 위한 DOM 엘리먼트를 생성합니다.
        const contentEl = document.createElement('div');
        contentEl.className = 'custom-marker';
        contentEl.innerHTML = `
          <div class="pin-body"><span class="pin-number">${area.incompleteLocationCount}</span></div>
          <div class="pin-tail"></div>
        `;

        // 엘리먼트에 직접 클릭 이벤트를 추가합니다.
        contentEl.addEventListener('click', () => {
          emit('area-clicked', area.areaCode);
        });

        // 커스텀 오버레이를 생성합니다
        const customOverlay = new kakao.maps.CustomOverlay({
          position: coords,
          content: contentEl, // 문자열 대신 엘리먼트를 전달합니다.
          clickable: true, // clickable은 여전히 필요할 수 있습니다.
          yAnchor: 1.3, 
        });

        customOverlay.setMap(map);

      } else {
        console.error(`'${area.areaName}' 주소에 대한 지오코딩 실패:`, status);
      }
    });
  });
};
onMounted(() => {
  if (window.kakao && window.kakao.maps) {
    initMap();
  } else {
    const script = document.createElement("script");
    /* global kakao */
    script.onload = () => {
      kakao.maps.load(() => {
        initMap();
        // Kakao Maps SDK가 로드된 후 props.areas를 감시 시작
        watch(
          () => props.areas,
          (newAreas) => {
            displayAreaMarkers(newAreas);
          },
          { immediate: true, deep: true } // 컴포넌트가 마운트될 때 즉시 실행
        );
      });
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
  };
  map = new kakao.maps.Map(container, options); // map 변수에 인스턴스 할당
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
  background-color: #ef4444; /* 빨간색 배경 */
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
.pin-body .pin-number {
  transform: rotate(45deg); /* 숫자 바로 세우기 */
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
}
</style>
