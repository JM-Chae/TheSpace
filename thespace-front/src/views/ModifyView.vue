<script lang = "ts" setup>
import {onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router";
import {Delete} from "@element-plus/icons-vue";
import Editor from '@tinymce/tinymce-vue'

const loading = ref(true);

onMounted(() => {
  setTimeout(() => {
    loading.value = false;
  }, 500);
});

const categories = ref()
const title = ref<string>(window.history.state.title)
const content = ref<string>(window.history.state.content)
const categoryId = ref(window.history.state.categoryId)
const bno = ref(window.history.state.bno)
const fileNames = ref<string[] | null[]>(JSON.parse(window.history.state.fileNames))
const newFileNames = ref<string[]>()

const deleteFile = function (fileid: string, filename: string, index: number)
  {
    try
      {
        axios.delete(`/delete/${fileid}/${filename}`)
          .then(() =>
          {
            fileNames.value[index] = null
          })
      } catch (error)
      {
        console.log(error)
      }
  }

const path = "Test Community Name" // Community name -> Switch to reactive when after implementing the Community page.
axios.get(`/category/list/${path}`).then(res => categories.value = res.data).catch(error => console.error(error))

const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)

const post = async function ()
  {
    
    await upload();
    selectedFiles.value = [];
    await axios.patch("/board",
      {
        bno: bno.value,
        title: title.value,
        content: content.value,
        writer: user.name,
        writerUuid: user.uuid,
        categoryId: categoryId.value,
        fileNames: newFileNames.value
      })
      .then(() => router.push({name: "read", state: {bno: bno.value}}))
  }

const selectedFiles = ref<File[]>([]);

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if(target.files)
    {
      const fileList = Array.from(target.files)
      selectedFiles.value = [...selectedFiles.value, ...fileList]
    }};

function unSelectFile(index: number)
  {
    selectedFiles.value.splice(index, 1)
  }

const upload = async () => {
  const formData = new FormData();
  selectedFiles.value.forEach((file, index) => {
    formData.append('fileList', file)
  })

  try {
    await axios.post('/upload',  formData, {headers: {
        'Content-Type': 'multipart/form-data'
      }})
      .then(res => newFileNames.value = res.data.map((list: any) => list.fileId + '_' +list.fileName))
  }catch (error)
    {
    }
}


const buttonTrigger = () => {
  const fileInput = document.getElementById('fileUpload') as HTMLInputElement;
  if (fileInput) {
    fileInput.click();
  }
};
</script>

<template>
	<html class="dark">
	<main>
		<div>
			<h2 class = "communityName">{{ path }}</h2>
		</div>
		<hr class = "mb-2 mt-2" style = "background: #25394a; height: 2px; border: 0">
		<div>
			<el-row :gutter = "10">
				<el-col :span = "12">
					<el-select id = "select-category" v-model = "categoryId" class = "form-control mt-3" placeholder = "Choose category">
						<el-option v-for = "category in categories" :key = "category.categoryId" :label = "category.categoryName" :value = "category.categoryId">
							{{ category.categoryName }}
						</el-option>
					</el-select>
				</el-col>
				<el-col :span = "3">
					<div class = "mt-3" style = "text-align-last: center">
						<el-input :value = "user.uuid" readonly>{{ user.uuid }}</el-input>
					</div>
				</el-col>
				<el-col :span = "9">
					<div class = "mt-3">
						<el-input :value = "user.name" readonly>{{ user.name }}</el-input>
					</div>
				</el-col>
			</el-row>
		</div>
		
		<div class = "mt-3">
			<el-input v-model = "title" placeholder = "Enter title" size = "large"/>
		</div>
		<div class = "mt-3">
			<Editor v-if="!loading"
					v-model="content"
					:init="{
        toolbar_mode: 'wrap',
        plugins: [
          'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'image', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
          'checklist', 'mediaembed', 'casechange', 'export', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'editimage', 'mentions', 'tableofcontents', 'footnotes', 'autocorrect', 'typography', 'inlinecss'
        ],
        toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
     selector: 'textarea', skin: 'oxide-dark', content_css: 'dark'}"
					api-key="578wuj2fodmolbfsnxl67toi5ejoa0x1g38prodv7k93380c"
			/>
		</div>
		<div class="mt-3">
			<el-button style="margin-right: 1em" type="warning" @click="buttonTrigger">File Select</el-button>
			<input id="fileUpload" hidden multiple type="file" @change="handleFileChange">
		</div>
		<ul class = "p-0 mt-2">
			<li v-for="(files, index) in selectedFiles" class="m-2" style="list-style-type: none;">
				<div style="color: #333333; line-height: 2; margin-right: 0.5em; padding: 0 1em 0 1em; vertical-align: center; height: 32px; display: inline-block; background: rgb(99,195,107); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
					{{files.name}}
				</div>
				<el-button type="danger" @click="unSelectFile(index)"><el-icon size="15"><Delete/></el-icon></el-button>
			</li>
			<li v-for = "(file, index) in fileNames" class = "mt-3" style = "list-style-type: none;">
				<div v-if = "file && file[index]">
					<div style = "color: #333333; line-height: 2; margin-right: 0.5em; padding: 0 1em 0 1em; vertical-align: center; height: 32px; display: inline-block; background: #c1c1c1; border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
						{{ file?.split('_').slice(1).join('_') }}
					</div>  <!-- If file is image, pop-up thumbnail -->
					<el-button type = "danger" @click = "deleteFile(`${file?.split('_')[0]}`, `${file?.split('_').slice(1).join('_')}`, index)">
						<el-icon size = "15">
							<Delete/>
						</el-icon>
					</el-button>
				</div>
			</li>
		</ul>
		<div class = "mt-3" style = "text-align: end">
			<el-button size = "large" type = "primary" @click = "post()">Submit</el-button>
		</div>
	
	</main>
	</html>
</template>

<style scoped>

</style>