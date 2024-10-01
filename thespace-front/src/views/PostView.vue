<script setup lang = "ts">
import {onMounted, ref} from "vue";
import axios from "axios";

const title = ref("")
const content = ref("")
const path = "aaa"
const categories = ref([])
const selectedCategory = ref("")

onMounted(() => axios.get(`/getcategory`, {params:{path}}).then(response => {categories.value = response.data; console.log(categories.value);}).catch(error => console.error(error)))

const post = function ()
{
  axios.post("/board/post", {title: title.value, content: content.value})
}
</script>

<template>
  <main>
  <div>
    <el-select id="select-category" v-if="categories.length > 0" v-model="selectedCategory" class="form-control">
      <el-option v-for="category in categories" :key="category.categoryId" :value="category.categoryName">{{category.categoryName}}</el-option>
    </el-select>
  </div>

  <div class="mt-2"><el-input v-model="title" placeholder="enter title"/></div>

  <div class="mt-2"><el-input type="textarea" v-model="content" placeholder="enter content"/></div>

  <div class="mt-2"><el-button type="primary" @click="post()">Submit</el-button></div>
  </main>
</template>

<style scoped>

</style>