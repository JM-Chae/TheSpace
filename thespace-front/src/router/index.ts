import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/test',
      name: 'test',
      component:  () => import('../views/ComponentTest.vue'),
    },
    {
      path: '/list',
      name: 'list',
      component:  () => import('../views/ListView.vue'),
    },
    {
      path: '/space',
      name: 'home',
      component:  () => import('../views/HomeView.vue'),
    },
    {
      path: '/post',
      name: 'post',
      component: () => import('../views/PostView.vue'),
      meta: { roles: ["ROLE_USER"] },
      props: true
    },
    {
      path: '/modify',
      name: 'modify',
      component: () => import('../views/ModifyView.vue'),
      meta: { roles: ["ROLE_USER"] },
      props: true
    },
    {
      path: '/read',
      name: 'read',
      component:  () => import('../views/ReadView.vue'),
      props: true
    },
    {
      path: '/user/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/user/logout',
      name: 'logout',
      component: () => import('../views/LogoutView.vue')
    }
  ]
})

export default router

const userInfo = sessionStorage.getItem("userInfo") || "{}"
const roles = JSON.parse(userInfo)


router.beforeEach((to, from, next) =>
    {

      if (to.meta.roles && userInfo == "{}")
      {
        alert('You must to login')
          next('/user/login')
      }else {
          next()
        }
    }
)