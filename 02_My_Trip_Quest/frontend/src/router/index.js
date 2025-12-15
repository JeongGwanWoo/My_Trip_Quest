import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import QuestMap from '@/views/Questmap.vue'
import Rankings from '@/views/Rankings.vue'
import FittingRoom from '@/views/FittingRoom.vue'
import Profile from '@/views/Profile.vue'
import Shop from '@/views/Shop.vue'
import TermsOfService from '@/views/TermsOfService.vue'
import PrivacyPolicy from '@/views/PrivacyPolicy.vue'
import NotFound from '@/views/NotFound.vue'
import SocialLoginRedirect from '@/views/SocialLoginRedirect.vue'

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
  },
  {
    path: '/terms',
    name: 'Terms',
    component: TermsOfService,
  },
  {
    path: '/privacy',
    name: 'Privacy',
    component: PrivacyPolicy,
  },
  {
    path: '/social-login-redirect',
    name: 'SocialLoginRedirect',
    component: SocialLoginRedirect,
  },
  // Catch-all route for 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: 'active'
})

export default router