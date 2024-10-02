<script setup lang = "ts">
import {ref} from "vue";
import axios from "axios";

const id = ref("")
const pw = ref("")

const login = function ()
{
  axios.post("http://localhost:8080/user/login",
      {id: id.value, password: pw.value})
      .then(res => {console.log(res.data)}).catch (error => {
    console.error(error)})
}
</script>

<template>
  <div class="mt-2"><el-input type="text" v-model="id" placeholder="Enter ID"></el-input></div>
  <div class="mt-2"><el-input type="password" v-model="pw" placeholder="Enter Password"></el-input></div>
  <div class="mt-2"><el-button type="primary" @click="login">Login</el-button></div>

</template>

<style scoped>

</style>