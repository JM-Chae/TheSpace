<script lang="ts" setup>
import {ref} from "vue";
import {BellFilled, CloseBold, Select} from '@element-plus/icons-vue'
import {formatDate, routToURL, useNotificationsStore,} from "@/stores/notification";
import {storeToRefs} from "pinia";
import type {Notification} from '@/types/domain';
import {lowerCase} from "lodash-es";
import {acceptRequest, rejectRequest} from "@/stores/friendship"

const { notificationList } = storeToRefs(useNotificationsStore());

const page = ref(1);
const size = ref(10);

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
    <div v-if="!showNotification" style="position: fixed; bottom: 1em; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; height: 4em; width: 4em; padding: 0; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);" >
       <el-button link style="display: flex; width: 4em; height: 4em; margin: 0;" @click="showNotification = true"><el-icon style="font-size: 2em; position: absolute; right: 16px; top: 16px;"><BellFilled /></el-icon></el-button>
    </div>
<!-- Notification popup -->
    <div v-if="showNotification" style="display: flex; flex-direction: column; position: fixed; bottom: 1em; width: 25em; height: 40em; border-radius: 0.5em; box-shadow: 0.1em 0.1em 1em 0.1em #878787; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(18,18,18);">
      <div style="display: flex; justify-content: center">
        <el-text style="font-size: 1.3em; font-weight: bold; color: #00DE64;">Notification</el-text>
<!-- Popup close button -->
        <el-button link style="position: absolute; right: 1em; top: 1.3em;" @click="showNotification = false">X</el-button>
      </div>
      <el-table v-if="notificationList" :data="notificationList.dtoList" empty-text="" @row-click = routToURL>
        <el-table-column>
          <template #default = "scope">
            <div class="n-list">
              <div style="display: flex">
                <div class="n-title">{{ scope.row.title }}</div>
                <div class="n-date">{{ formatDate(scope.row.sentAt) }}</div>
              </div>
              <div v-dompurify-html="formattedBody(scope.row)" class="n-body"></div>
              <div v-if="scope.row.dataPayload.status != 'REQUEST' && scope.row.type == 'FRIENDSHIP_REQUEST'"><el-text class="processed">You {{ lowerCase(scope.row.dataPayload.status) }} this request.</el-text></div>
              <div v-if="scope.row.type == 'FRIENDSHIP_REQUEST' && scope.row.dataPayload.status == 'REQUEST'" class="n-f-button-container">
                <el-button class="n-f-button accept" type="success" @click="acceptRequest(scope.row)" @click.stop><el-icon><Select /></el-icon></el-button>
                <el-button class="n-f-button reject" type="danger" @click="rejectRequest(scope.row)" @click.stop><el-icon><CloseBold /></el-icon></el-button>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>

</template>

<style scoped>
/* Styles are unchanged */
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