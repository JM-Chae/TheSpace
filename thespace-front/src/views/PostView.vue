<script lang = "ts" setup>
import {onMounted, ref, watch} from "vue";
import axios from "axios";
import router from "@/router";
import {Delete} from '@element-plus/icons-vue'
import Editor from '@tinymce/tinymce-vue'

const fileInput = window.document;
const formData = new FormData();
const fileUrl = URL;

const loading = ref(true);

onMounted(() =>
{
  setTimeout(() =>
  {
    loading.value = false;
  }, 500);
});

const title = ref("")
const content = ref("")
const categoryName = ref("")

const path = "Test Community Name" // Community name -> Switch to reactive when after implementing the Community page.
const categories = ref()
const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)
const bno = ref<number>()
const fileNames = ref([])

axios.get(`/getcategory/${path}`).then(res => categories.value = res.data).catch(error => console.error(error))

const post = async () =>
  {
    const errorMessage = ref('')

    if (categoryName.value == "")
      {
        errorMessage.value += 'Choose category \n'
      }
    if (title.value == "")
      {
        errorMessage.value += 'Enter title \n'
      }
    if (content.value == "")
      {
        errorMessage.value += 'Enter content \n'
      }

    if (errorMessage.value == '')
      {
        try
          {
            await upload();
            const res = await axios.post("/board/post",
              {
                title: title.value,
                content: content.value,
                writer: user.name,
                writerUuid: user.uuid,
                categoryName: categoryName.value,
                fileNames: fileNames.value
              })
            selectedFiles.value = [];
            bno.value = res.data
          } catch (error)
          {

          }
      } else
      {
        alert(errorMessage.value);
      }

  }

watch(bno, (newValue) =>
{
  if (newValue)
    {
      router.push({name: "read", state: {bno: bno.value}})
    }
})

const selectedFiles = ref<File[]>([]);

const handleFileChange = (event: Event) =>
  {
    const target = event.target as HTMLInputElement
    if (target.files)
      {
        const fileList = Array.from(target.files)
        selectedFiles.value = [...selectedFiles.value, ...fileList]
      }
  };

function unSelectFile(index: number)
  {
    selectedFiles.value.splice(index, 1)
  }

const upload = async () =>
  {
    const formData = new FormData();
    selectedFiles.value.forEach((file, index) =>
    {
      formData.append('fileList', file)
    })

    try
      {
        await axios.post('/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
          .then(res => fileNames.value = res.data.map((list: any) => list.fileId + '_' + list.fileName))
      } catch (error)
      {
      }
  }

const buttonTrigger = () =>
  {
    const fileInput = document.getElementById('fileUpload') as HTMLInputElement;
    if (fileInput)
      {
        fileInput.click();
      }
  };
</script>

<template>
	<html class = "dark">
	<main>
		<div>
			<h2 class = "communityName">{{ path }}</h2>
		</div>
		<hr class = "mb-2 mt-2" style = "background: #25394a; height: 2px; border: 0">
		<div>
			<el-row :gutter = "10">
				<el-col :span = "12">
					<el-select id = "select-category" v-model = "categoryName" class = "form-control mt-3" placeholder = "Choose category">
						<el-option v-for = "category in categories" :key = "category.categoryId" :value = "category.categoryName">
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
			<Editor v-if = "!loading"
							v-model = "content"
							:init = "{
        toolbar_mode: 'wrap',
        plugins: [
          'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'image', 'link', 'lists', 'media', 'searchreplace', 'table', 'visualblocks', 'wordcount',
          'checklist', 'mediaembed', 'casechange', 'export', 'formatpainter', 'pageembed', 'a11ychecker', 'tinymcespellchecker', 'permanentpen', 'powerpaste', 'advtable', 'advcode', 'editimage', 'mentions', 'tableofcontents', 'footnotes', 'autocorrect', 'typography', 'inlinecss'
        ],
        toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
     selector: 'textarea',
     skin: 'oxide-dark',
     content_css: 'dark',
     image_title: true,
     automatic_uploads: true,
     file_picker_types: 'image',
     file_picker_callback: (callback: any, value: any, meta: any) => {
    if (meta.filetype === 'image') {
          const input = fileInput.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');

    // change 이벤트 리스너 설정
    input.onchange = async () => {
      const files = input.files;
      if (files) {
        const selectedFile = files[0]; // 첫 번째 파일을 예시로 사용
        formData.append('fileList', selectedFile);

        try {
          const response = await axios.post('/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            }
          })
          
          const fileid = response.data[0].fileId;
          const filename = response.data[0].fileName;
          
          const blob = await axios.get(`/get/${fileid}/${filename}`, {responseType: 'blob'})
          const objectUrl = fileUrl.createObjectURL(blob.data);

          if (objectUrl) {
            console.log(objectUrl)
            callback (objectUrl, { title: selectedFile.name });
          }
        } catch (error) {}
      }
    };}

    // 파일 선택 창 열기
    input.click();
  }

   }"
							api-key = "578wuj2fodmolbfsnxl67toi5ejoa0x1g38prodv7k93380c"
			/>
		</div>
		<div class = "mt-3">
			<el-button style = "margin-right: 1em" type = "warning" @click = "buttonTrigger">File Select</el-button>
			<input id = "fileUpload" hidden multiple type = "file" @change = "handleFileChange">
		</div>
		<ul class = "p-0 mt-2">
			<li v-for = "(files, index) in selectedFiles" class = "m-2" style = "list-style-type: none;">
				<div style = "color: #333333; line-height: 2; margin-right: 0.5em; padding: 0 1em 0 1em; vertical-align: center; height: 32px; display: inline-block; background: #c1c1c1; border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
					{{ files.name }}
				</div>
				<el-button type = "danger" @click = "unSelectFile(index)">
					<el-icon size = "15">
						<Delete/>
					</el-icon>
				</el-button>
			</li>
		</ul>
		<div class = "mt-3" style = "text-align: end">
			<el-button size = "large" type = "primary" @click = "post">Submit</el-button>
		</div>
	
	
	</main>
	</html>
</template>

<style scoped>

</style>