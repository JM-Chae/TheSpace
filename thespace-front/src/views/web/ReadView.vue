<script lang="ts" setup>
import {onMounted, ref, watch} from "vue";
import {Delete, Edit, Expand, Hide, View} from '@element-plus/icons-vue'
import axios from "axios";
import {ElMessageBox} from "element-plus";
import router from "@/router";
import type {CategoryInfo, CommunityInfo} from "@/types/domain";
import {goMyPage} from "@/router/GoMyPage"

function getRead() {
  axios.get(`/board/${bno}`)
  .then(res => {
    getDto.value = res.data
    community.value = getDto.value?.communityInfo
  })
  .catch(error => console.error(error));

}

function modify() {
  router.push({
    name: "modify", state:
        {
          title: getDto.value?.title,
          content: getDto.value?.content,
          categoryId: getDto.value?.categoryInfo.id,
          bno: bno,
          fileNames: JSON.stringify(getDto.value?.fileNames),
          community: JSON.stringify(community.value)
        }
  });
}

function getReply() {
  axios.get(`/board/${bno}/reply`)
  .then(res => rdtoList.value = res.data)
  .then(() => {
    if (rdtoList.value) {
      rdtoList?.value
    }
  })
  .catch(error => console.error(error));
}

function deleteReply(rno: number, isNR: number) {
  if (isNR == 0) {
    axios.delete(`/board/${bno}/reply/${rno}`, {withCredentials: true})
    .then(() => {
      getReply()
      if (getDto.value) {getDto.value.rCount--;}
    })
  } else ElMessageBox.alert('You cannot delete a reply that has a nested reply.', 'Delete Confirmation', // The goal is to prevent comment deletion, but instead, to add an API that allows for arbitrary modification of the comment's information.
      {
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true
      })
}

function boardDelete(isR: number) {
  if (isR == 0) {
    axios.delete(`/board/${bno}`, {withCredentials: true})
    .then(() => router.push({path: '/community/home', state: {community: JSON.stringify(community.value)}}))
  } else {
    ElMessageBox.alert('You cannot delete a board that has a reply.', 'Delete Confirmation',
        {
          type: 'warning',
          dangerouslyUseHTMLString: true,
          center: true
        })
  }
}

const deleteBoardAlert = (isR: number) => {
  ElMessageBox.confirm('Are you sure you want to delete this Board?', 'Delete Confirmation',
      {
        cancelButtonText: 'NO',
        confirmButtonText: 'OK',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true,
      })
  .then(() => {
    boardDelete(isR)
  }).catch(() => {
    console.log('')
  })
}

const deleteReplyAlert = (rno: number, isNR: number) => {
  ElMessageBox.confirm('Are you sure you want to delete this reply?', 'Delete Confirmation',
      {
        cancelButtonText: 'NO',
        confirmButtonText: 'OK',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true,
      })
  .then(() => {
    deleteReply(rno, isNR);
  }).catch(() => {
    console.log('')
  })
}

interface dto {
  bno: number,
  categoryInfo: CategoryInfo,
  content: string,
  createDate: string,
  fileNames: string[],
  modDate: string,
  path: string,
  title: string,
  viewCount: number,
  vote: number,
  writer: string,
  writerUuid: string,
  rCount: number,
  communityInfo: CommunityInfo
}

interface rdto {
  rno: number,
  bno: number,
  replyContent: string,
  replyWriter: string,
  replyWriterUuid: string,
  replyDate: string,
  parentRno: number,
  vote: number,
  tag: string,
  childCount: number
  taggedCount: number,
  tagRno: number,
  child?: rdto[]
}

interface rdtos {
  total: number,
  dtoList: Array<rdto>
}

const rdtoList = ref<rdtos>()
const sortedReplies = ref<rdtos | null>(null)

function sortReply(rdtoList: any): rdto[] {
  const map = new Map<number, rdto>();
  const roots: rdto[] = [];

  rdtoList.forEach((reply: rdto) => {
    map.set(reply.rno, {...reply, child: []});
  });

  map.forEach(reply => {
    if (reply.parentRno === 0) {
      roots.push(reply);
    } else {
      const parent = map.get(reply.parentRno);
      if (parent) {
        parent.child?.push(reply);
      } else {
        console.warn(`Parent not found for reply rno=${reply.rno}`);
      }
    }
  });

  return roots;
}

watch(rdtoList, (newVal) => {
  if (newVal?.dtoList) {
    sortedReplies.value = {
      total: newVal.total,
      dtoList: sortReply(newVal.dtoList)
    };
  }
});

const getInfo = sessionStorage.getItem("userInfo") || ""

interface user {
  id: string,
  name: string,
  uuid: string
}

let user: user;
let inputPlaceholder = ''

if (getInfo != "") user = JSON.parse(getInfo)

else {
  user = {id: "Anonymous", name: "Anonymous", uuid: "Anonymous"}
  inputPlaceholder = "Please login to reply"
}

const getDto = ref<dto | null>(null)

const tag = ref<string>('')
const tagRno = ref<number>(0)
const community = ref<CommunityInfo>()
const bno: number = window.history.state.bno;

const replyContent = ref<string>()
const reply = function (parentRno: number) {
  console.log(parentRno)
  axios.post(`/board/${bno}/reply`,
      {
        replyContent: replyContent.value,
        parentRno: parentRno,
        tag: tag.value,
        tagRno: tagRno.value
      }, {withCredentials: true})
  .then(() => replyContent.value = "")
  .then(() => {
    getReply();
    if (getDto.value && getDto.value.rCount != undefined) {
      getDto.value.rCount += 1;
    }
  })
}

async function like(no: number) {
  if (no == 0) {
    axios.put(`/like`,
        {
          userId: <string>user.id,
          rno: 0,
          bno: bno
        }, {withCredentials: true}
    ).then(res => {
      if (getDto.value != null && getDto.value.vote != null) {
        getDto.value.vote += res.data
      }
    })
  } else {
    const response = await axios.put(`/like`,
        {
          userId: <string>user.id,
          rno: no,
          bno: 0
        }, {withCredentials: true}
    );
    return response.data;
  }
}

async function rLikeCount(rno: number, Dto: any) {
  const result = await like(rno)
  Dto.vote += result;
}

const writerCheck = ref(false)

watch(getDto, (newValue) => {
  if (newValue) {
    writerCheck.value = user.uuid === newValue.writerUuid
  }
})

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  date.setHours(date.getHours());
  return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('en-US', {
    hour12: true
  });
}

const toggleNested = (index: number) => {
  isVisible.value[index] = !isVisible.value[index];
  isVisible.value = isVisible.value.map((_, i) => i === index);
  focused.value = false;
}

const isVisible = ref<boolean[]>([]);
const focused = ref<boolean>(true)
const nestedReply = ref<number>(0)
const replyClose = ref<boolean>(true)

const closeAllNestedReplies = () => {
  isVisible.value = isVisible.value.map(() => false);
  focused.value = true;
  nestedReply.value = 0;
  tag.value = '';
  tagRno.value = 0;
};

onMounted(() => {
  getRead()
  getReply()
})


const tempSize = history.state.size;
const tempPage = history.state.page;
const page = ref(tempPage ? tempPage : 1);
const size = ref(tempSize ? tempSize : 10)

function returnBack() {
  const historyURL = history.state.back

  if (historyURL == '/post' || historyURL == '/modify') {
    router.push({path: '/community/home', state: {community: JSON.stringify(community.value)}})
  } else {
    history.replaceState({community: JSON.stringify(community.value), size: size.value, page: page.value}, '')
    router.back()
  }
}

function routing() {
  router.push({path: '/community/home', state: {community: JSON.stringify(community.value)}})
}
</script>

<template>
  <html class="dark">
    <div class="board">
      <div>
        <div style="cursor: pointer; " @click="routing()">
        <h2 v-if="community" style="color: rgba(0,189,126,0.69)">{{ community.name }}</h2>
        </div>
      </div>
      <hr class="mb-2 mt-2" style="background: #25394a; height: 2px; border: 0">
      <el-row>
        <el-col :span="16">
          <div class="mt-3">
            <h3>{{ getDto?.title }}</h3>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="mt-4" style="text-align: end">
            <el-text v-if="getDto" class="name">{{ formatDate(getDto.createDate) }}</el-text>
          </div>
        </el-col>
        <el-col :span="16">
          <div>
            <el-button link @click="goMyPage(getDto?.writerUuid)">
              <el-text class="name">{{ getDto?.writerUuid }}</el-text>
              <el-text>&nbsp/&nbsp</el-text>
              <el-text class="name">{{ getDto?.writer }}</el-text>
            </el-button>
          </div>
        </el-col>
        <el-col :span="8">
          <div style="text-align: end">
            <el-text class="name">Like {{ getDto?.vote }}</el-text>
            <el-text>&nbsp/&nbsp</el-text>
            <el-text class="name">Viewed {{ getDto?.viewCount }}</el-text>
            <el-text>&nbsp/&nbsp</el-text>
            <el-text class="name">Reply {{ getDto?.rCount }}</el-text>
          </div>
        </el-col>
      </el-row>

      <hr class="mb-2 mt-2" style="background: rgba(70,130,180,0.17); height: 0.01em; border: 0;">
      <div class="mt-3" style="min-height: 15em">
        <el-text v-dompurify-html="getDto?.content" class="text"></el-text>
      </div>
      <div class="mt-3 mb-2" style="display: flex; justify-content: flex-start">
        <div v-if="getDto?.fileNames.length">
          <el-popover :show-arrow="false" :width="'fit-content'" effect="dark"
                      placement="right-start" popper-style="text-align: center" trigger="click">
            <template #reference>
              <el-button clss="button" color="#4682B42B" round size="small"
                         style="justify-self: start;" title="Attached Files">
                <el-icon size="15" style="margin-right: 0.5em">
                  <Expand/>
                </el-icon>
                Files List
              </el-button>
            </template>
            <li v-for="file in getDto?.fileNames" style="width: fit-content">
              <a
                  :download="file?.split('_').slice(1).join('_')"
                  :href="`/get/${file?.split('_')[0]}/${file?.split('_').slice(1).join('_')}`">
                {{ file?.split('_').slice(1).join('_') }}
              </a>
            </li>
          </el-popover>
        </div>
        <el-button class="button" color="#ff25cf" round size="small" style="margin-left: auto;"
                   title="Like!" @click="like(0)"><svg fill="none" height="12px" viewBox="0 0 16 16" width="12px" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M1.24264 8.24264L8 15L14.7574 8.24264C15.553 7.44699 16 6.36786 16 5.24264V5.05234C16 2.8143 14.1857 1 11.9477 1C10.7166 1 9.55233 1.55959 8.78331 2.52086L8 3.5L7.21669 2.52086C6.44767 1.55959 5.28338 1 4.05234 1C1.8143 1 0 2.8143 0 5.05234V5.24264C0 6.36786 0.44699 7.44699 1.24264 8.24264Z" fill="#ffffff"></path> </g></svg>
        </el-button>
        <el-button placeholder="Close Reply" round size="small" style="margin-left: 0.5em"
                   type="primary" v-bind:title="replyClose ? 'ReplyHide' : 'ReplyView'"
                   @click="replyClose = !replyClose">
          <el-icon size="15">
            <Hide v-if="replyClose"/>
            <View v-if="!replyClose"/>
          </el-icon>
        </el-button>
        <el-button v-if="writerCheck" round size="small" style="margin-left: 0.5em" title="Modify"
                   type="warning" @click="modify">
          <el-icon size="15">
            <Edit/>
          </el-icon>
        </el-button>
        <el-button v-if="writerCheck && getDto" round size="small" style="margin-left: 0.5em"
                   title="Delete" type="danger" @click="deleteBoardAlert(getDto.rCount)">
          <el-icon size="15">
            <Delete/>
          </el-icon>
        </el-button>
      </div>
    </div>

    <hr style="background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
    <div v-show="replyClose && sortedReplies?.total" class="reply">
      <div class="replyList">
        <div class="p-3 m-3 pt-4"
             style="background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">

          <el-space :size="20" fill="fill" style="display: flex;">
            <ul v-for="(rDto, index) in sortedReplies?.dtoList" :key="rDto.rno" class="list"
                style="padding-left: 0">
              <li class="list" style="list-style-type: none;" @click="toggleNested(index); nestedReply = rDto.rno">
                <div style="display: grid;">
                  <div id="replyList">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <div style="flex-grow: 1; margin-bottom: auto; min-width: 10em; max-width: 10em; margin-right: 0.5em;">
                        <el-popover :width="10" placement="top" popper-style="text-align: center" title="UUID" trigger="hover">
                          <template #reference>
                            <el-button class="name" link style="display: inline-block; color: white; max-width: 10em;">
                              <el-button link @click="goMyPage(getDto?.writerUuid)">
                                <span style="overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">{{ rDto.replyWriter }}
                                </span>
                              </el-button>
                            </el-button>
                          </template>
                          <el-button link @click="goMyPage(getDto?.writerUuid)">
                          {{ rDto.replyWriterUuid }}
                          </el-button>
                        </el-popover>
                      </div>
                      <div style="text-align: left; margin-left: auto; flex-grow: 20">
                        <el-text class="text" style="white-space: pre-wrap;">{{ rDto.replyContent }}</el-text>
                      </div>
                      <div style="margin-left: 10px; margin-bottom: auto; text-align: right; flex-grow: 1; min-width: 160px">
                        <el-text class="text">{{ formatDate(rDto.replyDate) }}</el-text>
                      </div>
                    </div>

                    <div class="pt-1" style="display: flex; justify-content: end; align-items: center;">
                      <div style="margin-left: 10.5em; display: inline-block;">
                        <el-text>Like: {{ rDto.vote }}</el-text>
                      </div>
                      <div style="display: inline-block;">
                        <el-button class="button" color="#ff25cf" round size="small" style="margin-left: 1em;" title="Like!"
                                   @click="rLikeCount(rDto.rno, rDto)" @click.stop><svg fill="none" height="12px" viewBox="0 0 16 16" width="12px" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M1.24264 8.24264L8 15L14.7574 8.24264C15.553 7.44699 16 6.36786 16 5.24264V5.05234C16 2.8143 14.1857 1 11.9477 1C10.7166 1 9.55233 1.55959 8.78331 2.52086L8 3.5L7.21669 2.52086C6.44767 1.55959 5.28338 1 4.05234 1C1.8143 1 0 2.8143 0 5.05234V5.24264C0 6.36786 0.44699 7.44699 1.24264 8.24264Z" fill="#ffffff"></path> </g></svg></el-button>
                        <el-button v-if="rDto.replyWriterUuid == user.uuid" round size="small" style="margin-left: 0.5em" title="Delete" type="danger"
                                   @click="deleteReplyAlert(rDto.rno, rDto.childCount > 0 ? 1 : 0);" @click.stop>
                          <el-icon size="15"><Delete /></el-icon>
                        </el-button>
                        </div>
                    </div>
                  </div>

                  <ul v-if="rDto.child?.length" class="p-2 m-3 mt-2 mb-0">
                    <li v-for="(child) in rDto.child" id="nestedList" :key="child.rno" style="border-radius: 0.5em; border: 0.1em solid #494949; padding: 1em; margin: 0.5em 0 0.5em 0; background-color: rgb(46,46,46); list-style-type: none;"
                        @click="tag = child.replyWriter + ' ' + child.replyWriterUuid + ' To'; tagRno = child.rno">
                      <div style="display: grid;">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                          <div style="flex-grow: 1; margin-bottom: auto; min-width: 10em; max-width: 10em; margin-right: 0.5em;">
                            <el-popover :width="10" placement="top" popper-style="text-align: center" title="UUID" trigger="hover">
                              <template #reference>
                                <el-button class="name" link style="display: inline-block; color: white; max-width: 10em;">
                                  <el-button link @click="goMyPage(getDto?.writerUuid)">
                                    <span style="overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">
                                    {{ child.replyWriter }}
                                    </span>
                                  </el-button>
                                </el-button>
                              </template>
                              <el-button link @click="goMyPage(getDto?.writerUuid)">
                              {{ child.replyWriterUuid }}
                              </el-button>
                            </el-popover>
                          </div>
                          <div style="text-align: left; flex-grow: 20">
                            <span style="color: #919191">┗　</span>
                            <el-text v-if="child.tag != ''"
                                     v-model="child.tag">
                              <el-popover
                                          :width="10" placement="top"
                                          popper-style="text-align: center" title="UUID"
                                          trigger="hover">
                                <template #reference>
                                  <el-button class="name" link style="margin-left: 0; padding-left: 0; display: inline-block; max-width: 10em;">
                                    <span style="margin-right: 0.3em; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">
                                      <el-button link style="margin-left: 0; padding-left: 0; color: rgba(41,198,255,0.75);" @click="goMyPage(getDto?.writerUuid)">
                                        {{child.tag.split(' ')[2] }} {{child.tag.split(' ')[0] }}
                                      </el-button>
                                    </span>
                                  </el-button>
                                </template>
                                <el-button link @click="goMyPage(getDto?.writerUuid)">
                                {{child.tag.split(' ')[1] }}
                                </el-button>
                              </el-popover>
                            </el-text>
                            <el-text class="text" style="white-space: pre-wrap;">{{ child.replyContent }}</el-text>
                          </div>
                          <div style="margin-left: 10px; margin-bottom: auto; text-align: right; flex-grow: 1; min-width: 160px">
                            <el-text class="text">{{ formatDate(child.replyDate) }}</el-text>
                          </div>
                        </div>
                        <div class="pt-1" style="display: flex; justify-content: end; align-items: center;">
                          <div style="margin-left: 12.6em; display: inline-block;">
                            <el-text>Like: {{ child.vote }}</el-text>
                          </div>
                          <div style="display: inline-block;">
                            <el-button class="button" color="#ff25cf" round size="small" style="margin-left: 1em;" title="Like!"
                                       @click="rLikeCount(child.rno, child)" @click.stop><svg fill="none" height="12px" viewBox="0 0 16 16" width="12px" xmlns="http://www.w3.org/2000/svg"><g id="SVGRepo_bgCarrier" stroke-width="0"></g><g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g><g id="SVGRepo_iconCarrier"> <path d="M1.24264 8.24264L8 15L14.7574 8.24264C15.553 7.44699 16 6.36786 16 5.24264V5.05234C16 2.8143 14.1857 1 11.9477 1C10.7166 1 9.55233 1.55959 8.78331 2.52086L8 3.5L7.21669 2.52086C6.44767 1.55959 5.28338 1 4.05234 1C1.8143 1 0 2.8143 0 5.05234V5.24264C0 6.36786 0.44699 7.44699 1.24264 8.24264Z" fill="#ffffff"></path> </g></svg></el-button>
                            <el-button v-if="child.replyWriterUuid === user.uuid" round size="small" style="margin-left: 0.5em" title="Delete"
                                       type="danger" @click="deleteReplyAlert(child.rno, child.taggedCount)" @click.stop>
                              <el-icon size="15"><Delete /></el-icon>
                            </el-button>
                            </div>
                        </div>
                      </div>
                    </li>
                  </ul>

                  <div v-show="isVisible[index]" class="nestedReplyPost p-3 m-3 pb-2 pt-2 mt-2"
                       style="background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)"
                       @click.stop>
                    <div class="mb-1" style="display: flex">
                      <el-text style="color:rgba(97,255,176,0.8)">{{ user.name }}</el-text>
                      <el-button class="mb-1" link style="margin-left: auto" @click="closeAllNestedReplies()" >x</el-button>
                    </div>
                    <el-input v-model="replyContent" :autosize="{minRows: 3}" type="textarea"
                              :placeholder="tag ? tag.split(' ')[2] + ' ' + tag.split(' ')[0] : undefined"/>
                    <div style="text-align: end">
                      <el-button class="mt-2" round size="small" type="primary" @click="reply(rDto.rno)">Reply</el-button>
                    </div>
                  </div>

                  <div v-if="sortedReplies && sortedReplies.dtoList.length - 1 != index" class="mt-2" style="border-bottom: 1px dashed rgba(70,130,180,0.17);"></div>
                </div>
              </li>
            </ul>
          </el-space>

        </div>
        <hr style="background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
      </div>
    </div>

    <div v-if="focused" class="replyPost p-3 m-3 pb-2 pt-2"
         style="background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
      <div class="mb-1" style="color:rgba(97,255,176,0.8)">{{ user.name }}</div>
      <el-input v-model="replyContent" :autosize="{minRows: 3}" :disabled="inputPlaceholder != ''" :placeholder="inputPlaceholder" class="replyInput" type="textarea" @click.stop/>
      <div style="text-align: end">
        <el-button class="mt-2" round size="small" type="primary" @click="reply(0);">Reply</el-button>
      </div>
    </div>
    <div>
      <el-button color="#00bd7e" round style="position: fixed; top: 3vh; right: 2vw" @click="returnBack()">Return Back</el-button>
    </div>
  </html>
</template>

<style scoped>
.replyInput {
  --el-disabled-bg-color: #00000000;
}
.text {
  color: rgba(255, 255, 255, 0.82);
  font-size: 1em;
  font-weight: lighter;
}

h2 {
  font-size: 2.5em
}

h3 {
  font-size: 1.5em;
  color: rgba(248, 248, 248, 0.89);
}
</style>