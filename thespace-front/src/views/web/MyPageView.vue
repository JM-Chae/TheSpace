<script lang="ts" setup>
import {Edit} from '@element-plus/icons-vue'
import {onMounted, ref, watch} from "vue";
import axios from "axios";
import {onBeforeRouteUpdate, useRoute} from "vue-router";
import EmojiPicker from 'vue3-emoji-picker';
import 'vue3-emoji-picker/css';
import {useAuthStore} from "@/stores/auth";
import {
  friendshipBlock,
  friendshipRequest,
  friendshipRequestDelete,
  friendshipUnblock,
  getFriendshipInfo
} from '@/stores/friendship';
import type {FriendshipInfo, User} from '@/types/domain'

const uuid = ref(useRoute().query.uuid);

const pageUserInfo = ref<User>();
const friendshipInfo = ref<FriendshipInfo>();

const fStatus = ref<string>();
const fMemo = ref<string>();

async function fetchData(uuid: string) {
  if (!uuid) return;
  // Fetch user info
  axios.get(`/user/${uuid}/mypage`).then((res) => {
    pageUserInfo.value = res.data;
  });
 // Fetch friendship info
  friendshipInfo.value = await getFriendshipInfo(uuid);
}


//Valid value
function withValidUuid(action: (pageUserUUID: string) => void) {
    const pageUserUUID = pageUserInfo.value?.uuid;
    if (pageUserUUID) {
      action(pageUserUUID);
    } else {
      console.error("UUID not found.");
    }
}

function withValidUuidAndFid(action: (pageUserUUID: string, fid: number) => void) {
  const pageUserUUID = pageUserInfo.value?.uuid;
  const fid = friendshipInfo.value?.fid;
  if (pageUserUUID && fid) {
    action(pageUserUUID, fid);
  } else {
    console.error("UUID, FID not found.");
  }
}

//Friendship control logic
async function friendshipRequestHandler() {
  withValidUuid(async (pageUserUUID) => {
    friendshipInfo.value = await friendshipRequest(pageUserUUID);
  })
}

async function friendshipBlockHandler() {
  withValidUuid(async (pageUserUUID) => {
    friendshipInfo.value = await friendshipBlock(pageUserUUID);
  })
}

async function friendshipRequestDeleteHandler() {
  withValidUuidAndFid(async (pageUserUUID, fid) => {
    friendshipInfo.value = await friendshipRequestDelete(pageUserUUID, fid);
  })
}

async function friendshipUnblockHandler() {
  withValidUuidAndFid(async (pageUserUUID, fid) => {
    friendshipInfo.value = await friendshipUnblock(pageUserUUID, fid);
  })
}

watch(friendshipInfo, (newVal) => {
  if(newVal) {
    fMemo.value = newVal.memo;

    if (newVal.status == "NONE") fStatus.value =  "Oh, Do you wanna be 'Friends' with them?"
    else if (newVal.status == "REQUEST") fStatus.value = "Hmm... maybe they haven't seen your request yet.\nOr, you know, they might be thinking it over."
    else if (newVal.status == "BLOCKED") fStatus.value = "I thought you weren't a fan of them. No?"
    else if (newVal.status == "ACCEPTED") fStatus.value = "What's up bro?"
  }
})

//CurrentUser == this page?
const currentUser = useAuthStore().userInfo;

let editIcon = false;
let friendRequestButton = true;

if (currentUser.uuid == useRoute().query.uuid) {
  editIcon = true;
  friendRequestButton = false;
}

//Board Page
const tempPage = history.state.page
const page = ref(tempPage ? tempPage : 1);

const getPageValue = (value: number) => {
  page.value = value;
};

function formatDate(dateString: any) {
  const date = new Date(dateString);
  date.setHours(date.getHours() - 9);

    return date.getFullYear() + '. ' + (date.getMonth() + 1) + '. ' + date.getDate();
}

onMounted(async () => {

  await fetchData(uuid.value as string);
});

onBeforeRouteUpdate(async (to, from) => {
  if (to.query.uuid !== from.query.uuid) {
    await fetchData(to.query.uuid as string);
    uuid.value = to.query.uuid;
  }
})

//My page data
const signatureModal = ref(false)
const signature = ref('');
const onSelect = (emoji : any) => {
  signature.value = emoji.i
}
const nameModal = ref(false)
const nickname = ref('')
const introduceModal = ref(false)
const introduce = ref('')

function updateInfo() {
  axios.patch("/user/myinfo",  {
    signature: signature.value,
    name: nickname.value,
    introduce: introduce.value
  }, {withCredentials: true}).then(() => {
    fetchData(uuid.value as string);
  })

  signatureModal.value = false;
  nameModal.value = false;
  introduceModal.value = false;
}

</script>

<template>
  <html class="dark">
  <div class="main">
      <div class="profile">
        <div class="container content-gab" v-bind="pageUserInfo">
          <el-popover placement="left-start" title="Friendship Menu" trigger="click" width="fit-content">
            <template #reference>
              <el-button v-if="friendRequestButton" class="friendRequestBtn" color="rgba(0, 189, 126, 0.54)" round>
                <svg height="18px" viewBox="-2 0 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"><circle cx="11" cy="6" r="4"/><path d="M 3 21 A 8 8 0 0 1 11 13"/><path d="M15 13.5 A 2 2 0 1 1 17 15.5 L 17 17.5 M 17 20.5 V 20.01"/></g></svg>
              </el-button>
            </template>
            <div class="container content-gab" style="padding: 1em; gap: 1em;">
              <div v-if="pageUserInfo">
                <el-text class="popoverName">{{pageUserInfo.name}}</el-text><el-text class="popoverName">#{{pageUserInfo.uuid}}</el-text>
              </div>
              <el-text v-if="fMemo">{{fMemo}}</el-text>
              <el-text v-if="fStatus" class="fStatus">{{fStatus}}</el-text>
              <div>
                <el-button v-if="friendshipInfo?.fid == 0" round style="padding: 0 7px" type="primary" @click="friendshipRequestHandler()">
                  <!-- Friendship request -->
                  <svg height="18px" viewBox="-2 -1 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill="none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"><circle cx="11" cy="6" r="4"/><path d="M 3 21 A 8 8 0 0 1 11 13"/><path d="M 15 17 H 19 M 17 15 V 19"/></g></svg>
                </el-button>
                <el-button v-if="friendshipInfo && friendshipInfo.status == 'REQUEST'" round style="padding: 0 7px" type="warning" @click="friendshipRequestDeleteHandler()">
                    <!-- Delete to Friendship request -->
                    <svg height="18px" viewBox="-2 -1 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill= "none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" ><circle cx="11" cy="6" r="4"/><path d="M 3 21 A 8 8 0 0 1 11 13"/><path d="M 15 17 H 19"/></g></svg>
                </el-button>
                <el-button v-if="friendshipInfo?.status != 'BLOCKED'" round style="padding: 0 7px" type="danger" @click="friendshipBlockHandler()">
                  <!-- Friendship block -->
                  <svg height="18px" viewBox="-1 -1 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill= "none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" ><circle cx="11" cy="6" r="4"/><path d="M 3 21 A 8 8 0 0 1 11 13"/><circle cx="17" cy="17" r="3.5" /><path d="M 14.5 14.5 L 19.5 19.5"/></g></svg>
                </el-button>
                <el-button v-if="friendshipInfo && friendshipInfo.status == 'BLOCKED'" round style="padding: 0 7px" type="success" @click="friendshipUnblockHandler()">
                  <!-- Friendship unblock -->
                  <svg height="18px" viewBox="-1 -1 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill= "none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8"><circle cx="11" cy="6" r="4"/><path d="M 3 21 A 8 8 0 0 1 11 13"/></g><g fill= "none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="1.3"><path d="M 18 20.5 A 3.5 3.5 0 1 0 14 18 L 17 16 M 13.5 18 L 12.5 13.5"/></g></svg>
                </el-button>
                <el-button round style="padding: 0 7px" type="info">
                  <!-- Friendship memo -->
                  <svg height="18px" viewBox="0 0 24 24" width="18px" xmlns="http://www.w3.org/2000/svg"><g fill= "none" stroke="#ffffff" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" ><path d="M 5 3 H 19 V 21 H 5 Z M 8 8 H 16 M 8 12 H 16 M 8 16 H 12"/><path d="M 13 9 L 15 7 L 20 12 L 21 11 L 16 6 L 13 9 Z"/></g></svg>
                </el-button>
              </div>
            </div>
          </el-popover>
          <div class="title">Who's There?</div>
          <hr>
          <div class="signature">
            <div class="info-container signature-emoji">
              <p class="info-title">Signature</p>
              <div class="emoji">
                {{ pageUserInfo?.signature }}
              </div>
              <el-button v-if="editIcon" class="editButton" link round size="small" @click="signatureModal=true">
                <el-icon size="15" style="color: #8f8f8f;">
                  <Edit/>
                </el-icon>
              </el-button>
              <el-dialog v-model="signatureModal" title="Update to your Signature Emoji!" width="350px">
                <text style="font-size: 2em">Your choice: {{  signature  }}</text>
                <EmojiPicker :native="true" style="margin-top: 0.3em" theme="dark" @select="onSelect"/>
                <el-button round type="success" @click="updateInfo()">I'm Done!</el-button>
              </el-dialog>
            </div>
            <div class="signature-name content-gab">
              <div class="info-container">
                <el-button v-if="editIcon" class="editButton" link round size="small" @click="nameModal=true">
                  <el-icon size="15" style="color: #8f8f8f;">
                    <Edit/>
                  </el-icon>
                </el-button>
                <el-dialog v-model="nameModal" title="And now, this is your nickname!" width="350px">
                  <el-input v-model="nickname"/>
                  <el-button  round style="margin-top: 1em" type="success" @click="updateInfo()">I'm Done!</el-button>
                </el-dialog>
                <p class="info-title">Name</p>
                <p class="info-content">{{ pageUserInfo?.name }}</p>
              </div>
              <div class="info-container">
                <p class="info-title">UUID</p>
                <p class="info-content" style="padding: 0 0.4em">{{ pageUserInfo?.uuid }}</p>
              </div>
            </div>
          </div>
          <div class="info-container">
            <el-button v-if="editIcon" class="editButton" link round size="small" @click="introduceModal=true">
              <el-icon size="15" style="color: #8f8f8f;">
                <Edit/>
              </el-icon>
            </el-button>
            <el-dialog v-model="introduceModal" title="Wanna change your self-introduction?" width="450px">
              <el-input v-model="introduce" :autosize="{minRows: 8}" type="textarea"/>
              <el-button round style="margin-top: 1em" type="success" @click="updateInfo()">I'm Done!</el-button>
            </el-dialog>
            <p class="info-title">Introduce</p>
            <div class="info-content introduce">{{ pageUserInfo?.introduce }}</div>
          </div>
          <div class="info-container">
            <p class="info-title">Joined on</p>
            <p class="info-content">{{ formatDate(pageUserInfo?.joinedOn) }}</p>
          </div>
        </div>
      </div>
      <div class="activity">
        <div class="container content-gab">
          <div class="title">Their activities</div>
          <hr>
          <ListView v-if="pageUserInfo" :keyword="pageUserInfo.uuid" :page="page" :show="false" :size="5" tableMin="272px" type="u" @sendPage="getPageValue"/>
        </div>
      </div>
    </div>
  </html>
</template>

<style scoped>

  .friendRequestBtn {
    position: absolute;
    height: 30px;
    width: 0;
    top: 22px;
  }

  .editButton {
    position: absolute;
    height: 0;
    width: 0;
    top: 0.5em;
    right: 0.1em;
  }

  hr {
    position: relative;
    margin: -1.3em 0 0 0;
    border: 1px solid rgba(0, 189, 126, 0.13);
  }

  .title {
    color: rgba(255, 255, 255, 0.53);
    font-size: 2em;
    align-self: center;
    padding: 0;
    margin: 0;
  }

  .main {
    display: flex;

    .profile {
      min-width: fit-content;
      width: 35vw;
    }

    .activity {
      width: 65vw;
    }
  }

  .signature {
    display: flex;
    gap: 1.5em;
  }

  .signature-emoji {
    flex: 1;
  }

  .emoji {
    position: relative;
    top: -0.25em;
    font-size: 2.5em;
    height: 0;
  }

  .signature-name {
    flex: 1;
  }

  .info-container {
    font-size: 2em;
    border: 2px solid rgba(0, 189, 126, 0.54);
    border-radius: 0.3em;
    margin: 0 0 0 0;
    text-align: center;
    position: relative;
  }

  .info-title {
    font-size: 0.5em;
    width: fit-content;
    justify-self: center;
    padding: 0 0.5em 0 0.5em;
    z-index: 0;
    position: relative;
    top: -1em;
    background-color: #262626;
    height: 1em;
  }

  .info-content {
    display: flex;
    position: relative;
    top: -0.6em;
    height: 1em;
    width: fit-content;
    justify-self: center;
    font-size: 0.8em;
  }

  .introduce {
    display: block;
    white-space: pre-line;
    height: 10em;
    top: -0.3em
  }

  .container {
    border: 1px solid #BABABA3D;
    border-radius: 0.5em;
    background-color: #FFFFFF0F;
    padding: 1em 2em 2em 2em;
    margin: 0.5em;
    position: relative;
  }

  .content-gab {
    display: flex;
    flex-direction: column;
    gap: 2em;
  }

  .fStatus {
    border: 1px solid #BABABA3D;
    border-radius: 0.5em;
    background-color: rgba(255, 255, 255, 0.11);
    font-size: 1.1em;
    color: #41FF9EB5;
    padding: 0.5em
  }

  .popoverName {
    font-size: 1.2em;
    color: rgba(255, 255, 255, 0.84);
    padding: 0.5em 0.2em 0.5em 0.2em;
    font-weight: bold;
  }
</style>