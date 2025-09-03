import './assets/main.css'

import {createPinia} from 'pinia'
import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap-utilities.css'
import VueDOMPurifyHTML from 'vue-dompurify-html';
import 'element-plus/theme-chalk/dark/css-vars.css'
import ListView from './views/web/ListView.vue'
import ListViewAdmin from './views/web/ListViewAdmin.vue'

import {setupAxiosInterceptors} from "@/axiosConfig";
import {initializeFcmListener} from "@/firebase";

import {initializeServiceWorkerListener} from './sw-listener';


const app = createApp(App);
app.use(createPinia());
initializeServiceWorkerListener();

app.use(router);
app.use(ElementPlus);
app.use(VueDOMPurifyHTML);

app.component('ListView', ListView);
app.component('ListViewAdmin', ListViewAdmin);

initializeFcmListener();
setupAxiosInterceptors(router)

app.mount('#app');

