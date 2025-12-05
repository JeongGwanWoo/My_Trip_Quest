<template>
  <div id="map"></div>
</template>

<script setup>
import { onMounted, defineProps, watch } from "vue";

// 부모 컴포넌트로부터 areas 데이터를 받기 위한 props 정의
const props = defineProps({
  areas: {
    type: Array,
    required: true,
  },
});

let map = null; // 지도 인스턴스를 저장할 변수

// areas 데이터가 변경될 때 호출될 함수
const displayAreaMarkers = (newAreas) => {
  if (!map || !newAreas || newAreas.length === 0) {
    return;
  }

  // 주소-좌표 변환 객체를 생성합니다
  const geocoder = new kakao.maps.services.Geocoder();

  newAreas.forEach((area) => {
    // 주소로 좌표를 검색합니다
    geocoder.addressSearch(area.areaName, function (result, status) {
      // 정상적으로 검색이 완료됐으면
      if (status === kakao.maps.services.Status.OK) {
        const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

        // 결과값으로 받은 위치를 마커로 표시합니다
        const marker = new kakao.maps.Marker({
          map: map,
          position: coords,
        });

        // 커스텀 오버레이에 표시할 컨텐츠를 생성합니다
        const content = `<div class="custom-overlay">${area.questCount}</div>`;

        // 커스텀 오버레이를 생성합니다
        const customOverlay = new kakao.maps.CustomOverlay({
          position: coords,
          content: content,
          yAnchor: 2.5, // 마커 상단에 위치하도록 y 앵커 조정
        });

        // 커스텀 오버레이를 지도에 표시합니다
        customOverlay.setMap(map);
      } else {
        // 지오코딩 실패 시 로그 출력
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
/* CustomOverlay에 대한 전역 스타일 */
.custom-overlay {
  background-color: #ef4444; /* 빨간색 배경 */
  color: white; /* 흰색 텍스트 */
  padding: 5px 8px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: bold;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>

<style scoped>
#map {
  width: 100%;
  height: 600px;
}
</style>
