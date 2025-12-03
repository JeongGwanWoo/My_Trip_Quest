<template>
  <button class="base-button">
    <slot></slot>
  </button>
</template>

<script setup>
</script>

<style scoped>
.base-button {
  padding: 10px 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background-color: #f0f0f0;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.base-button:hover {
  background-color: #e0e0e0;
}
</style>
