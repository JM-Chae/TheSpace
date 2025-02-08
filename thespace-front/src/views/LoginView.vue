<script lang = "ts" setup>
import {ref} from "vue";
import axios from "axios";
import router from "@/router";

const id = ref("")
const pw = ref("")
const remember = ref(false)

const login = function ()
  {
    axios.post("user/login",
      {id: id.value, password: pw.value, remember: remember.value},
      {headers: {"Content-Type": "multipart/form-data"}, withCredentials: true})
      .then(res =>
      {
        if(remember.value == true)
          {
            document.cookie = 'isRemember=true; max-age=604800'
          }
        getInfo().then(() =>
        {
          window.location.href = res.data.redirectUrl
        })
      })
      .catch(error =>
      {
        console.error(error)
      })


    async function getInfo()
      {
        try
          {
            let res = await axios.get("/user/info", {withCredentials: true});
            return sessionStorage.setItem("userInfo", JSON.stringify(res.data));
          } catch (error)
          {
            return console.error("Error fetching user info" + error);
          }
      }

    sessionStorage.setItem("login", "true")
  }

const join = () =>
  {
    router.push('/user/join')
	}
</script>

<template>
	<html class="dark">
	<main style="height: 80vh">
		<div style="max-width: 350px; justify-self: center; padding-top: 5vh">
      <div class="pt-5 pe-3 pb-3" style="display: flex; justify-content: center;">
        <div style="display: flex;">
          <svg height="100" overflow="hidden" viewBox="0 0 585 585" width="100">
            <defs>
              <clipPath id="clip0">
                <rect height="585" width="585" x="1908" y="731"/>
              </clipPath>
              <linearGradient id="stroke1" gradientUnits="userSpaceOnUse" spreadMethod="reflect" x1="2493.24"
                              x2="1907.76" y1="730.76" y2="1316.24">
                <stop offset="0" stop-color="#373737"/>
                <stop offset="0.2" stop-color="#373737"/>
                <stop offset="0.5" stop-color="#00DE64"/>
                <stop offset="0.79" stop-color="#373737"/>
                <stop offset="1" stop-color="#373737"/>
              </linearGradient>
              <linearGradient id="fill2" gradientUnits="userSpaceOnUse" spreadMethod="reflect" x1="2365"
                              x2="2035" y1="1023.15" y2="1023.15">
                <stop offset="0" stop-color="#373737"/>
                <stop offset="0.25" stop-color="#373737"/>
                <stop offset="0.5" stop-color="#00DE64"/>
                <stop offset="0.75" stop-color="#373737"/>
                <stop offset="1" stop-color="#373737"/>
              </linearGradient>
            </defs>
            <g clip-path="url(#clip0)" transform="translate(-1908 -731)">
              <path
                  d="M1917.5 1023.5C1917.5 867.203 2044.2 740.5 2200.5 740.5 2356.8 740.5 2483.5 867.203 2483.5 1023.5 2483.5 1179.8 2356.8 1306.5 2200.5 1306.5 2044.2 1306.5 1917.5 1179.8 1917.5 1023.5Z"
                  fill="#262626" fill-rule="evenodd" stroke="url(#stroke1)"
                  stroke-linecap="round" stroke-miterlimit="8" stroke-width="19.4792"/>
              <path
                  d="M2070.77 1062.92 2089.78 1062.92 2089.78 1127.38 2308.6 1127.38 2308.6 1062.92 2327.94 1062.92 2327.94 1146.23 2070.77 1146.23Z"
                  fill="url(#fill2)" fill-rule="evenodd"/>
              <text fill="#000000" fill-opacity="0" font-family="Arial,Arial_MSFontService,sans-serif"
                    font-size="83.3057" x="2070.77" y="1135.82">␣
              </text>
            </g>
          </svg>
        </div>
        <div class="ms-2" style="display: flex; flex-direction: column;">
          <text style="color: #00DE64; font-size: 3em">The Space</text>
          <text class="ms-1" style="font-size: 0.7em">Hope to find your own space in The Space.</text>
        </div>
      </div>
      <div class = "pt-5 pb-3">
        <h2 style="text-align: center">Welcome to Your Space!</h2>
      </div>
      <div class = "mt-2" style="width: 250px; height: 75px; display: flex; flex-direction: column; justify-content: space-between; justify-self: center">
        <el-input v-model = "id" placeholder = "Enter ID" type = "text"></el-input>
        <el-input v-model = "pw" placeholder = "Enter Password" type = "password" @keydown.enter = "login"></el-input>
      </div>
      <div class = "mt-2 pt-2" style="max-width: 300px; justify-self: center;">
        <el-checkbox v-model = "remember" type = "primary">Remember Me</el-checkbox>
        <el-button class = "float-end" type = "primary" @click = "login">Login</el-button>
        <el-button class = "float-end me-2" type = "success" @click = "join">Join</el-button>
      </div>
		</div>
	</main>
	</html>
</template>

<style scoped>

</style>