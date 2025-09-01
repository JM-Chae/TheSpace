<script setup lang = "ts">
import axios from "axios";
import {unregisterFcmToken} from "@/firebase";
import router from "@/router";
import {useAuthStore} from '@/stores/auth';

const authStore = useAuthStore();

axios.post("/user/logout", {withCredentials: true}).then(async () => {
  document.cookie = 'isRemember=false; path=/; false';
  sessionStorage.removeItem("userInfo");
  sessionStorage.setItem("login", "false");

  await unregisterFcmToken();
  await authStore.logout();
  sessionStorage.removeItem("fcmToken");

  await router.push({name: "home"})
})
</script>
<template>

</template>