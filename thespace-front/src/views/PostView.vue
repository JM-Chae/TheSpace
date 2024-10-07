<script setup lang = "ts">
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const title = ref("")
const content = ref("")
const categoryName = ref("")
const bno = ref("")



const path = "aaa" // Community name -> Switch to reactive when after implementing the Community page.
const categories = ref()
const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)


axios.get(`http://localhost:8080/getcategory/${path}`).then(res => categories.value = res.data).catch(error => console.error(error))

const post = function ()
{
  axios.post("http://localhost:8080/board/post",
      {title: title.value,
        content: content.value,
        writer: user.name,
        writerUuid: user.uuid,
        categoryName: categoryName.value})
      .then(res => {bno.value = res.data}).catch(error => {alert(error);})
      .then(() => router.push({name: "read", state: { bno: bno.value }}))
}
</script>

<template>
  <main>
    <div>
      <el-row :gutter="10">
        <el-col :span="12">
            <el-select size="large" id="select-category" class="form-control" placeholder="Choose category" v-model="categoryName">
              <el-option v-for="category in categories" :key="category.categoryId" :value="category.categoryName">
                {{category.categoryName}}
              </el-option>
            </el-select>
        </el-col>
        <el-col :span="12">
            <el-input size="large" class readonly v-model="path">{{path}}</el-input>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="10">
      <el-col :span="12"/>
      <el-col :span="3">
        <div class="mt-3" style="text-align-last: center">
          <el-input :value="user.uuid" readonly >{{user.uuid}}</el-input>
        </div>
      </el-col>
      <el-col :span="9">
        <div class="mt-3">
          <el-input :value="user.name" readonly >{{user.name}}</el-input>
        </div>
      </el-col>
    </el-row>
    <div class="mt-3"><el-input size="large" v-model="title" placeholder="Enter title"/> </div>
    <div class="mt-3"><el-input size="large" type="textarea" v-model="content" placeholder="Enter content" :autosize="{minRows: 10}"/></div>
    <div class="mt-3"><el-button size="large" type="primary" @click="post()">Submit</el-button></div>

  </main>
</template>

<style scoped>

</style>