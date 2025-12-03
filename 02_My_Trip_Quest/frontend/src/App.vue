<template>
  <div id="app-container">
    
    <TheHeader v-if="!route.meta.hideHeader" />

    <TheNavBar v-if="!route.meta.hideHeader" />

    <router-view />

  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
import TheHeader from '@/components/common/TheHeader.vue';
import TheNavBar from '@/components/common/TheNavBar.vue';

const route = useRoute();
</script>

<style>

body {
  margin: 0;
  padding: 0;
  background-color: #f0f9ff;
}
</style>