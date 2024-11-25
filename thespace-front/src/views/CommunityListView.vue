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

</script>

<template>
<html class="dark">

<ul>
	<li v-for="dtoList in dto">
		<a @click="goHomepage(dtoList.communityName)">{{dtoList.communityName}}</a>
	</li>
</ul>

</html>
</template>

<style scoped>

</style>