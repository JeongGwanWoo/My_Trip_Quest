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
  padding: 32px 28px;
  border-radius: 16px;
  width: 90%;
  max-width: 440px;
  position: relative;
  box-sizing: border-box;
  box-shadow: 0 10px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04);
}

@media (max-width: 600px) {
  .modal-content {
    width: 95%;
    max-width: none;
    padding: 24px 20px;
  }
}

.close-button {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  color: #64748b;
  border: none;
  border-radius: 50%;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s ease;
}
.close-button:hover {
  background: #e2e8f0;
  transform: rotate(90deg);
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
