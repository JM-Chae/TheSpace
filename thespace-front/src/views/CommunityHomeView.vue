<script lang = "ts" setup>
import {ref} from 'vue'
import axios from "axios";
import router from "@/router";

const communityname = history.state.communityname;

axios.get(`/community/${communityname}`).then(res => community.value = res.data)

const tempSize = history.state.size
const tempPage = history.state.page

const page = ref(tempPage ? tempPage : 1);
const size = ref(tempSize ? tempSize : 10)

const getPageValue = (value: number) => {
  page.value = value;
};

const getSizeValue = (value: number) => {
  size.value = value;
}

const community = ref()

function formatDate(dateString: string)
  {
    const date = new Date(dateString);
    date.setHours(date.getHours() - 9);
    
    return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('en-US', {
      hour12: true
    });
  }

function management()
  {
		router.push({path: '/community/management', state: {communityname: communityname, page: page.value, size: size.value}})
	}
</script>

<template>
	<html class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; grid-template-columns: 1fr 1fr;  width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
	<el-text v-if="communityname" v-model="communityname" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{communityname}}</el-text><div></div>
	<el-text v-if="community" v-model="community" class="p-2" style="font-size: 1.2em">{{community.description}}</el-text><div></div>
		<el-text v-if="community" v-model="community" class="p-2">{{formatDate(community.createDate)}}</el-text><div><el-button class="mb-2 me-2" style="display: flex; margin-left: auto" type="warning" @click="management()">Management</el-button></div>
	</div>
	<ListView :page="page" :path="communityname" :size="size" @sendPage="getPageValue" @sendSize="getSizeValue"/>
	</html>
</template>

<style scoped>

</style>