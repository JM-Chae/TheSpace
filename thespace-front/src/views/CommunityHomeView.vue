<script lang = "ts" setup>
import {ref} from 'vue'
import axios from "axios";
import router from "@/router";

const community = ref()
const communityName = history.state.communityName;
const hasAdmin = ref(false);

if(sessionStorage.getItem('userInfo') == undefined) {
} else {
  const userinfo = sessionStorage.getItem('userInfo') || "";
  const userId = JSON.parse(userinfo).id;
  const roles = JSON.parse(userinfo).roles

  hasAdmin.value = roles.includes('ADMIN_' + communityName.toUpperCase())

  axios.get(`/community/${communityName}`).then(res => community.value = res.data)
}

const tempSize = history.state.size
const tempPage = history.state.page
const tempCategoryName = history.state.categoryName
const tempType = history.state.type
const tempKeyword = history.state.keyword

const page = ref(tempPage ? tempPage : 1);
const size = ref(tempSize ? tempSize : 10)
const categoryName = ref(tempCategoryName ? tempCategoryName : '')
const type = ref(tempType ? tempType : 't')
const keyword = ref(tempKeyword ? tempKeyword : '')

const getKeywordValue = (value: string) => {
  keyword.value = value;
};
const getTypeValue = (value: string) => {
  type.value = value;
};
const getCategoryNameValue = (value: string) => {
  categoryName.value = value;
};
const getPageValue = (value: number) => {
  page.value = value;
};

const getSizeValue = (value: number) => {
  size.value = value;
}

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
		router.push({path: '/community/management', state: {communityName: communityName, size: size.value, page: page.value, categoryName: categoryName.value, type: type.value, keyword: keyword.value}})
	}
</script>

<template>
	<html class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; grid-template-columns: 1fr 1fr;  width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<el-text v-if="communityName" v-model="communityName" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{communityName}}</el-text><div></div>
		<el-text v-if="community" v-model="community" class="p-2" style="font-size: 1.2em">{{community.description}}</el-text><div></div>
		<el-text v-if="community" v-model="community" class="p-2">{{formatDate(community.createDate)}}</el-text><div><el-button v-if="hasAdmin" class="mb-2 me-2" style="display: flex; margin-left: auto" type="warning" @click="management()">Management</el-button></div>
	</div>
	<ListView :categoryName="categoryName" :keyword="keyword" :page="page" :path="communityName" :size="size" :type="type" @sendCategoryName="getCategoryNameValue" @sendKeyword="getKeywordValue" @sendPage="getPageValue" @sendSize="getSizeValue" @sendType="getTypeValue"/>
	</html>
</template>

<style scoped>

</style>