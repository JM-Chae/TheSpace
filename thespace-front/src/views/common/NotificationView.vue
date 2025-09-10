<script lang="ts" setup>
import {ref} from "vue";
import {BellFilled, CloseBold, Select} from '@element-plus/icons-vue'
import {
  formatDate,
  readNotification,
  routToUrlAndMarkAsRead,
  useNotificationsStore,
} from "@/stores/notification";
import {storeToRefs} from "pinia";
import type {Notification} from '@/types/domain';
import {lowerCase} from "lodash-es";
import {acceptRequest, rejectRequest} from "@/stores/friendship"

const { notificationList } = storeToRefs(useNotificationsStore());

const size = ref(10);

const infiniteHandler = async ($state: any) => {

  if (notificationList.value && notificationList.value.dtoList.length >= notificationList.value.total) {
    $state.complete();
    return
  }
  const MAX_PAGES = 1000;
  if (useNotificationsStore().page > MAX_PAGES) {
    console.warn("Infinite loading stopped: Maximum page limit (${MAX_PAGES}) reached.");
    $state.complete();
    return;
  }
  try {
      const loaded = await useNotificationsStore().updateList();

      if (loaded == 0) {
        $state.complete();
      } else {
        $state.loaded();
    }
  } catch (e) {
    console.error(e);
    $state.error();
  }
}

const rowStyle = (row : Notification ) => {
  if (row.isRead) {
    return {
      opacity: 0.2
    };
  }
  return {};
}

const showNotification = ref<boolean>(false);

const formattedBody = (notification: Notification) => {
  //if notification type is Friendship
  if (notification.type === 'FRIENDSHIP_REQUEST' || notification.type === 'FRIENDSHIP_ACCEPT') {
    if (notification.dataPayload?.targetNameAndUUID) {
      const targetNameAndUUID = notification.dataPayload.targetNameAndUUID
      return notification.body.replace(targetNameAndUUID,
          `<span class="highlight-user">${targetNameAndUUID}</span>`
      );
    }
  }

  //else
  return notification.body;
};
</script>

<template>

<!-- showNotification button -->
  <div style="display: flex; flex-direction: column; align-items: end;">
    <transition name="fade">
      <div v-if="!showNotification" style="z-index: 9997; position: fixed; bottom: 1em; right: calc(40px + 4em); border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; height: 4em; width: 4em; padding: 0; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);" >
      <el-button link style="display: flex; width: 4em; height: 4em; margin: 0;" @click="showNotification = true"><el-icon style="font-size: 2em; position: absolute; right: 16px; top: 16px;"><BellFilled /></el-icon></el-button>
      </div>
    </transition>
<!-- Notification popup -->
    <transition name="pop">
      <div v-if="showNotification" style="z-index: 9999; display: flex; flex-direction: column; position: fixed; right: 20px; bottom: 1em; width: 25em; height: 80vh; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);">
        <div style="display: flex; justify-content: center; border-bottom: 1px solid #434343; padding-bottom: 0.4em">
          <el-text style="font-size: 1.3em; font-weight: bold; color: #00DE64;">Notification</el-text>
  <!-- Popup close button -->
          <el-button link style="position: absolute; right: 1em; top: 1.3em;" @click="showNotification = false">X</el-button>
        </div>
        <el-button :type="'primary'" style="padding: 5px; margin-left: auto;" text @click="readNotification(0)">Mark all as read</el-button>
        <div ref="scrollContainer" class="scroll">
          <ul v-if="notificationList" class="notifications">
              <li v-for="notification in notificationList.dtoList" :key="notification.nno" :style="rowStyle(notification)" class="notification-item" @click="routToUrlAndMarkAsRead(notification)">
                <div class="n-list">
                  <div style="display: flex">
                    <div class="n-title">{{ notification.title }}</div>
                    <div class="n-date">{{ formatDate(notification.sentAt) }}</div>
                  </div>
                  <div v-dompurify-html="formattedBody(notification)" class="n-body"></div>
                  <div v-if="notification.dataPayload.status != 'REQUEST' && notification.type == 'FRIENDSHIP_REQUEST'"><el-text class="processed">You {{ lowerCase(notification.dataPayload.status) }} this request.</el-text></div>
                  <div v-if="notification.type == 'FRIENDSHIP_REQUEST' && notification.dataPayload.status == 'REQUEST'" class="n-f-button-container">
                    <el-button class="n-f-button accept" type="success" @click="acceptRequest(notification)" @click.stop><el-icon><Select /></el-icon></el-button>
                    <el-button class="n-f-button reject" type="danger" @click="rejectRequest(notification)" @click.stop><el-icon><CloseBold /></el-icon></el-button>
                  </div>
                </div>
              </li>
          </ul>
          <infinite-loading v-if="showNotification" @infinite="infiniteHandler">
            <template #complete><span> </span></template>
          </infinite-loading>
        </div>
      </div>
    </transition>
  </div>



</template>

<style scoped>
.scroll {
  flex: 1;
  overflow-y: auto;
}
.notifications {
  padding: 0;
  margin: 0;
}
.notification-item {
  list-style-type: none;
  padding: 0.5em;
  border-bottom: 1px solid #434343;
}
.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #404040;
}
.n-list {
  .n-title {
    font-weight: bold;
    font-size: 1.2em;
    color: white;
  }
  .n-date {
    font-size: 0.9em;
    margin-left: auto;
    color: #737373;
    min-width: fit-content;
  }
  .n-body {
    color: #bababa;
    margin: 0.5em 0;
  }

  .processed {
    color: #a9ff9c;
    font-style: italic;
    font-weight: bold;
  }

  .n-body :deep(.highlight-user) {
      font-weight: bold;
      color: #4e9bff;
  }

  .n-f-button-container {
    display: flex;
    justify-content: center;
    margin: 0.5em 0;

    .n-f-button {
      margin-top: 0.3em;
      width: 5em;
      height: 2.5em;
    }
  }
}
</style>