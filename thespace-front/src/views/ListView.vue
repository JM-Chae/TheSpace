<script setup lang = "ts">
import axios from "axios";
import {onMounted, ref} from "vue";
onMounted(()=>
{
  axios.get(`/board/list`, {params:{page: page.value, keyword: keyword.value, type: type.value}})
    .then(res => dtoList.value = res.data)
})
const dtoList = ref<dtoList>()

interface dtoList
  {
    page: number,
		size: number,
		total: number,
		start: number,
		end: number,
		prev: boolean,
		next: boolean,
		dtoList: [
      bno: number,
			title: string,
			content: string,
			path: string,
			writer: string,
			writerUuid: string,
			createDate: string,
			modDate: string,
			viewCount: number,
			vote: number,
			fileNames: [],
			categoryName: string,
			rcount: number
		]
	}

const page = ref(1)
const type = ref('')
const keyword = ref('')

</script>

<template>

</template>

<style scoped>

</style>