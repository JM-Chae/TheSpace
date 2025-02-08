import {createRouter, createWebHistory} from 'vue-router'
import axios from "axios";
import {ref} from "vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/space',
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
      meta: { login: true },
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
      path: '/user/join',
      name: 'join',
      component: () => import('../views/JoinView.vue')
    },
    {
      path: '/user/logout',
      name: 'logout',
      component: () => import('../views/LogoutView.vue')
    },
    {
      path: '/community',
      name: 'community',
      component: () => import('../views/CommunityListView.vue')
    },
    {
      path: '/community/home',
      name: 'communityhome',
      component: () => import('../views/CommunityHomeView.vue'),
    },
    {
      path: '/community/create',
      name: 'communitycreate',
      component: () => import('../views/CreateCommunityView.vue')
    },
    {
      path: '/community/management',
      name: 'cManagement',
      component: () => import('../views/CommunityManagementView.vue'),
    }
  ]
})

export default router
async function getInfo()
{
  try
  {
    await axios.get("/user/info", {withCredentials: true})
    .then(res =>
    {
      sessionStorage.setItem('login', 'true');
      sessionStorage.setItem("userInfo", JSON.stringify(res.data));
    });
    login.value = sessionStorage.getItem('login') == 'true';
  } catch (error)
  {
    return console.error("Error fetching user info" + error);
  }
}

const login =  ref(sessionStorage.getItem('login') == 'true');

router.beforeEach((to, from, next) =>
  {
    if (to.meta.login)
      {
        if(sessionStorage.getItem('login')?.split('=')[0] === 'true')
          {
            next()
          }else
          {
            alert('You must to login')
            next('/user/login')
          }

      }else {
        if(login.value) {
        getInfo();
        }
      next()
    }
  }
)