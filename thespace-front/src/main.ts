import './assets/main.css'

import {createPinia} from 'pinia'
import {createApp, ref} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap-utilities.css'
import axios from "axios";
import VueDOMPurifyHTML from 'vue-dompurify-html';
import 'element-plus/theme-chalk/dark/css-vars.css'
import ListView from './views/web/ListView.vue'
import ListViewAdmin from './views/web/ListViewAdmin.vue'

//axios config
axios.defaults.withCredentials = true;
axios.defaults.baseURL = `http://localhost:8080`

export async function loadCsrfToken() {
  const res = await axios.get('/user/csrf');
  const csrfToken = res.data.token;
  const csrfHeader = res.data.headerName;

  axios.defaults.headers.common[csrfHeader] = csrfToken;
}

export const login = ref(sessionStorage.getItem('login') === 'true')

export function setLogin(state: boolean) {
  login.value = state
  sessionStorage.setItem('login', state ? 'true' : 'false')
}

if (sessionStorage.getItem('login') === 'true') {
  loadCsrfToken();
}

(async () => {
  await loadCsrfToken();

  //login status
  if (document.cookie.includes('isRemember=true')) {
    if (!(sessionStorage.getItem('login') == 'true')) {
      axios.post("/user/login/me", null,{withCredentials: true})
      .then(res =>
      {
        setLogin(true)
        sessionStorage.setItem('login', 'true');
        sessionStorage.setItem("userInfo", JSON.stringify(res.data));
        loadCsrfToken();
      });
    }
  }
  const app = createApp(App);
  app.use(createPinia());
  app.use(router);
  app.use(ElementPlus);
  app.use(VueDOMPurifyHTML);
  app.component('ListView', ListView);
  app.component('ListViewAdmin', ListViewAdmin);
  app.mount('#app');
})();

