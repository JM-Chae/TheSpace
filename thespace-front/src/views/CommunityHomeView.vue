<script lang = "ts" setup>
import {ref} from 'vue'
import axios from "axios";

const communityname = history.state.communityname;

axios.get(`/community/${communityname}`).then(res => community.value = res.data)

const community = ref()

function formatDate(dateString: string)
  {
    const date = new Date(dateString);
    date.setHours(date.getHours() - 9);
    
    return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('en-US', {
      hour12: true
    });
  }

</script>

<template>
	<html class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
	<el-text v-if="communityname" v-model="communityname" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{communityname}}</el-text>
	<el-text v-if="community" v-model="community" class="p-2" style="font-size: 1.2em">{{community.description}}</el-text>
		<el-text v-if="community" v-model="community" class="p-2">{{formatDate(community.createDate)}}</el-text>
	</div>
	<ListView :path="communityname"/>
	</html>
</template>

<style scoped>

</style>