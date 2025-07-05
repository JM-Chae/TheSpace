<script lang = "ts" setup>
import axios from "axios";
import {onMounted, ref} from 'vue'
import router from "@/router";
import {Search} from "@element-plus/icons-vue";

const dto = ref()
const type = ref('n')
const keyword = ref('')
let userinfo;
let userId: string;

if(sessionStorage.getItem('userInfo')) {
  userinfo = sessionStorage.getItem('userInfo') || ""
  userId = JSON.parse(userinfo).id
}

onMounted(() =>
{
  getList();
}
)

function getList()
  {
    axios.get("/community/list", {
      params: {
        page: 0,
        size: 0,
        type: type.value,
        keyword: keyword.value,
      }
    })
      .then(res => dto.value = res.data.dtoList)
  }

const goHomepage = (community: any) =>
  {
    router.push({path: '/community/home', state: {community: JSON.stringify(community)}})
	}

function create()
  {
    router.push('/community/create')
	}

function hasAdmin()
  {
  	axios.get(`/community/list/admin`, {withCredentials: true})
			.then(res =>
      {
        keyword.value = res.data.toString()
				type.value = "i"
        getList();
        keyword.value = ""
        type.value = "n"
      })

  }
</script>

<template>
<html class="dark">
<div class="ps-4 pb-4">
	<div style="display: grid; grid-template-columns: 2fr 1fr 1fr 2fr; justify-content: center">
			<div>
				<!--<div class = "p-3 pe-1 d-inline-block" style = "width: 15%;">-->
				<!--	<el-select v-model = "type" default-first-option>-->
				<!--		<el-option v-for = "list in typeList" :key = "list.value" :label = "list.name" :value = "list.value">-->
				<!--			{{ list.name }}-->
				<!--		</el-option>-->
				<!--	</el-select>-->
				<!--</div>-->
				<div class = "d-inline-block" style = "position: relative; width: 177px; height: 32px; top:-32px">
					<el-input v-model = "keyword" class = "radius" placeholder = "Enter keyword" style = "position: relative; top:32px; border-radius: 0" @keydown.enter = "getList()"/>
					<el-button style = "position: relative; left: 177px; border-radius: 0 4px 4px 0;" @click = "getList()">
						<el-icon size = "17">
							<Search/>
						</el-icon>
					</el-button>
				</div>
			</div>
		<el-button color="#00bd7e" style="justify-self: center; min-width: 12em" type="success" @click="create()">Create Community!</el-button>
		<el-button color="#ff7277" style="justify-self: center; color: white; min-width: 12em" type="success" @click="hasAdmin()">My Community</el-button>
		<div>
		</div>
	</div>
</div>

<div class="pb-2 pt-2" style="display: grid; border: 1px solid rgba(186,186,186,0.24); border-radius: 0.5em; background: rgba(255, 255, 255, 0.06);">
<ul style="top: 0; height: 100%; display: grid; grid-template-columns: repeat(4, 1fr); grid-template-rows: repeat(auto-fit, 1fr)">
	<li v-for="dtoList in dto" style="grid-row: auto; grid-column: auto">
		<a @click="goHomepage(dtoList)">{{dtoList.name}}</a>
	</li>
</ul>
</div>
</html>
</template>

<style scoped>
.radius {
    --el-border-radius-base: 4px 0px 0px 4px;
}
</style>