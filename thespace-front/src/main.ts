import './assets/main.css'
import { createPinia } from 'pinia'
import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import 'bootstrap/dist/css/bootstrap-utilities.css'
import axios from "axios";

axios.defaults.withCredentials = true;
axios.defaults.baseURL = `http://localhost:8080`

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
