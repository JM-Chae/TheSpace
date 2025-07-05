import {createRouter, createWebHistory} from 'vue-router'
import axios from "axios";
import {ref} from "vue";
import {ElMessageBox} from "element-plus";

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
      component:  () => import('../views/web/HomeView.vue'),
    },
    {
      path: '/post',
      name: 'post',
      component: () => import('../views/web/PostView.vue'),
      meta: { login: true },
      props: true
    },
    {
      path: '/modify',
      name: 'modify',
      component: () => import('../views/web/ModifyView.vue'),
      meta: { roles: ["ROLE_USER"] },
      props: true
    },
    {
      path: '/read',
      name: 'read',
      component:  () => import('../views/web/ReadView.vue'),
      props: true
    },
    {
      path: '/user/login',
      name: 'login',
      component: () => import('../views/web/LoginView.vue')
    },
    {
      path: '/user/join',
      name: 'join',
      component: () => import('../views/web/JoinView.vue')
    },
    {
      path: '/user/logout',
      name: 'logout',
      component: () => import('../views/web/LogoutView.vue')
    },
    {
      path: '/community',
      name: 'community',
      component: () => import('../views/web/CommunityListView.vue')
    },
    {
      path: '/community/home',
      name: 'communityhome',
      component: () => import('../views/web/CommunityHomeView.vue'),
    },
    {
      path: '/community/create',
      name: 'communitycreate',
      component: () => import('../views/web/CreateCommunityView.vue')
    },
    {
      path: '/community/management',
      name: 'cManagement',
      component: () => import('../views/web/CommunityManagementView.vue'),
    },
    {
      path: '/mypage/',
      name: 'mypage',
      component: () => import('../views/web/MyPageView.vue'),
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
            ElMessageBox.alert('You must to login', 'Alert',
                {
                  confirmButtonText: 'OK',
                  type: 'warning',
                  dangerouslyUseHTMLString: true,
                  center: true,
                  customClass: '.el-message-box'
                })
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