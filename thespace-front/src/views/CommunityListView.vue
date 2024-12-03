<script lang = "ts" setup>
import axios from "axios";
import {ref} from 'vue'
import router from "@/router";

const dto = ref()
const page = ref(1)
const type = ref('n')
const keyword = ref('')

axios.get("/community", {params: {
    page: page.value,
		type: type.value,
		keyword: keyword.value
  }
})
	.then(res => dto.value = res.data.dtoList)

const goHomepage = (communityname: string) =>
  {
    router.push({path: '/community/home', state: {communityname}})
	}

function create()
  {
    router.push('/community/create')
	}
</script>

<template>
<html class="dark">
<div class="pb-4" style="display: grid; grid-template-columns: 2fr 1fr 1fr 2fr; justify-content: center">
	<div></div>
	<el-button color="#00bd7e" style="justify-self: center; min-width: 12em" type="success" @click="create()">Create Community!</el-button>
	<el-button color="#ff7277" style="justify-self: center; color: white; min-width: 12em" type="success">My Community</el-button>
	<div></div>
</div>
<ul>
	<li v-for="dtoList in dto">
		<a @click="goHomepage(dtoList.communityName)">{{dtoList.communityName}}</a>
	</li>
</ul>

</html>
</template>

<style scoped>

</style>