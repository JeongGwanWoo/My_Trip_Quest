<template>
  <Transition name="overlay-fade">
    <div v-if="show" class="modal-overlay" @click.self="close">
      <Transition name="modal-slide">
        <div v-if="show" class="modal-content">
          <button class="close-button" @click="close">X</button>
          <slot></slot>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    required: true,
  },
});

const emit = defineEmits(['close']);

const close = () => {
  emit('close');
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100; /* Ensure modal is on top of the bottom sheet */
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%; /* Take 90% of available width */
  max-width: 500px; /* Max width on larger screens */
  position: relative;
  box-sizing: border-box; /* Include padding in width calculation */
}

@media (max-width: 600px) {
  .modal-content {
    width: 95%; /* Wider on small screens */
    max-width: none; /* No max-width on small screens */
    padding: 15px; /* Slightly less padding */
  }
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

/* Modal Transition Styles */
.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.3s ease;
}
.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}

.modal-slide-enter-active,
.modal-slide-leave-active {
  transition: all 0.3s ease-out;
}
.modal-slide-enter-from,
.modal-slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>
