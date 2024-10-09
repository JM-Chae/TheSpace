<script setup lang = "ts">
import {onMounted, ref, watch} from "vue";
import axios from "axios";

interface dto
{
  bno: number,
  categoryName: string,
  content: string,
  createDate: string,
  fileNames: [],
  modDate: string,
  path: string,
  title: string,
  viewCount: number,
  vote: number,
  writer: string,
  writerUuid: string,
  rCount: number
}

interface rdto
{
  rno: number,
  bno: number,
  replyContent: string,
  replyWriter: string,
  replyWriterUuid: string,
  replyDate: string,
  path: string,
  vote: number
}

const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)
const getDto = ref<dto|null>(null)

const path = "Test Community Name" // Community name -> Switch to reactive when after implementing the Community page.
const bno = window.history.state.bno;
onMounted(()=> {
  axios.get(`http://localhost:8080/board/read/${bno}`)
      .then(res => {getDto.value = res.data;})
      .catch(error => console.error(error));
})

const replyContent = ref("")
const nestedReply = ref("")
const reply = function ()
{
  axios.post(`http://localhost:8080/board/${bno}/reply`,
      {
        bno: bno,
        replyContent: replyContent.value,
        replyWriter: user.name,
        replyWriterUuid: user.uuid,
        path: bno+"/"+nestedReply.value
      })
      .then(() => replyContent.value="")
}

const like = function ()
{
  const rno = 0 // If click like button then return rno value
  axios.put(`http://localhost:8080/like`,
      {
        userId: <string>user.id,
        rno: rno,
        bno: bno
      })
}


const writerCheck = ref(false)

watch(getDto,(newValue) => {if(newValue) {writerCheck.value = user.uuid === newValue.writerUuid}})

const formatDate = (dateString: string) =>
{
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString('en-US', { hour12: true });
}

</script>

<template>
  <main >
    <div>
      <h2 class="communityName">{{path}}</h2>
    </div>
    <hr class="mb-2 mt-2" style="background: #25394a; height: 2px; border: 0">
    <el-row>
      <el-col :span="16">
         <div class="mt-3">
           <h3 class="text" v-if="getDto">{{ getDto.title }}</h3>
         </div>
      </el-col>
      <el-col :span="8">
        <div class="mt-4" style="text-align: end">
          <el-text class="name" v-if="getDto">{{ formatDate(getDto.createDate) }}</el-text>
        </div>
      </el-col>
      <el-col :span="16">
        <div>
            <el-text class="name" v-if="getDto">{{getDto.writerUuid}}</el-text>
          <el-text>&nbsp/&nbsp</el-text>
            <el-text class="name" v-if="getDto">{{getDto.writer}}</el-text>
        </div>
      </el-col>
      <el-col :span="8">
        <div style="text-align: end">
          <el-text class="name" v-if="getDto">Like {{getDto.vote}}</el-text>
          <el-text>&nbsp/&nbsp</el-text>
          <el-text class="name" v-if="getDto">View {{getDto.viewCount}}</el-text>
          <el-text>&nbsp/&nbsp</el-text>
          <el-text class="name" v-if="getDto">Reply {{getDto.rCount}}</el-text>
        </div>
      </el-col>
    </el-row>
    <hr class="mb-2 mt-2" style="background: rgba(70,130,180,0.17); height: 0.01em; border: 0;">
    <div class="mt-3" style="min-height: 15em"><el-text v-if="getDto">{{getDto.content}}</el-text></div>
    <div v-if="writerCheck" class="mt-3 mb-2" style="text-align: end">
      <el-button style="margin-left: 0.5em" class="button" round color="#ff25cf" size="small" @click="like">Like!</el-button>
      <el-button style="margin-left: 0.5em" round size="small" type="primary" @click="">Close Reply</el-button>
      <el-button style="margin-left: 0.5em" round size="small" type="warning" @click="">Modify</el-button>
    </div>
    <div class="replyList">
      <hr style="background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
    </div>
    <hr style="background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
    <div class="replyPost p-3 m-3 pb-2 pt-2" style="background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
      <div class="mb-1" style="color:rgba(97,255,176,0.8)">{{ user.name }}</div>
      <el-input v-model="replyContent" type="textarea" :autosize="{minRows: 3}"/>
      <div style="text-align: end">
       <el-button class="mt-2" round size="small" type="primary" @click="reply">Reply</el-button>
      </div>
    </div>


  </main>
</template>

<style scoped>
.text{ color: white }
h2{font-size: 2.5em}
h3{font-size: 1.5em}
</style>