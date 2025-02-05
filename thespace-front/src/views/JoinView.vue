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
		<div style = "max-width: 300px; justify-self: center; padding-top: 10vh">
			<div class = "pb-3"><h2>Welcome to Your Space!</h2></div>
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