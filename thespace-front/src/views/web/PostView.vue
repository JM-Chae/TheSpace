<script lang = "ts" setup>
import {ref, watch} from "vue";
import axios from "axios";
import router from "@/router";
import {Delete} from '@element-plus/icons-vue'
import Editor from '@tinymce/tinymce-vue'
import {ElMessageBox} from "element-plus";

const isSubmitted = ref<boolean>(false)

const save = () =>
  {
    setTimeout(() =>
    {

      isSubmitted.value = false;
      const submitButton = document.querySelector('button.tox-button[title="Save"]');
      if (submitButton)
        {
          submitButton.addEventListener('click', () =>
          {
            isSubmitted.value = true;
          });
        }
    }, 500)
  }

const closeDialog = async () =>
  {
    if (!isSubmitted.value)
      {
        if (count > 0)
          {
            try
              {
                const temp = fileNames.value.pop();
                if (temp)
                  {
                    const fileid = temp.split('_')[0];
                    const filename = temp.substring(temp.indexOf('_') + 1);
                    await axios.delete(`/file/${fileid}/${filename}`);
                  }
              } catch (error)
              {
                console.log('Error during file deletion on page unload:', error);
              }
          }
      }
  }

const fileInput = window.document;
const formData = new FormData();

const title = ref("")
const content = ref("")
const categoryId = ref("")

const community = JSON.parse(history.state.community)
const categories = ref()
const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)
const bno = ref<number>()
const fileNames = ref<any[]>([])

const deleteFile = function (fileid: string, filename: string, index: number)
{
  try
  {
    axios.delete(`/file/${fileid}/${filename}`)
    .then(() =>
    {
      fileNames.value[index] = null
    })
  } catch (error)
  {
    console.log(error)
  }
}

axios.get(`/category/list`, {params: {communityId: community.id}}).then(res => categories.value = res.data).catch(error => console.error(error))


const post = async () =>
  {
    const errorMessage = ref('')

    if (categoryId.value == "")
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
            window.removeEventListener('beforeunload', deleteEditorImage)
            window.removeEventListener('popstate', deleteEditorImage)
            await upload();
            axios.post("/board",
              {
                title: title.value,
                content: content.value,
                categoryId: categoryId.value,
                fileNames: fileNames.value
              }, {withCredentials: true})
            .then(res => {
              selectedFiles.value = []
              bno.value = res.data
            })
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
      router.push({path: '/read', state: {bno: bno.value}})
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
        await axios.post('/file', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
          .then(res => fileNames.value.push(...res.data.map((list: any) => list.fileId + '_' + list.fileName)))
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

const deleteEditorImage = () =>
  {
    try
      {
        for (let i = 0; i < count; i++)
          {
            const temp = fileNames.value.pop();
            if (temp)
              {
                const fileid = temp.split('_')[0];
                const filename = temp.substring(temp.indexOf('_') + 1);

                const url = `http://localhost:8080/file/${fileid}/${filename}`;

                count--;

                fetch(url, {
                  method: 'DELETE',
                  keepalive: true,
                })
              }
          }
      } catch (error)
      {
        console.error('Error during file deletion on page unload:', error);
      }
  }

//In the future, on the backend, when a file is initially uploaded, a separate column will be used to store a "temporary save flag."
//A logic will then be implemented to delete the corresponding data if the "temporary save flag" remains true after the session expiration time.
window.addEventListener('beforeunload', deleteEditorImage);

window.addEventListener('popstate', deleteEditorImage);

let count = 0;

</script>

<template>
	<html class = "dark">
	<main>
		<div>
			<h2 class = "communityName">{{  }}</h2>
		</div>
		<hr class = "mb-2 mt-2" style = "background: #25394a; height: 2px; border: 0">
		<div>
			<el-row :gutter = "10">
				<el-col :span = "12">
					<el-select id = "select-category" v-model = "categoryId" class = "form-control mt-3" placeholder = "Choose category">
						<el-option v-for = "category in categories" :key = "category.categoryId" :label="category.categoryName" :value = "category.categoryId">
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
		<div id = "MCE" class = "mt-3"></div>
		<Editor v-model = "content"
						:init = "{
        toolbar_mode: 'wrap',
        plugins: [
          'anchor', 'autolink', 'charmap', 'codesample', 'emoticons', 'image', 'link', 'lists', 'searchreplace', 'table', 'visualblocks', 'wordcount'
          ],
        toolbar: 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table mergetags | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
     skin: 'tinymce-5-dark',
     content_style: 'body { color: '+ '#fff' +'; background-color: ' + '#181818' + ' }',
     image_title: true,
     tinycomments_mode: 'embedded',
	   tinycomments_author: 'Author name',
     file_picker_types: 'image',
  //    setup: (editor: any) => {
  //         editor.on('OpenWindow', () =>
  //          {
  //          save()
  //          })
	// 				editor.on('CloseWindow', async () => {
  //       		await closeDialog()
  //   });
  // },
     file_picker_callback: (callback: any, value: any, meta: any) => {
    if (meta.filetype === 'image') {
          const input = fileInput.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');

    input.onchange = async () => {
      let files = input.files;
      if (files) {
        if(files[0].type.startsWith('image/')) {
        let selectedFile = files[0];
        formData.set('fileList', selectedFile);


        try {
          const response = await axios.post('/file', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })

          let fileid: string = response.data[0].fileId;
          let filename: string = response.data[0].fileName;

          let blob = `http://localhost:8080/file/${fileid}/${filename}`

          fileNames.push(fileid + '_' + filename)
          count += 1;

          if (blob) {
            callback (blob, { title: selectedFile.name });
          }
        } catch (error) {}
       } else {
         await ElMessageBox.alert('You must select image file.', 'Alert',
        {
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true
      })
       }
      }
    };
    input.click();}
  }
   }"
						api-key = "578wuj2fodmolbfsnxl67toi5ejoa0x1g38prodv7k93380c"
		/>
		<div class = "mt-3">
			<el-button style = "margin-right: 1em" type = "warning" @click = "buttonTrigger">File Select</el-button>
			<input id = "fileUpload" hidden multiple type = "file" @change = "handleFileChange">
		</div>
		<ul class = "p-0 mt-2">
			<li v-for = "(files, index) in selectedFiles" class = "m-2" style = "list-style-type: none;">
        <div style="color: #333333; line-height: 2; margin-right: 0.5em; padding: 0 1em 0 1em; vertical-align: center; height: 32px; display: inline-block; background: rgb(99,195,107); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
          {{files.name}}
        </div>
				<el-button type = "danger" @click = "unSelectFile(index)">
					<el-icon size = "15">
						<Delete/>
					</el-icon>
				</el-button>
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
			<el-button size = "large" type = "primary" @click = "post">Submit</el-button>
		</div>
	
	
	</main>
	</html>
</template>

<style scoped>

</style>