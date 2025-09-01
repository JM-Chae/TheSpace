import axios from "axios";
import {useAuthStore} from "@/stores/auth";
import type {Router} from "vue-router";


axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.withCredentials = true;

export const setupAxiosInterceptors = (router: Router) => {
  const authStore = useAuthStore();

  axios.interceptors.response.use((res) => res, async error => {
    if (error.response && error.response.status === 401) {
      console.log("401 Unauthorized. Logging out.");

      await authStore.logout();
      await router.push("/user/login?expired=true");
    }

    return Promise.reject(error);
  });
}