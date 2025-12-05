<template>
  <div class="fitting-page">
    <div class="content-container">

      <section class="preview-section">
        <h2 class="section-label">CHARACTER PREVIEW</h2>
        
        <div class="avatar-display">
          <div class="avatar-layers">
            <div class="layer skin">{{ equipped.skin.image }}</div>
            <div class="layer outfit" v-if="equipped.outfit">{{ equipped.outfit.image }}</div>
            <div class="layer hair" v-if="equipped.hair">{{ equipped.hair.image }}</div>
            <div class="layer accessory" v-if="equipped.accessory">{{ equipped.accessory.image }}</div>
          </div>
        </div>

        <div class="username">TRAVELMASTER</div>
      </section>

      <section class="customize-section">
        <h2 class="section-label">
          <span class="sparkle-icon">✨</span> CUSTOMIZE YOUR CHARACTER
        </h2>

        <div class="tab-bar">
          <button 
            v-for="tab in tabs" 
            :key="tab.id"
            class="tab-btn"
            :class="{ active: currentTab === tab.id }"
            @click="currentTab = tab.id"
          >
            <span class="tab-icon">{{ tab.icon }}</span> {{ tab.label }}
          </button>
        </div>

        <div class="items-area">
          <div 
            v-for="item in currentItems" 
            :key="item.id"
            class="item-card"
            :class="{ selected: isEquipped(item) }"
            @click="equipItem(item)"
          >
            <div v-if="isEquipped(item)" class="check-mark">✔</div>
            
            <div class="item-img">{{ item.image }}</div>
            <div class="item-name">{{ item.name }}</div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const currentTab = ref('all');

const equipped = ref({
  skin: { id: 1, type: 'skin', name: '기본 스킨', image: '😊' },
  hair: { id: 4, type: 'hair', name: '모자', image: '🎩' },
  outfit: { id: 7, type: 'outfit', name: '정장', image: '👔' },
  accessory: null 
});

const tabs = [
  { id: 'all', label: 'ALL ITEMS', icon: '🏷️' },
  { id: 'skin', label: 'SKINS', icon: '😊' },
  { id: 'hair', label: 'HAIR', icon: '🎩' },
  { id: 'outfit', label: 'OUTFITS', icon: '👖' },
  { id: 'accessory', label: 'ACCESSORIES', icon: '🎒' },
];

const inventory = {
  skin: [
    { id: 1, type: 'skin', name: '기본 스킨', image: '😊' },
    { id: 2, type: 'skin', name: '멋진 스킨', image: '😎' },
    { id: 3, type: 'skin', name: '행복 스킨', image: '😄' },
  ],
  hair: [
    { id: 4, type: 'hair', name: '모자', image: '🎩' },
    { id: 5, type: 'hair', name: '왕관', image: '👑' },
    { id: 6, type: 'hair', name: '헬멧', image: '⛑️' },
  ],
  outfit: [
    { id: 7, type: 'outfit', name: '정장', image: '👔' },
    { id: 8, type: 'outfit', name: '티셔츠', image: '👕' },
    { id: 9, type: 'outfit', name: '드레스', image: '👗' },
  ],
  accessory: [
    { id: 10, type: 'accessory', name: '배낭', image: '🎒' },
    { id: 11, type: 'accessory', name: '카메라', image: '📷' },
    { id: 12, type: 'accessory', name: '지도', image: '🗺️' },
  ]
};

const currentItems = computed(() => {
  if (currentTab.value === 'all') {
    // Flatten all items from all categories into one array
    return Object.values(inventory).flat();
  }
  return inventory[currentTab.value] || [];
});

const isEquipped = (item) => {
  if (!item || !item.type) return false;
  const current = equipped.value[item.type];
  return current && current.id === item.id;
};

const equipItem = (item) => {
  
  if (isEquipped(item)) {
    // Cannot unequip skin
    if (item.type !== 'skin') {
      equipped.value[item.type] = null;
    }
  } else {
    equipped.value[item.type] = item;
  }
};
</script>

<style scoped>

.fitting-page {
  width: 100%;
  display: flex;
  justify-content: center;
  color: #1e1e1e;
}

.content-container {
  max-width: 1000px;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.preview-section {
  background-color: #8b5cf6;
  border: 4px solid #000;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  color: white;
}

.section-label {
  font-size: 14px;
  margin-bottom: 20px;
  text-shadow: 2px 2px 0 #000;
  letter-spacing: 2px;
}

.avatar-display {
  width: 150px;
  height: 150px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  position: relative;
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
}

.avatar-layers {
  position: relative;
  width: 100px;
  height: 100px;
}

.layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  filter: drop-shadow(4px 4px 0 rgba(0,0,0,0.3));
}

.layer.skin { z-index: 1; }
.layer.outfit { z-index: 2; margin-top: 30px; font-size: 60px; }
.layer.hair { z-index: 3; margin-top: -35px; }
.layer.accessory { z-index: 4; margin-left: 40px; margin-top: 20px; font-size: 40px; }

.username {
  font-size: 16px;
  font-weight: bold;
  text-shadow: 2px 2px 0 #000;
  letter-spacing: 1px;
}

.customize-section {
  background-color: #ec4899;
  border: 4px solid #000;
  padding: 20px;
  box-shadow: 6px 6px 0 rgba(0,0,0,0.2);
  min-height: 400px;
}

.customize-section .section-label {
  font-size: 16px;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  text-shadow: 2px 2px 0 #000;
}

.tab-bar {
  background-color: #1e293b;
  padding: 10px;
  border: 4px solid #fbbf24;
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab-btn {
  background: transparent;
  border: 2px solid transparent;
  color: #94a3b8;
  padding: 8px 16px;
  font-family: inherit;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  text-transform: uppercase;
}

.tab-btn:hover {
  color: white;
}

.tab-btn.active {
  background-color: #fbbf24;
  color: #000;
  border: 2px solid #000;
  font-weight: bold;
  box-shadow: 2px 2px 0 #000;
}

.items-area {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 20px;
  padding: 10px;
}

.item-card {
  background-color: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(0,0,0,0.1);
  padding: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  position: relative;
  transition: transform 0.1s;
}

.item-card:hover {
  transform: translateY(-5px);
  background-color: rgba(255, 255, 255, 0.4);
}

.item-card.selected {
  background-color: #22c55e;
  border: 4px solid #fbbf24;
  box-shadow: 4px 4px 0 rgba(0,0,0,0.2);
}

.check-mark {
  position: absolute;
  top: 5px;
  right: 5px;
  color: #fbbf24;
  font-size: 10px;
  text-shadow: 1px 1px 0 #000;
}

.item-img {
  font-size: 32px;
  margin-bottom: 10px;
  filter: drop-shadow(2px 2px 0 rgba(0,0,0,0.2));
}

.item-name {
  font-size: 8px;
  color: white;
  text-shadow: 1px 1px 0 #000;
  text-align: center;
}

@media (max-width: 768px) {
  .tab-bar {
    justify-content: flex-start; /* Align to start for scrolling */
    overflow-x: auto; /* Enable horizontal scrolling */
    flex-wrap: nowrap; /* Prevent wrapping */
    padding: 8px 10px; /* Adjust padding */
    gap: 8px; /* Reduce gap between buttons */

    /* Hide scrollbar */
    -ms-overflow-style: none;  /* IE and Edge */
    scrollbar-width: none;  /* Firefox */
  }
  .tab-bar::-webkit-scrollbar {
    display: none; /* Chrome, Safari and Opera */
  }
  .tab-btn {
    flex-shrink: 0; /* Prevent buttons from shrinking */
    font-size: 9px; /* Smaller font size */
    padding: 6px 10px; /* Smaller padding */
    gap: 6px;
  }
  .tab-btn .tab-icon {
    font-size: 14px; /* Smaller icon size */
  }
  .items-area { grid-template-columns: repeat(3, 1fr); }
}
</style>