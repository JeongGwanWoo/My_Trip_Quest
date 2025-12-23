import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
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
import Admin from '@/views/Admin.vue' // Import Admin component

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
    path: '/travel',
    name: 'TravelSearch',
    component: () => import('@/views/TravelSearch.vue')
  },
  {
    path: '/fitting-room',
    name: 'FittingRoom',
    component: FittingRoom,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile/ongoing-quests',
    name: 'OngoingQuests',
    component: () => import('@/views/OngoingQuests.vue'),
    meta: { requiresAuth: true }
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
    path: '/find-password',
    name: 'FindPassword',
    component: () => import('@/views/FindPassword.vue'),
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
  {
    path: '/social-signup',
    name: 'SocialSignup',
    component: () => import('@/views/SocialSignup.vue'),
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin,
    meta: { requiresAuth: true, requiresAdmin: true } // Admin route requires authentication and admin role
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

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin) // Check for admin requirement

  if (requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (requiresAdmin && !authStore.isAdmin) { // New admin check
    next('/home') // Redirect to home or an unauthorized page
  } else {
    next()
  }
})

export default router