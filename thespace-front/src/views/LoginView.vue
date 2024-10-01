<script setup lang = "ts">
import {ref} from "vue";
import axios from "axios";

const id = ref("")
const pw = ref("")

const login = function ()
{
  axios.post("/user/login")
}
</script>

<template>
  <div class="mt-2"><el-input type="text" v-model="id" placeholder="Enter ID"></el-input></div>
  <div class="mt-2"><el-input type="password" v-model="pw" placeholder="Enter Password"></el-input></div>
  <div class="mt-2"><el-button type="primary" @click="login">Login</el-button></div>

</template>

<style scoped>

</style>