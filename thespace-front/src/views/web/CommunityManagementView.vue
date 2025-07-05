<script lang = "ts" setup>
import {onMounted, ref} from 'vue'
import axios from "axios";
import router from "@/router";
import {ElMessageBox} from "element-plus";

const community = ref(JSON.parse(history.state.community));
const page = ref(history.state.page)
const size = ref(history.state.size)
const categoryId = ref(history.state.categoryId)
const type = ref(history.state.type)
const keyword = ref(history.state.keyword)
const userinfo = sessionStorage.getItem('userInfo') || ""
const roles = JSON.parse(userinfo).roles

const hasAdmin = ref(roles.includes('ADMIN_'+ community.value.name.toUpperCase()))
const categoryMaker = ref(false)

const getKeywordValue = (value: string) => {
  keyword.value = value;
};
const getTypeValue = (value: string) => {
  type.value = value;
};
const getCategoryIdValue = (value: number) => {
  categoryId.value = value;
};
const getPageValue = (value: number) => {
  page.value = value;
};
const getSizeValue = (value: number) => {
  size.value = value;
}

function getCommunity()
  {
    axios.get(`/community/${community.value.id}`).then(res => community.value = res.data)
      .then(() =>
      {
        hasAdmin.value = roles.includes('ADMIN_'+ community.value.name.toUpperCase())
      })
  }

onMounted(() =>
{
  if(!hasAdmin)
    {
      ElMessageBox.alert('You have not admin role with this community!', {
        confirmButtonText: 'OK',
        callback: () => { router.push({path: '/community/home', state: {community: JSON.stringify(community.value), size: size.value, page: page.value}})}
			})
    
    }
})

const desReadOnly = ref(true)
const des = ref(community.value.description)
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
    axios.patch(`/community/${community.value.id}/modify`,
			{
				description: des.value
      },
      {withCredentials: true})
			.then(() => getCommunity());
		
		desReadOnly.value = !desReadOnly.value;
  }

function returnHome()
  {
		router.push({path: '/community/home', state: {community: JSON.stringify(community.value), size: size.value, page: page.value, categoryName: categoryId.value, type: type.value, keyword: keyword.value}})
	}

const categoryType = ref()
const categoryName = ref()

function createCategory()
  {
    axios.post(`/category/admin`,
			{
        type: categoryType.value,
				communityId: community.value.id,
				name: categoryName.value
			},
			{withCredentials: true})
			.then(() =>
      {
        categoryMaker.value = false;
        categoryType.value = '';
        categoryName.value = '';
        categoryRe.value = true;
      })
	}

const categoryRe = ref(false)

const getCategoryRe = (value: boolean) => {
  categoryRe.value = value;
}
</script>

<template>
	<html v-show="hasAdmin" class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<el-text v-if="community" v-model="community.name" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{community.name}}</el-text>
		<div>
			<el-input v-if="community" v-model="des" :autosize = "{minRows: 3}" :readonly="desReadOnly" class="p-2" style="font-size: 1.2em; width: 80%" type="textarea">{{des}}</el-input>
			<el-button v-if="desReadOnly" class="mb-2" style="margin-left: 11%" type="warning" @click="desModify()">Modify</el-button>
			<div v-if="!desReadOnly" style="display: inline-block; margin-left: 0.7em">
				<el-button class="mb-2 ms-1" color="#00bd7e" style="width: 40%;" type="success" @click="done()">Done</el-button>
				<el-button class="mb-2 ms-4" style="width: 40%;" type="danger" @click="cancel()">Cancel</el-button>
			</div>
		</div>
	</div>
	<div>
		<el-button round style="z-index: 100; position: fixed; top: 8vh; right: 2vw; width: 10em" type="warning" @click="categoryMaker=true">Create Category</el-button>
		<el-dialog v-model="categoryMaker" :show-close="false" width="500">
			<template #header="{ close, titleId, titleClass }">
				<div class="my-header">
					<h4 :id="titleId" :class="titleClass">Create Category!</h4>
					<div class="m-3">
						<el-input v-model="categoryName" class="mb-2" placeholder="Enter Category Name">{{categoryName}}</el-input>
						<el-input v-model="categoryType" placeholder="Enter Category Type">{{categoryType}}</el-input>
					</div>
					<div class="mt-4" style="justify-self: end">
						<el-button type="danger" @click="close">Close</el-button>
						<el-button type="primary" @click="createCategory()">Submit</el-button>
					</div>
				</div>
			</template>
		</el-dialog>
	</div>
		<div>
			<el-button color="#00bd7e" round style="z-index: 100; position: fixed; top: 3vh; right: 2vw; width: 10em" @click="returnHome()">Return Home</el-button>
	</div>
	
	<ListViewAdmin v-if="community" :categoryId="categoryId" :categoryRe="categoryRe" :community="community" :keyword="keyword" :page="page" :size="size" :type="type" @sendCategoryId="getCategoryIdValue" @sendCategoryRe="getCategoryRe" @sendKeyword="getKeywordValue" @sendPage="getPageValue" @sendSize="getSizeValue" @sendType="getTypeValue"/>
	</html>
</template>

<style scoped>

</style>