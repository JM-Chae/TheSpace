<script lang = "ts" setup>
import axios from "axios";
import {onMounted, ref} from "vue";
import 'element-plus/theme-chalk/dark/css-vars.css'

onMounted(() =>
{
  axios.get(`/board/list`, {
    params: {
      page: page.value,
      keyword: keyword.value,
      type: type.value,
      path: path.value,
      category: category.value
    }
  })
    .then(res => dtoList.value = res.data)
})
const dtoList = ref<dtoList>()

interface dto
  {
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
    rCount: number
  }

interface dtoList
  {
    page: number,
    size: number,
    total: number,
    start: number,
    end: number,
    prev: boolean,
    next: boolean,
    dtoList: dto[]
  }

const page = ref(1)
const type = ref('')
const keyword = ref('')
const path = ref("Test Community Name") // Community name -> Switch to reactive when after implementing the Community page.
const category = ref('')

function formatDate(dateString: string)
  {
    const date = new Date(dateString);
    date.setHours(date.getHours() - 9);
    const today = new Date();

    if (date.getFullYear() == today.getFullYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate())
      {
        return date.toLocaleTimeString('en-US', {
          hour12: false,
          hour: "2-digit",
          minute: "2-digit"
        });
      } else if (date.getFullYear() == today.getFullYear())
      {
        const res = date.getMonth() + '. ' + date.getDate();
        return res;
      }

    return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('en-US', {
      hour12: true
    });
  }
</script>

<template>
	<html class="dark">
	<el-table :data = "dtoList?.dtoList" class = "p-3 table" style = "justify-self: center; width: fit-content; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<el-table-column width="50">
				<template #header>
					<div class="text-center th">No</div>
				</template>
				<template #default="scope">
					<div class="td">{{scope.row.bno}}</div>
				</template>
		</el-table-column>
		<el-table-column width="670">
			<template #header>
				<div class="text-center th">Tile</div>
			</template>
			<template #default="scope">
				<div class="td" style="display: inline-block">{{scope.row.title}}</div><div style="color: rgba(255,255,255,0.29);display: inline-block">　[{{scope.row.rCount }}]</div>
			</template>
		</el-table-column>
		<el-table-column width="100">
			<template #header>
				<div class="text-center th">Writer</div>
			</template>
			<template #default="scope">
				<el-popover :hide-after="10" effect = "dark" placement = "left" popper-style = "text-align: center" title = "UUID" trigger = "hover">
					<template #default>
					<el-button class = "td" link style = "display: inline-block; color: white;">
							{{ scope.row.writerUuid }}
						</el-button>
					</template>
					<template #reference>
					<div class="text-center td">{{ scope.row.writer }}</div>
					</template>
				</el-popover>
			</template>
		</el-table-column>
		<el-table-column width="100">
			<template #header>
				<div class="text-center th">Date</div>
			</template>
		<template #default="scope">
			<div class="text-center td">{{formatDate(scope.row.createDate)}}</div>
		</template>
		</el-table-column>
		<el-table-column width="80">
			<template #header>
				<div class="text-center th">Viewed</div>
			</template>
			<template #default="scope">
				<div class="text-center td">{{scope.row.viewCount}}</div>
			</template>
		</el-table-column>
		<el-table-column width="60">
			<template #header>
				<div class="text-center th">Vote</div>
			</template>
			<template #default="scope">
				<div class="text-center td">{{scope.row.vote}}</div>
			</template>
		</el-table-column>
	</el-table>
	</html>
</template>

<style scoped>
.table
{
--el-bg-color: rgba(255, 255, 255, 0);
--el-table-row-hover-bg-color: rgba(255, 255, 255, 0.07);
}

.th
{
		font-weight: bold;
		color: rgba(65, 255, 158, 0.71);
}

.td
{
		color: rgba(248, 248, 248, 0.71);
}
</style>