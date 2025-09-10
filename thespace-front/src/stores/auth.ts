import {defineStore} from 'pinia';
import type {User} from "@/types/domain"
import axios from "axios";
import {loadCsrfToken} from "@/stores/loadCsrfToken";
import {registerFcmToken} from "@/firebase"; // API 호출용

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false as boolean,
    userInfo: {} as User,
  }),
  actions: {
    async initializeAuth() {

      const csrfToken = axios.defaults.headers.common['X-CSRF-TOKEN'];
      if (!csrfToken) await loadCsrfToken();

      let storedLogin = sessionStorage.getItem('login') === 'true';
      let storedUserInfo = sessionStorage.getItem('userInfo') as string;

      if (storedLogin && storedUserInfo) {
        this.isLoggedIn = true;
        try {
          this.userInfo = JSON.parse(storedUserInfo);
        } catch (e) {
          console.error("Error parsing userInfo from storage:", e);
          this.userInfo = {} as User;
          sessionStorage.removeItem('userInfo');
        }
        return;
      } else if (document.cookie.includes('isRemember=true')) {
        if (!(sessionStorage.getItem('login') == 'true')) {
          axios.post("/user/login/me", null,{withCredentials: true})
          .then(async res => {
            const authStore = useAuthStore();

            sessionStorage.setItem('login', 'true');
            sessionStorage.setItem("userInfo", JSON.stringify(res.data));
            await loadCsrfToken();
            await registerFcmToken();
            await this.loginSuccess(res.data)
          });
        }
      } else {
        this.isLoggedIn = false;
        this.userInfo = {} as User;
      }
    },

    async loginSuccess(userInfo: any) {
      this.isLoggedIn = true;
      this.userInfo = userInfo;

      console.log("login success.");
    },

    async logout() {
      this.isLoggedIn = false;
      this.userInfo = {} as User;
      sessionStorage.removeItem('login');
      sessionStorage.removeItem('userInfo');

      console.log("logout success.");
    },
  },
});
