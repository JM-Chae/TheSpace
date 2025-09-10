<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {onMounted, watchEffect} from 'vue'
import {useAuthStore} from "@/stores/auth";
import {storeToRefs} from 'pinia';
import {useNotificationsStore} from "@/stores/notification";
import {useFriendsStore} from './stores/friendship';
import ComponentButton from "@/views/common/ComponentButton.vue";


const { isLoggedIn, userInfo } = storeToRefs(useAuthStore());

onMounted(async () => {
  await useAuthStore().initializeAuth();
  watchEffect(async () => {
    const notificationStore = useNotificationsStore();
    if (isLoggedIn.value && notificationStore.showNotification) {
      if (!notificationStore.notificationList.dtoList ||
          notificationStore.notificationList.dtoList.length === 0) {
        await useNotificationsStore().initializeNotifications();
      }
    }
      const friendsStore = useFriendsStore();
    if (isLoggedIn.value && friendsStore.showFriendList) {
      if (!friendsStore.friendsList.dtoList ||
          friendsStore.friendsList.dtoList.length === 0) {
        await useFriendsStore().initializeFriends();
      }
    }
  })
})
</script>

<template >
  <ComponentButton></ComponentButton>

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
</style>