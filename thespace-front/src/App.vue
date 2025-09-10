<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {onMounted, ref, watchEffect} from 'vue'
import {useAuthStore} from "@/stores/auth";
import {storeToRefs} from 'pinia';
import NotificationView from "@/views/common/NotificationView.vue";
import {useNotificationsStore} from "@/stores/notification";
import FriendListView from "@/views/common/FriendListView.vue";
import {CloseBold, MoreFilled} from '@element-plus/icons-vue'
import {useFriendsStore} from './stores/friendship';


const { isLoggedIn, userInfo } = storeToRefs(useAuthStore());
const showMoreButton = ref(false)

onMounted(async () => {
  await useAuthStore().initializeAuth();
  watchEffect(async () => {
    if (isLoggedIn.value && showMoreButton.value) {
      await useNotificationsStore().initializeNotifications();
      await useFriendsStore().initializeFriends();
    }
  })
})
</script>

<template >
  <transition name="fade">
    <el-button v-if="!showMoreButton && isLoggedIn" class="show-button" link @click="showMoreButton = true"><el-icon style="font-size: 1.5em"><MoreFilled /></el-icon></el-button>
  </transition>
  <transition name="slide-in-right">
    <div v-if="isLoggedIn && showMoreButton" class="footer-menu">
      <div class="footer-menu-content">
        <div class="notification-container"><NotificationView /></div>
        <div class="friendList-container"><FriendListView /></div>
        <el-button v-if="showMoreButton" class="show-button" link @click="showMoreButton = false"><el-icon style="font-size: 1.5em"><CloseBold /></el-icon></el-button>
      </div>
    </div>
  </transition>

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
.footer-menu {
  position: fixed;
  bottom: 0;
  right: 0;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  width: 400px;
  height: 100px;
  gap: 20px;
  overflow: hidden;
}

.footer-menu-content {
  width: 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-sizing: border-box;
}

.show-button {
  position: fixed;
  bottom: 1em;
  right: 20px;
  border-radius: 0.5em;
  box-shadow: 0.1em 0.1em 1em 0.1em #878787;
  height: 60px;
  width: 62px;
  padding: 0;
  margin: 0.5em 0 0.5em 0;
  background-color: rgb(18,18,18);
  font-size: 1em;
  z-index: 9998;
}

.notification-container {
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;

}

.friendList-container {
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

</style>