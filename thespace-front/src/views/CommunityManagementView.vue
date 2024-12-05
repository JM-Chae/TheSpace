<script lang = "ts" setup>
import {onMounted, ref} from 'vue'
import axios from "axios";

const communityname = history.state.communityname;

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
		
    getCommunity();
  }

</script>

<template>
	<html class = "dark" style="display: grid; justify-content: center">
	<div class="mb-3" style="display: grid; width: 924px; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<el-text v-if="communityname" v-model="communityname" class="p-2" size="large" style="color: #00bd7e; font-size: 1.5em">{{communityname}}</el-text>
		<div>
			<el-input v-if="des" v-model="des" :autosize = "{minRows: 3}" :readonly="desReadOnly" class="p-2" style="font-size: 1.2em; width: 80%" type="textarea">{{des}}</el-input>
			<el-button v-if="desReadOnly" class="mb-2" style="margin-left: 0.7em" type="warning" @click="desModify()">Modify</el-button>
			<div v-if="!desReadOnly" style="display: inline-block; margin-left: 0.7em">
				<el-button class="mb-2" color="#00bd7e" type="success" @click="">Done</el-button>
				<el-button class="mb-2" type="danger" @click="cancel()">Cancel</el-button>
			</div>
		</div>
	</div>
	</html>
</template>

<style scoped>

</style>