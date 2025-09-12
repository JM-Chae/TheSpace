<script lang="ts" setup>

import NotificationView from "@/views/common/NotificationView.vue";
import FriendListView from "@/views/common/FriendListView.vue";
import {BellFilled, CloseBold, MoreFilled, UserFilled} from "@element-plus/icons-vue";
import {storeToRefs} from "pinia";
import {useNotificationsStore} from "@/stores/notification";
import {useAuthStore} from "@/stores/auth";
import {ref} from "vue";
import {useFriendsStore} from "@/stores/friendship";

const { showNotification } = storeToRefs(useNotificationsStore());
const { showFriendList } = storeToRefs(useFriendsStore());
const { isLoggedIn } = storeToRefs(useAuthStore());
const showMoreButton = ref(false)

function toggleModal(modal: string) {
  switch (modal) {
    case 'notification':
      showNotification.value = true;
      showFriendList.value = false;
      break;
    case 'friend':
      showFriendList.value = true;
      showNotification.value = false;
      break;
    case 'close':
      showFriendList.value = false;
      showNotification.value = false;
      showMoreButton.value = false;
      break;
  }
}
</script>

<template>
  <transition name="fade">
    <div>
      <el-button v-if="!showMoreButton && isLoggedIn" class="show-button" link @click="showMoreButton = true"><el-icon style="font-size: 1.5em"><MoreFilled /></el-icon></el-button>
      <div v-if="showMoreButton" class="footer-menu-content">
        <div class="friendList-container"><FriendListView /></div>
        <div class="notification-container"><NotificationView /></div>
      </div>
    </div>
  </transition>
  <transition name="slide-in-right">
  <div v-if="isLoggedIn && showMoreButton" class="footer-menu">
    <div style="display: flex; flex-direction: column; align-items: end;">
      <div>
        <transition name="fade">
          <div style="z-index: 9997; position: fixed; bottom: 1em; right: calc(40px + 4em); border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; height: 4em; width: 4em; padding: 0; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);" >
            <el-button link style="display: flex; width: 4em; height: 4em; margin: 0;" @click="toggleModal('notification')"><el-icon style="font-size: 2em; position: absolute; right: 16px; top: 16px;"><BellFilled /></el-icon></el-button>
          </div>
        </transition>
      </div>
      <div>
        <transition name="fade">
          <div style="z-index: 9997; position: fixed; bottom: 1em; right: calc(60px + 8em); border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; height: 4em; width: 4em; padding: 0; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);" >
            <el-button link style="display: flex; width: 4em; height: 4em; margin: 0;" @click="toggleModal('friend')"><el-icon style="font-size: 2em; position: absolute; right: 16px; top: 16px;"><UserFilled /></el-icon></el-button>
          </div>
        </transition>
      </div>
    </div>
    <el-button class="show-button" link @click="toggleModal('close')"><el-icon style="font-size: 1.5em"><CloseBold /></el-icon></el-button>
  </div>
  </transition>
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
  height: 0;
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