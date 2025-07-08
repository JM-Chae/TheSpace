<script lang="ts" setup>
import {Edit} from '@element-plus/icons-vue'
import {onMounted, ref} from "vue";
import axios from "axios";
import {useRoute} from "vue-router";
import EmojiPicker from 'vue3-emoji-picker';
import 'vue3-emoji-picker/css';

function getMyPageInfo() {
  axios.get(`/user/${uuid}/mypage`).then((res) => {
    pageUserInfo.value = res.data})
}

interface user {
  signature: string,
  name: string,
  uuid: string,
  introduce: string,
  joinedOn: string
}

const route = useRoute()
const uuid = route.query.uuid
const pageUserInfo = ref<user>();

const getInfo = sessionStorage.getItem("userInfo") || ""
const currentUser = JSON.parse(getInfo)

let editIcon = false;

if (currentUser.uuid == uuid) {
  editIcon = true;
}

const tempPage = history.state.page
const page = ref(tempPage ? tempPage : 1);

const getPageValue = (value: number) => {
  page.value = value;
};

function formatDate(dateString: any)
{
  const date = new Date(dateString);
  date.setHours(date.getHours() - 9);

    return date.getFullYear() + '. ' + (date.getMonth() + 1) + '. ' + date.getDate();
}

onMounted(() => {

  getMyPageInfo()
})

const signature = ref('');

const signatureModal = ref(false)
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
    getMyPageInfo()
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
          <div class="title">Who's There?</div>
          <hr>
          <div class="signature">
            <div class="info-container signature-emoji">
              <p class="info-title">Signature</p>
              <div class="emoji">
                {{ pageUserInfo?.signature }}
              </div>
              <el-button v-if="editIcon" class="editButton" link round size="small" @click="signatureModal=true">
                <el-icon size="15" style="color: #8f8f8f; top: -0.6em; right: -2.7em;">
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
                  <el-icon size="15" style="color: #8f8f8f; top: 0.3em; right: -2.6em;">
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
                <p class="info-content">{{ pageUserInfo?.uuid }}</p>
              </div>
            </div>
          </div>
          <div class="info-container">
            <el-button v-if="editIcon" class="editButton" link round size="small" @click="introduceModal=true">
              <el-icon size="15" style="color: #8f8f8f; top: 0.5em; right: -7.7em;">
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
          <ListView :keyword="pageUserInfo?.uuid" :page="page" :show="false" :size="5" tableMin="272px" type="u" @sendPage="getPageValue"/>
        </div>
      </div>
    </div>
  </html>
</template>

<style scoped>

  .editButton {
    position: absolute;
    height: 0;
    width: 0;
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
      width: 30vw;
    }

    .activity {
      width: 60vw;
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
  }

  .content-gab {
    display: flex;
    flex-direction: column;
    gap: 2em;
  }
</style>