<template>
  <div id="map"></div>
</template>

<script setup>
import { onMounted } from "vue";

onMounted(() => {
  if (window.kakao && window.kakao.maps) {
    initMap();
  } else {
    const script = document.createElement("script");
    /* global kakao */
    script.onload = () => kakao.maps.load(initMap);
    script.src =
      "//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=4a721b099696ef1b4f34ca52a919cbee";
    document.head.appendChild(script);
  }
});

const initMap = () => {
  const container = document.getElementById("map");
  const options = {
    center: new kakao.maps.LatLng(35.9, 127.8),
    level: 13,
  };
  const map = new kakao.maps.Map(container, options);

  // You can add more map features here, like markers, info windows, etc.
};
</script>

<style scoped>
#map {
  width: 100%;
  height: 600px;
}
</style>
