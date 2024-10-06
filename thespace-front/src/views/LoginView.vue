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
      .then(res => {getInfo().then(() =>{ window.location.href = res.data.redirectUrl})})
      .catch (error => {console.error(error)})


 async function getInfo()
 {
   try {
     let res = await axios.get("http://localhost:8080/user/info", {withCredentials: true});
     return sessionStorage.setItem("userInfo", JSON.stringify(res.data));
   } catch (error) {
     return console.error("Error fetching user info" + error);
   }
 }

  localStorage.setItem("login", "true")
}
</script>

<template>
  <main>
    <div class="pb-3"><h2>Welcome to Your Space!</h2></div>

  <div class="mt-2"><el-input type="text" v-model="id" placeholder="Enter ID"></el-input></div>
  <div class="mt-2"><el-input type="password" v-model="pw" placeholder="Enter Password"></el-input></div>
  <div class="mt-2 pt-2">
    <el-checkbox type="primary" v-model="remember">Remember Me</el-checkbox>
    <el-button class="float-end" type="primary" @click="login">Login</el-button>
  </div>
  </main>
</template>

<style scoped>

</style>