<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {onMounted} from 'vue'
import {useAuthStore} from "@/stores/auth";
import {storeToRefs} from 'pinia';
import NotificationView from "@/views/web/NotificationView.vue";
import {useNotificationsStore} from "@/stores/notification";

const { isLoggedIn, userInfo } = storeToRefs(useAuthStore());

onMounted(async () => {
  await useAuthStore().initializeAuth();
  if(isLoggedIn.value) await useNotificationsStore().initializeNotifications(1, 10);

})
</script>

<template>
  <div class="notification-container">
    <NotificationView v-if="isLoggedIn" />
  </div>

  <header>
    <div class="wrapper pb-5" style="height: 10vh">
      <nav style="display: flex">
        <RouterLink to="/space">HOME</RouterLink>
				<RouterLink to="/community">COMMUNITY</RouterLink>
				<RouterLink v-if="isLoggedIn" style="margin-left: auto; margin-right: 0.5em" to="/user/logout">LOGOUT</RouterLink>
        <RouterLink v-else style="margin-left: auto; margin-right: 0.5em" to="/user/login">LOGIN</RouterLink>
        <RouterLink v-if="isLoggedIn" :to="{name: 'mypage', query: { uuid: userInfo.uuid }}" style="margin-right: 0.5em">MY PAGE</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />

  <footer>
  </footer>
</template>

<style scoped>
.notification-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  width: 350px;
}
</style>