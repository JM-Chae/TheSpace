<script lang = "ts" setup>
import {ref, watch} from "vue";
import axios from "axios";
import {Check} from '@element-plus/icons-vue'
import router from "@/router";

const userinfo = sessionStorage.getItem('userInfo') || "";
const userId = JSON.parse(userinfo).id;

const communityName = ref("")
const description = ref("")
const nameCheck = ref(false)

watch(communityName, (newValue) =>
{
  if (newValue)
    {
      nameCheck.value = false;
    }
})

watch(nameCheck, (newValue) =>
{
  if (newValue == true)
    {
      const element = document.getElementById('name');
      
      if(element && element.parentElement)
        {
          element.parentElement.style.border = "1px solid #00bd7e"
        }
    }
  if (newValue == false)
    {
      const element = document.getElementById('name');

      if(element && element.parentElement)
        {
          element.parentElement.style.border = "1px solid #F56C6C"
        }
    }
})

function reset()
  {
    communityName.value = "";
    description.value = "";
	}

function check(communityName: string)
  {
    axios.get(`community/nameCheck`, {params: {communityName: communityName}})
			.then(res => nameCheck.value = res.data)
	}

function create()
  {
    axios.post(`community`,
			{communityName: communityName.value,
        description: description.value
      }, {params:
					{userid: userId,
            nameCheck: nameCheck.value}})
			.then(() => router.push({path: '/community/home', state: {communityName: communityName.value}}))
  }


</script>

<template>
	<html class="dark">
	<main style="height: 80vh">
		<div style="max-width: 320px; justify-self: center; padding-top: 10vh">
			<div class = "pb-3" style="text-align: center"><h2>What Community do you want?</h2></div>
			<div class = "mt-2">
				<el-input id="name" v-model = "communityName" placeholder = "Community Name" type = "text"></el-input>
				<el-icon v-show="check" color="#00bd7e" font-bold="font-bold" style="float: right; top: -24px; right: 6px"><Check /></el-icon>
			</div>
			<div class = "mt-2">
				<el-input v-model = "description" :autosize = "{minRows: 3}" placeholder = "Enter Description" type = "textarea" @keydown.enter = ""></el-input>
			</div>
			<div class = "mt-2 pt-2" style="display: grid; grid-template-columns: 1fr 1fr 1fr; grid-column-gap: 1em">
				<el-button class = "float-end m-0" type = "danger" @click="reset()">Reset</el-button>
				<el-button class = "float-end m-0" color="#00bd7e" type = "success" @click = "check(communityName)">Check Name</el-button>
				<el-button :disabled="!nameCheck" class = "float-end m-0" type = "primary" @click = "create()">Let's Create!</el-button>
			</div>
		</div>
	</main>
	</html>
</template>

<style scoped>

</style>