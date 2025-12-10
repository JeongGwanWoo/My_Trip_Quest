import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import QuestMap from '@/views/Questmap.vue'
import Rankings from '@/views/Rankings.vue'
import FittingRoom from '@/views/FittingRoom.vue'
import Profile from '@/views/Profile.vue'
import Shop from '@/views/Shop.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  

  {
  path: '/rankings',
  name: 'Rankings',
  component: Rankings
 },

  {
    path: '/quest-map',
    name: 'QuestMap',
    component: QuestMap
    
  },
  {
    path: '/fitting-room',
    name: 'FittingRoom',
    component: FittingRoom
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/shop',
    name: 'Shop',
    component: Shop
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/signup',
    name: 'Signup',
    component: () => import('@/views/SignupView.vue'),
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

export default router