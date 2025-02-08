<script lang = "ts" setup>
import {ref, watch} from 'vue'
import axios from "axios";
import router from "@/router";

const id = ref('')
const pw = ref('')
const email = ref('')
const nickname = ref('')
const isOK = ref(false)

const nameWarn = ref(true)
const idWarn = ref(true)
const emailWarn =ref(true)
const pwWarn = ref(true)
const idCheck = ref(true)

const pattern = /^[A-Za-z0-9_\\.\-]+@[A-Za-z0-9\-]+\.[A-za-z0-9\-]+/;

watch(pw, (newValue) =>
{
  if (newValue.length < 6 || newValue.length > 20)
    {
      pwWarn.value = true;
    } else if (newValue.length > 5 || newValue.length < 21)
    {
      pwWarn.value = false;
    }
})

watch(email, (newValue) =>
{
  if(pattern.test(newValue))
    {
      emailWarn.value = false;

    }else if (!pattern.test(newValue))
    {
      emailWarn.value = true;
    }
})

watch(nickname, (newValue) =>
{
  if(newValue.length < 2 || newValue.length > 20)
    {
      nameWarn.value = true;

    }else if (newValue.length > 1 || newValue.length < 21)
    {
      nameWarn.value = false;
    }
})

watch(id, (newValue) =>
{
  if (newValue)
    {
      isOK.value = false;
    }

  if (newValue.length < 6 || newValue.length > 20)
    {
      idWarn.value = true;
    } else if (newValue.length > 5 || newValue.length < 21)
    {
      idWarn.value = false;
    }
})


const join = () =>
  {
    axios.post('/user', {
      id: id.value,
      password: pw.value,
      email: email.value,
      name: nickname.value,
    }, {params: {checkid: isOK.value}})
      .then(() => router.push({ path: '/user/login' }))
			.then(() => location.reload())
  }

const check = () =>
  {
    if (idWarn.value == false)
      {
        axios.get('/user/checkid', {
          params: {id: id.value}
        })
          .then(res => {
            isOK.value = res.data
            idCheck.value = res.data
          })
      }
  }

</script>

<template>
	<html class = "dark">
	<main style = "height: 80vh">
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
		<div class="pt-5" style = "max-width: 300px; justify-self: center;">
			<div class = "pb-3 pt-3" style="text-align: center">
        <h2 style="font-size: 1.8em">Welcome to Your Space!</h2>
      </div>
			<div class = "mt-2">
				<el-input v-model = "id" placeholder = "Enter ID" type = "text"></el-input>
				<div v-if = "idWarn" style = "color:#c36169">Please enter between 6 and 20 characters.</div>
			</div>
			<div class = "mt-2">
				<el-input v-model = "pw" placeholder = "Enter Password" type = "password"></el-input>
				<div v-if = "pwWarn" style = "color:#c36169">Please enter between 6 and 20 characters.</div>
			</div>
			<div class = "mt-2">
				<el-input v-model = "email" placeholder = "Enter E-mail" type = "text"></el-input>
			</div>
			<div v-if = "emailWarn" style = "color:#c36169">Incorrect email address.</div>
			<div class = "mt-2">
				<el-input v-model = "nickname" placeholder = "Enter Name" type = "text"></el-input>
				<div v-if = "nameWarn" style = "color:#c36169">Please enter between 2 and 20 characters.</div>
			</div>
			<div class = "mt-2">
				<el-button v-if = "!isOK" style = "width: 35%" type = "danger" @click = "check">ID Check</el-button>
				<el-button v-if = "isOK" style = "width: 35%" type = "primary">Checked!</el-button>
				<el-button class = "float-end" color="#00bd7e" style = "width: 35%" type = "success" @click = "join">Join</el-button>
			</div>
      <div v-if="!idCheck" class = "mt-2" style = "color:#c36169">This ID is already existed!</div>
		</div>
	</main>
	</html>
</template>

<style scoped>

</style>