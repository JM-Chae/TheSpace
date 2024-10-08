<script setup lang = "ts">
import {onMounted, ref} from "vue";
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
}

const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)
const getDto = ref<dto|null>(null)

const path = "Test Community Name" // Community name -> Switch to reactive when after implementing the Community page.

onMounted(()=> {
  const bno = window.history.state.bno;
  axios.get(`http://localhost:8080/board/read/${bno}`)
      .then(res => {getDto.value = res.data;})
      .catch(error => console.error(error));
})

const formatDate = (dateString: string) =>
{
  const date = new Date(dateString);
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

</script>

<template>
  <main>
    <div>
      <H2 class="communityName">{{path}}</H2>
    </div>
    <hr class="mb-2 mt-2" style="background: #25394a; height: 2px; border: 0">
    <el-row>
      <el-col :span="16">
         <div class="mt-3">
           <H3 class="text" v-if="getDto">{{ getDto.title }}</H3>
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
        </div>
      </el-col>
    </el-row>
    <hr class="mb-2 mt-2" style="background: rgba(70,130,180,0.17); height: 1px; border: 0;">
    <div class="mt-3" style="min-height: 15em"><el-text v-if="getDto">{{getDto.content}}</el-text></div>
    <hr class="mb-2 mt-2" style="background: rgba(70,130,180,0.17); height: 1px; border: 0;">
    <el-row><el-col>
      <div class="mt-3" style="text-align: end"><el-button size="large" type="warning" @click="">Modify</el-button></div>
    </el-col></el-row>
  </main>
</template>

<style scoped>
.text{ color: white }
H2{font-size: 2.5em}
H3{font-size: 1.5em}
.name{color: #979797}
</style>