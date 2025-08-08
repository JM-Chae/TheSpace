<script lang = "ts" setup>
import axios from "axios";
import {computed, onMounted, ref, watch} from "vue";
import 'element-plus/theme-chalk/dark/css-vars.css'
import router from "@/router";
import {Link, Picture, Search} from '@element-plus/icons-vue'
import type {CategoryInfo, CommunityInfo} from "@/types/domain";

const getWidth = ref(window.innerWidth);

function updateWidth() {
  getWidth.value = window.innerWidth;
}

window.addEventListener('resize', updateWidth);

const props = withDefaults(defineProps<{
  community?: CommunityInfo,
  size: number,
  page: number,
  type: string,
  keyword: string,
  category?: CategoryInfo,
  show?: boolean,
  tableMin?: string
}>(), {
  show: true,
  tableMin: '472px'
});

if(props.show) {
  axios.get(`/category/list`, {params: {communityId: props.community?.id}}).then(res => categories.value = res.data).catch(error => console.error(error))
}

const paginationLayout = computed(() => {
  return props.show
      ? "sizes, jumper, prev, pager, next"
      : "prev, pager, next";
});

const categories = ref()

function getList()
  {
    axios.get(`/board/list`, {
      params: {
        page: page.value,
        keyword: keyword.value,
        type: type.value,
				size: size.value,
        communityId: props.community?.id ? props.community?.id : 0,
        categoryId: props.category?.id ? props.category?.id : 0
      }
    })
      .then(res =>
      {
        dtoList.value = res.data
        if (dtoList && dtoList.value != undefined)
          {
            pageCount.value = parseInt(String((dtoList.value.total / dtoList.value.size))) + (((dtoList.value.total % dtoList.value.size) > 0) ? 1 : 0)
          }
      })
  }

onMounted(() =>
{
  getList()

  watch(type, (newValue) =>
  {
    if (newValue)
      {
        history.replaceState({communityId: props.community?.id, page: page.value, type: type.value, keyword: keyword.value, categoryId: categoryId.value}, '')
        sendTypeToParent()
        getList()
      }
  })
  watch(keyword, (newValue) =>
  {
    if (newValue)
      {
        history.replaceState({communityId: props.community?.id, page: page.value, type: type.value, keyword: keyword.value, categoryId: categoryId.value}, '')
        sendKeywordToParent()
        getList()
      }
  })
  watch(categoryId, (newValue) =>
  {
    if (newValue)
      {
        history.replaceState({communityId: props.community?.id, page: page.value, type: type.value, keyword: keyword.value, categoryId: categoryId.value}, '')
        sendCategoryIdToParent()
        getList()
      }
  })
  watch(page, (newValue) =>
  {
    if (newValue)
      {
        history.replaceState({communityId: props.community?.id, page: page.value, type: type.value, keyword: keyword.value, categoryId: categoryId.value}, '')
        sendPageToParent()
        getList()
      }
  })
  watch(size, (newValue) =>
  {
    if (newValue)
      {
        history.replaceState({communityName: props.community?.id, page: page.value, type: type.value, keyword: keyword.value, categoryId: categoryId.value}, '')
        sendSizeToParent()
        getList()
      }
  })

	watch(categoryId, () => {
        getList()
})
})

const dtoList = ref<dtoList>()

const moveRead = (row: any) =>
  {
    router.push({name: "read", state: {bno: row.bno, size: size.value, page: page.value}})
  }

interface dto
  {
    bno: number,
    title: string,
    content: string,
    communityInfo: CommunityInfo,
    writer: string,
    writerUuid: string,
    createDate: string,
    modDate: string,
    viewCount: number,
    vote: number,
    fileNames: string[],
    thumbCheck: boolean,
    categoryInfo: CategoryInfo,
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

const setPage = (val1: number, val2: number) =>
  {
    page.value = val1;
    size.value = val2;
  }

const size = ref(props.size ? props.size : 10)
const page = ref(props.page ? props.page : 1)
const type = ref(props.type ? props. type : 't')
const keyword = ref(props.keyword ? props.keyword : '')
const categoryId = ref(props.category?.id ? props.category?.id : 0)
const pageCount = ref<number>(0)

const emit = defineEmits<{
  (event: 'sendPage', value: number): void;
  (event: 'sendSize', value: number): void;
  (event: 'sendType', value: string): void;
  (event: 'sendKeyword', value: string): void;
  (event: 'sendCategoryId', value: number): void;
}>();

const sendCategoryIdToParent = () =>
  {
    emit('sendCategoryId', categoryId.value)
  }
const sendKeywordToParent = () =>
  {
    emit('sendKeyword', keyword.value)
  }
const sendTypeToParent = () =>
  {
    emit('sendType', type.value)
  }
const sendPageToParent = () =>
  {
    emit('sendPage', page.value)
  }
const sendSizeToParent = () =>
  {
    emit('sendSize', size.value)
  }

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
        return (date.getMonth() + 1) + '. ' + date.getDate();
      }

    return date.getFullYear() + '. ' + (date.getMonth() + 1);
  }

const thumbnails = ref();

const imageExt = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'tiff', 'svg', 'ico', 'heic', 'heif'];

const mouseLeave = function (rowData: dto)
  {
    thumbnails.value = '';
    rowData.thumbCheck = false;
  }

const extCheck = function (rowData: dto)
  {
    return rowData?.fileNames.filter(file =>
    {
      const filesExt = file.split('.').pop()?.toLowerCase();
      return imageExt.includes(filesExt ? filesExt : '');
    }).toString() != ''
  }

const mouseOver = async function (rowData: dto)
  {
    rowData.thumbCheck = false;

    const imgArr = rowData?.fileNames.filter(file =>
    {
      const filesExt = file.split('.').pop()?.toLowerCase();
      return imageExt.includes(filesExt ? filesExt : '');
    })

    if (imgArr.length > 0)
      {
        rowData.thumbCheck = true;

        const response = ref()

        for (let i = 0; i < imgArr.length; i++)
          {
            let fileid = imgArr[i].split("_")[0]
            let filename = imgArr[i].substring(imgArr[i].indexOf('_') + 1);
            response.value = URL.createObjectURL((await axios.get(`/file/${fileid}/s_${filename}`, {responseType: 'blob'}))?.data)
          }

        thumbnails.value = response.value
      }
  }

type ListItem = {
  name: string;
  value: string;
};

const typeList: ListItem[] = [{name: 'title', value: 't'}, {name: 'content', value: 'c'}, {
  name: 'writer name',
  value: 'w'
}, {name: 'writer UUID', value: 'u'}, {name: 'reply', value: 'r'}, {name: 'title + content', value: 'tc'}]

const post = () =>
  {
    router.push({path: '/post', state: {community: JSON.stringify(props.community)}})
	}
</script>

<template>
	<html class = "dark">
	
	<div style = "width: 100%; justify-self: center; background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
		<div v-show="props.show" :class="['p-3', 'pe-1', { 'd-inline-block': props.show }]" style = "width: 15%;">
			<el-select v-model = "type" default-first-option>
				<el-option v-for = "list in typeList" :key = "list.value" :label = "list.name" :value = "list.value">
					{{ list.name }}
				</el-option>
			</el-select>
		</div>
		<div v-show="props.show" :class="{ 'd-inline-block': props.show }" style = "width: 19.2%; height: 32px; margin-right: 4em">
			<el-input v-model = "keyword" class = "radius" placeholder = "Enter keyword" style = "position: relative; top:32px; border-radius: 0" @keydown.enter="getList()"/>
			<el-button style = "position: relative; left: 177px; border-radius: 0 4px 4px 0;" @click = "getList()">
				<el-icon size = "17">
					<Search/>
				</el-icon>
			</el-button>
		</div>
		<div v-show="props.show" :class="{ 'd-inline-block': props.show }" style="width: 20%">
			<el-select v-model="categoryId" class = "form-control" placeholder = "Choose category">
				<el-option :key ="0" :value = "0" label = "All">All</el-option>
				<el-option v-for = "category in categories" :key = "category.categoryId" :label="category.categoryName" :value="category.categoryId">
					{{ category.categoryName }}
				</el-option>
			</el-select>
		</div>
		<div v-show="props.show" class="pe-3" style="display: inline-grid; margin-left: 30%;">
			<el-button color="rgba(65, 255, 158, 0.71)" style="color: rgba(255,255,255,0.82); justify-self: end; width: 100%" type="success" @click="post">Post</el-button>
		</div>
		<el-table v-if = "dtoList?.total!=0" v-loading="!dtoList" :cell-style="{padding: '8px 10px'}" :data = "dtoList?.dtoList" :header-cell-style="{padding: '20px 10px'}" :style="{minHeight: props.tableMin}" class = "table" empty-text="" @row-click = "moveRead">
			<el-table-column v-if="props.show" width = "75">
				<template #header>
					<div class = "text-center th">No</div>
				</template>
				<template #default = "scope">
					<div class = "td" style="text-align: center">{{ scope.row.bno }}</div>
				</template>
			</el-table-column>
			<el-table-column>
				<template #header>
					<div class = "th">Title</div>
				</template>
				<template #default = "scope">
					<el-popover :hide-after = "10" :visible = "scope.row.thumbCheck" :width = "`fit-content`" effect = "dark" placement = "bottom" popper-style = "text-align: center" title = "" trigger = "hover">
						<template #default>
							<img :src = "thumbnails" alt = "thumbnail" style="width: 200px; height: fit-content;">
						</template>
						<template #reference>
							<div style = "width: 100%" @mouseleave = "mouseLeave(scope.row)" @mouseover = "mouseOver(scope.row)">
								<div class = "td" style = "display: inline-block">{{ scope.row.title }}</div>
								<div style = "color: rgba(255,255,255,0.29);display: inline-block">&nbsp[{{ scope.row.rCount }}]</div>
								<div v-if = "scope.row.fileNames != '' && !extCheck(scope.row)" style = "display: inline-block; position: relative; top: 3px">&nbsp
									<el-icon>
										<Link/>
									</el-icon>
								</div>
								<div v-if = "extCheck(scope.row)" style = "display: inline-block; position: relative; top: 3px">&nbsp
									<el-icon>
										<Picture/>
									</el-icon>
								</div>
							</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>
			<el-table-column v-if="props.show" width = "100">
				<template #header>
					<div class = "text-center th">Writer</div>
				</template>
				<template #default = "scope">
					<el-popover :hide-after = "10" placement = "left" popper-style = "text-align: center" title = "UUID" trigger = "hover">
						<template #default>
							<el-button class = "td" link style = "display: inline-block; color: white;">
								{{ scope.row.writerUuid }}
							</el-button>
						</template>
						<template #reference>
							<div class = "text-center td">{{ scope.row.writer }}</div>
						</template>
					</el-popover>
				</template>
			</el-table-column>
			<el-table-column width = "100%">
				<template #header>
					<div class = "text-center th">Date</div>
				</template>
				<template #default = "scope">
					<div class = "text-center td">{{ formatDate(scope.row.createDate) }}</div>
				</template>
			</el-table-column>
			<el-table-column width = "100%">
				<template #header>
					<div class = "text-center th">Views</div>
				</template>
				<template #default = "scope">
					<div class = "text-center td">{{ scope.row.viewCount }}</div>
				</template>
			</el-table-column>
			<el-table-column width = "100%">
				<template #header>
					<div class = "text-center th">Likes</div>
				</template>
				<template #default = "scope">
					<div class = "text-center td">{{ scope.row.vote }}</div>
				</template>
			</el-table-column>
		</el-table>
		<div v-if = "dtoList" class = "p-3 paging" style = "justify-self: end">
    <el-pagination v-model:current-page = "page" v-model:page-size = "size" :change = "setPage" :layout = "paginationLayout" :page-count = "pageCount" :page-sizes = "[10, 15, 20, 30, 50, 100]" :pager-count = "7" :size = "'large'" :total = "dtoList?.total" background class = "paging"/>
    </div>
  </div>
	</html>
</template>

<style scoped>
.table {
    --el-bg-color: rgba(255, 255, 255, 0);
    --el-table-row-hover-bg-color: rgba(255, 255, 255, 0.07);
}

.th {
    font-weight: bold;
    color: rgba(65, 255, 158, 0.71);
}

.td {
    color: rgba(248, 248, 248, 0.71);
}

.paging {
    --el-pagination-button-color: rgba(65, 255, 158, 0.71);
    --el-color-primary: rgba(181, 181, 181, 0.32);
    --el-pagination-hover-color: rgba(255, 255, 255, 0.82);
    --el-disabled-bg-color: rgba(97, 255, 176, 0.45);
    --el-color-white: rgba(65, 255, 158, 0.71);
    --el-text-color-placeholder: white;
}

.radius {
    --el-border-radius-base: 4px 0px 0px 4px;
}
</style>