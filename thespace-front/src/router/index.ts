import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/space',
      name: 'home',
      component:  () => import('../views/HomeView.vue')
    },
    {
      path: '/post',
      name: 'post',
      component: () => import('../views/PostView.vue')
    },
    {
      path: '/user/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    }
  ]
})

export default router
