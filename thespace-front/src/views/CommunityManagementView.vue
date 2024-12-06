<script lang = "ts" setup>
import {onMounted, ref} from 'vue'
import axios from "axios";
import router from "@/router";

const communityname = history.state.communityname;
const page = ref(history.state.page)
const size = ref(history.state.size)
const userinfo = sessionStorage.getItem('userInfo') || ""
const userId = JSON.parse(userinfo).id

const getPageValue = (value: number) => {
  page.value = value;
};

const getSizeValue = (value: number) => {
  size.value = value;
}

function getCommunity()
  {
    axios.get(`/community/${communityname}`).then(res => community.value = res.data)
      .then(() => des.value = community.value.description)
  }

onMounted(() =>
{
  getCommunity();
})

const community = ref()
const desReadOnly = ref(true)
const des = ref()
const temp = ref()

function desModify()
  {
    desReadOnly.value = !desReadOnly.value
		temp.value = des.value
	}

function cancel()
  {
    des.value = temp.value
		desReadOnly.value = !desReadOnly.value
	}

function done()
  {
    axios.put(`community/modify`,
			{
        communityId: community.value.communityId,
        communityName: communityname,
				description: des.value
      },
      {params: {userId: userId}})
			.then(() => getCommunity());
		
		desReadOnly.value = !desReadOnly.value;
  }

function returnHome()
  {
		router.push({path: '/community/home', state: {communityname: communityname, size: size.value, page: page.value}})
	}

</script>

<template>
	<html class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<el-text v-if="communityname" v-model="communityname" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{communityname}}</el-text>
		<div>
			<el-input v-if="des" v-model="des" :autosize = "{minRows: 3}" :readonly="desReadOnly" class="p-2" style="font-size: 1.2em; width: 80%" type="textarea">{{des}}</el-input>
			<el-button v-if="desReadOnly" class="mb-2" style="margin-left: 11%" type="warning" @click="desModify()">Modify</el-button>
			<div v-if="!desReadOnly" style="display: inline-block; margin-left: 0.7em">
				<el-button class="mb-2 ms-1" color="#00bd7e" style="width: 40%;" type="success" @click="done()">Done</el-button>
				<el-button class="mb-2 ms-4" style="width: 40%;" type="danger" @click="cancel()">Cancel</el-button>
			</div>
		</div>
	</div>
	<div>
		<el-button color="#00bd7e" round style="position: fixed; top: 93vh; right: 2vw" @click="returnHome()">Return Home</el-button>
	</div>
	
	<ListViewAdmin :page="page" :path="communityname" :size="size" @sendPage="getPageValue" @sendSize="getSizeValue"/>
	</html>
</template>

<style scoped>

</style>