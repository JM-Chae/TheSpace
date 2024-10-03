<script setup lang = "ts">
import {ref} from "vue";
import axios from "axios";

const id = ref("")
const pw = ref("")
const remember = ref(false)


const login = function ()
{
  axios.post("http://localhost:8080/user/login",
      {id: id.value, password: pw.value, remember: remember.value},
      {headers: {"Content-Type": "multipart/form-data"}, withCredentials: true})
      .then(res => window.location.href = res.data.redirectUrl).catch (error => {
    console.error(error)})
}
</script>

<template>
  <main>
  <div class="mt-2"><el-input type="text" v-model="id" placeholder="Enter ID"></el-input></div>
  <div class="mt-2"><el-input type="password" v-model="pw" placeholder="Enter Password"></el-input></div>
  <div class="mt-2"><el-checkbox class="pe-5" type="primary" v-model="remember">Remember Me</el-checkbox><el-button type="primary" @click="login">Login</el-button></div>
  </main>
</template>

<style scoped>

</style>