<script lang = "ts" setup>
import {onMounted, ref, watch, onBeforeUnmount} from "vue";
import axios from "axios";

function getRead()
  {
    axios.get(`http://localhost:8080/board/read/${bno}`)
      .then(res => getDto.value = res.data)
      .catch(error => console.error(error))
  }

function getReply()
  {
    axios.get(`http://localhost:8080/board/${bno}/reply`)
      .then(res => rDtoList.value = res.data)
			.then(res =>
        {
              rno.value = res.dtoList.map((rnoV: rdto) => rnoV.rno)
					
        }
			)
			.then(()=> getNestedReplyFunction())
      .catch(error => console.error(error));
  }

function getNestedReplyFunction()
  {
    for (let i=0; i<rno.value.length; i++)
      {
        getNestedReply(rno.value[i], i)
      }
  }

function getNestedReply(rno: number, i: number)
  {
    axios.get(`http://localhost:8080/board/${bno}/reply/${rno}`)
      .then(res => nrDtoList.value[i] = res.data)
			.then(()=>
      {
        if (nrDtoList.value[i].total != 0 && nrDtoList.value[i].total != undefined)
          {
            isNested.value[i] = true
          }
      })
      .catch(error => console.error(error));
  }



interface dto
  {
    bno: number,
    categoryName: string,
    content: string,
    createDate: string,
    fileNames: [],
    modDate: string,
    path: string,
    title: string,
    viewCount: number,
    vote: number,
    writer: string,
    writerUuid: string,
    rCount: number
  }

interface rdto
  {
    rno: number,
    bno: number,
    replyContent: string,
    replyWriter: string,
    replyWriterUuid: string,
    replyDate: string,
    path: string,
    vote: number
  }

interface rDtos
  {
    total: number,
    dtoList: Array<rdto>
  }

const rDtoList = ref<rDtos | null>(null)
const nrDtoList = ref<rDtos[]>([{dtoList: [], total: 0}])

const getInfo = sessionStorage.getItem("userInfo") || ""
const user = JSON.parse(getInfo)
const getDto = ref<dto | null>(null)

const path = "Test Community Name" // Community name -> Switch to reactive when after implementing the Community page.
const bno = window.history.state.bno;
const rno = ref<number[]>([])

const replyContent = ref<string>()
const reply = function ()
  {
    axios.post(`http://localhost:8080/board/${bno}/reply`,
      {
        bno: bno,
        replyContent: replyContent.value,
        replyWriter: user.name,
        replyWriterUuid: user.uuid,
        path: bno + "/" + nestedReply.value
      })
      .then(() => replyContent.value = "")
      .then(() =>
      {
        getReply();
				if (getDto.value && getDto.value.rCount != undefined)
          {
            getDto.value.rCount += 1;
          }
      })
  }
// let likeCheck = axios.get
const like = function ()
  {
    const rno = 0 // If click like button then return rno value
    axios.put(`http://localhost:8080/like`,
      {
        userId: <string>user.id,
        rno: rno,
        bno: bno
      })
    // .then(() =>
    // {
    //
    //   if (getDto.value && getDto.value.vote != undefined && likeCheck == 0)
    //     {
    //       getDto.value.vote += 1;
    //       likeCheck = 1;
    //     }
    //   if (getDto.value && getDto.value.vote != undefined && likeCheck == 1)
    //     {
    //       getDto.value.vote += 1;
    //       likeCheck = 0;
    //     }
    //)
  }


const writerCheck = ref(false)

watch(getDto, (newValue) =>
{
  if (newValue)
    {
      writerCheck.value = user.uuid === newValue.writerUuid
    }
})

const formatDate = (dateString: string) =>
  {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {timeZone: "UTC"}) + ' ' + date.toLocaleTimeString( 'en-US',{hour12: true, timeZone: "UTC"});
  }

const isVisible = ref<boolean[]>([]);
const focused = ref<boolean>(true)
const nestedReply = ref<number|string>('')
const isNested = ref<boolean[]>([])

const closeAllNestedReplies = () =>
  {
    isVisible.value = isVisible.value.map(() => false);
    focused.value = true;
    replyContent.value = '';
    nestedReply.value = ''
  };

const handleOutsideClick = (event: MouseEvent) =>
  {
    const clickedElement = event.target as HTMLElement;
    if (clickedElement.closest("li"))
      {
        return;
      }
    closeAllNestedReplies();
  }

if (rDtoList.value != null)
  {
    isVisible.value = new Array(rDtoList.value.dtoList.length).fill(false);
    isNested.value = new Array(rDtoList.value.dtoList.length).fill(false);
  }
const toggleNested = (index: number) =>
  {
    isVisible.value[index] = !isVisible.value[index];
    isVisible.value = isVisible.value.map((_, i) => i === index);
    replyContent.value = '';
    focused.value = false;
  }

const listEnd = ref<boolean[]>([])
watch(rDtoList, (newValue) =>
{
  if (newValue != null)
    {
      listEnd.value = new Array(newValue.dtoList?.length).fill(true);
    }
  if(newValue?.dtoList != undefined)
    {
  		newValue?.dtoList.forEach((_, index) =>
				{
					const listEndCheck = () =>
						{
							listEnd.value[index] = index != newValue?.dtoList?.length - 1;
						}
					
					listEndCheck();
				})
    }
})

onMounted(() =>
{
  getRead()
  getReply()
  window.addEventListener("click", handleOutsideClick);
})
onBeforeUnmount(() =>
{
  window.removeEventListener("click", handleOutsideClick);
});

</script>

<template>
	<html>
	<main>
		<div class = "board">
			<div>
				<h2 class = "communityName">{{ path }}</h2>
			</div>
			<hr class = "mb-2 mt-2" style = "background: #25394a; height: 2px; border: 0">
			<el-row>
				<el-col :span = "16">
					<div class = "mt-3">
						<h3 v-if = "getDto">{{ getDto.title }}</h3>
					</div>
				</el-col>
				<el-col :span = "8">
					<div class = "mt-4" style = "text-align: end">
						<el-text v-if = "getDto" class = "name">{{ formatDate(getDto.createDate) }}</el-text>
					</div>
				</el-col>
				<el-col :span = "16">
					<div>
						<el-text v-if = "getDto" class = "name">{{ getDto.writerUuid }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text v-if = "getDto" class = "name">{{ getDto.writer }}</el-text>
					</div>
				</el-col>
				<el-col :span = "8">
					<div style = "text-align: end">
						<el-text v-if = "getDto" class = "name">Like {{ getDto.vote }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text v-if = "getDto" class = "name">View {{ getDto.viewCount }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text v-if = "getDto" class = "name">Reply {{ getDto.rCount }}</el-text>
					</div>
				</el-col>
			</el-row>
			
			<hr class = "mb-2 mt-2" style = "background: rgba(70,130,180,0.17); height: 0.01em; border: 0;">
			<div class = "mt-3" style = "min-height: 15em">
				<el-text v-if = "getDto" class = "text">{{ getDto.content }}</el-text>
			</div>
			<div v-if = "writerCheck" class = "mt-3 mb-2" style = "text-align: end">
				<el-button class = "button" color = "#ff25cf" round size = "small" style = "margin-left: 0.5em" @click = "like">
					Like!
				</el-button>
				<el-button round size = "small" style = "margin-left: 0.5em" type = "primary" @click = "">Close Reply</el-button>
				<el-button round size = "small" style = "margin-left: 0.5em" type = "warning" @click = "">Modify</el-button>
			</div>
		</div>
		
		<hr style = "background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
		
		<div class = "replyList">
			<div class = "p-3 m-3 pb-2 pt-4"
					 style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
				
				<el-space :size = "20" fill = "fill" style = "display: flex;">
					<ul v-for = "(rDto, index) in rDtoList?.dtoList" key = "rDto.rno" class = "list" style = "padding-left: 0">
						<li style = "list-style-type: none;" @click = "toggleNested(index); nestedReply = rDto.rno" class="list">
							<div style = "display: grid;">
								<div style = "display: flex; justify-content: space-between; align-items: center; ">
									<div style = "flex-grow: 1; min-width: 90px">
										<el-popover :width = "10" effect = "dark" placement = "top" popper-style = "text-align: center" title = "UUID" trigger = "hover">
											<template #reference>
												<el-button class = "name" style = "color: white" link>{{ rDto.replyWriter }}</el-button>
											</template>
											{{ rDto.replyWriterUuid }}
										</el-popover>
									</div>
									<div style = "text-align: left; margin-left: auto; flex-grow: 20">
										<el-text class = "text">{{ rDto.replyContent }}</el-text>
									</div>
									<div style = "margin-left: 10px; text-align: right; flex-grow: 1; min-width: 160px">
										<el-text class = "text">{{ formatDate(rDto.replyDate) }}</el-text>
									</div>
								</div>
								
								<ul v-if="nrDtoList[index]?.dtoList" v-for = "(nrDto) in nrDtoList[index].dtoList" key = "nrDto.rno" style = "border-radius: 0.5em; border: 0.1em solid #494949; padding-left: 0; background-color: rgb(46,46,46)" class="p-2 m-3 mt-2 mb-0">
									<li v-show="isNested[index]" style = "list-style-type: none;" class = "nestedList">
										<div style = "display: grid;">
											<div style = "display: flex; justify-content: space-between; align-items: center; ">
												<div style = "flex-grow: 1; min-width: 90px">
													<el-popover :width = "10" effect = "dark" placement = "top" popper-style = "text-align: center" title = "UUID" trigger = "hover">
														<template #reference>
															<el-button class = "name" style = "color: white" link>{{ nrDto.replyWriter }}</el-button>
														</template>
														{{ nrDto.replyWriterUuid }}
													</el-popover>
												</div>
												<div style = "text-align: left; margin-left: auto; flex-grow: 20">
													<el-text class = "text">{{ nrDto.replyContent }}</el-text>
												</div>
												<div style = "margin-left: 10px; text-align: right; flex-grow: 1; min-width: 160px">
													<el-text class = "text">{{ formatDate(nrDto.replyDate) }}</el-text>
												</div>
											</div>
										</div>
									</li>
								</ul>
								
								<div v-show = "isVisible[index]" @click.stop class = "nestedReplyPost p-3 m-3 pb-2 pt-2 mt-2"
										 style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
									<div class = "mb-1" style = "color:rgba(97,255,176,0.8)">{{ user.name }}</div>
									<el-input v-model = "replyContent" :autosize = "{minRows: 3}" type = "textarea"/>
									<div style = "text-align: end">
										<el-button class = "mt-2" round size = "small" type = "primary" @click = "reply">Reply</el-button>
									</div>
								</div>
							</div>
							
							<div v-if = "listEnd && listEnd[index]" style = "border-bottom: 1px dashed rgba(70,130,180,0.17);" class="mt-2"></div>
						</li>
					</ul>
				</el-space>
				
			</div>
			<hr style = "background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
		</div>
		
		<div v-if = "focused" class = "replyPost p-3 m-3 pb-2 pt-2"
				 style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
			<div class = "mb-1" style = "color:rgba(97,255,176,0.8)">{{ user.name }}</div>
			<el-input v-model = "replyContent" :autosize = "{minRows: 3}" type = "textarea" @click.stop/>
			<div style = "text-align: end">
				<el-button class = "mt-2" round size = "small" type = "primary" @click = "reply">Reply</el-button>
			</div>
		</div>
	</main>
	</html>
</template>

<style scoped>
.text {
    color: rgba(255, 255, 255, 0.82);
    font-size: 1em;
    font-weight: lighter;
}

h2 {
    font-size: 2.5em
}

h3 {
    font-size: 1.5em;
    color: rgba(248, 248, 248, 0.89);
}
</style>