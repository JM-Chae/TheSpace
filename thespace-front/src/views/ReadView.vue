<script lang = "ts" setup>
import {onBeforeUnmount, onMounted, ref, watch} from "vue";
import {Delete, Edit, Expand, Hide, View} from '@element-plus/icons-vue'
import axios from "axios";
import {ElMessageBox} from "element-plus";
import router from "@/router";

function getRead()
  {
    axios.get(`/board/${bno}`)
      .then(res =>
      {
        getDto.value = res.data
        path.value = getDto.value?.path || ''
      })
      .catch(error => console.error(error));

  }

function modify()
  {
    router.push({
      name: "modify", state:
        {
          title: getDto.value?.title,
          content: getDto.value?.content,
          categoryId: getDto.value?.categoryId,
          bno: bno,
          fileNames: JSON.stringify(getDto.value?.fileNames)
        }
    });
  }

function getReply()
  {
    axios.get(`/board/${bno}/reply`)
      .then(res => rDtoList.value = res.data)
      .then(res =>
        {
          if (res.dtoList != undefined)
            rno.value = res.dtoList.map((rnoV: rdto) => rnoV.rno)
        }
      )
      .then(() => getNestedReplyFunction())
      .catch(error => console.error(error));
  }

function getNestedReplyFunction()
  {
    for (let i = 0; i < rno.value.length; i++)
      {
        getNestedReply(rno.value[i], i)
      }
  }


function getNestedReply(rno: number, i: number)
  {
    axios.get(`/board/${bno}/reply/${rno}`)
      .then(res => nrDtoList.value[i] = res.data)
      .then(() =>
      {
        if (nrDtoList.value[i].total != 0 && nrDtoList.value[i].total != undefined)
          {
            isNested.value[i] = true
            nrWriterCheck.value[i] = new Array(nrDtoList.value.length).fill(false)
            for (let n = 0; n < nrDtoList.value[i].dtoList.length; n++)
              {
                nrWriterCheck.value[i][n] = user.uuid == nrDtoList.value[i].dtoList[n].replyWriterUuid;
              }
            tagName.value[i] = nrDtoList.value[i].dtoList.map(V => V.tag.split(' ')[0])
            tagUuid.value[i] = nrDtoList.value[i].dtoList.map(V => V.tag.split(' ')[1])
          }
      })
      .catch(error => console.error(error));
  }

function deleteReply(rno: number, isNR: number)
  {
    if (isNR == 0)
      {
        axios.delete(`/reply/${rno}`, {params: {bno: bno}})
          .then(() => getReply())
      } else ElMessageBox.alert('You cannot delete a reply that has a nested reply.', 'Delete Confirmation', // The goal is to prevent comment deletion, but instead, to add an API that allows for arbitrary modification of the comment's information.
      {
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true
      })
  }

function boardDelete(isR: number)
  {
    if (isR == 0)
      {
        axios.delete(`/board/${bno}`, {params: {userUuid: user.uuid}})
					.then(()=> router.back())
      } else
      {
        ElMessageBox.alert('You cannot delete a board that has a reply.', 'Delete Confirmation',
          {
            type: 'warning',
            dangerouslyUseHTMLString: true,
            center: true
          })
      }
  }

const deleteBoardAlert = (isR: number) =>
  {
    ElMessageBox.confirm('Are you sure you want to delete this Board?', 'Delete Confirmation',
      {
        cancelButtonText: 'NO',
        confirmButtonText: 'OK',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true,
        customClass: '.el-message-box'
      })
      .then(() =>
      {
        boardDelete(isR)
      }).catch(() =>
    {
      console.log('')
    })
  }

const deleteReplyAlert = (rno: number, isNR: number) =>
  {
    ElMessageBox.confirm('Are you sure you want to delete this reply?', 'Delete Confirmation',
      {
        cancelButtonText: 'NO',
        confirmButtonText: 'OK',
        type: 'warning',
        dangerouslyUseHTMLString: true,
        center: true,
        customClass: '.el-message-box'
      })
      .then(() =>
      {
        deleteReply(rno, isNR);
      }).catch(() =>
    {
      console.log('')
    })
  }

interface dto
  {
    bno: number,
    categoryName: string,
    content: string,
    createDate: string,
    fileNames: string[],
    modDate: string,
    path: string,
    title: string,
    viewCount: number,
    vote: number,
    writer: string,
    writerUuid: string,
    rCount: number,
    categoryId: number
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
    vote: number,
    tag: string,
    isNested: number
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

const tag = ref<string>('')
const path = ref<string>('')
const bno: number = window.history.state.bno;
const rno = ref<number[]>([])
const tagName = ref<Array<string[]>>([])
const tagUuid = ref<Array<string[]>>([])

const replyContent = ref<string>()
const reply = function ()
  {
    axios.post(`/board/${bno}/reply`,
      {
        replyContent: replyContent.value,
        replyWriter: user.name,
        replyWriterUuid: user.uuid,
        path: bno + "/" + nestedReply.value,
        tag: tag.value
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

async function like(no: number)
  {
    if (no == 0)
      {
        axios.put(`/like`,
          {
            userId: <string>user.id,
            rno: 0,
            bno: bno
          }
        ).then(res =>
        {
          if (getDto.value != null && getDto.value.vote != null)
            {
              getDto.value.vote += res.data
            }
        })
      } else
      {
        const response = await axios.put(`/like`,
          {
            userId: <string>user.id,
            rno: no,
            bno: 0
          }
        );
        return response.data;
      }
  }

async function rLikeCount(rno: number, Dto: any)
  {
    const result = await like(rno)
    Dto.vote += result;
  }

const writerCheck = ref(false)
const rWriterCheck = ref<boolean[]>([])
const nrWriterCheck = ref<boolean[][]>([])

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
    date.setHours(date.getHours() - 9);
    return date.toLocaleDateString('ko-KR') + ' ' + date.toLocaleTimeString('en-US', {
      hour12: true
    });
  }

const isVisible = ref<boolean[]>([]);
const focused = ref<boolean>(true)
const nestedReply = ref<number | string>('')
const isNested = ref<boolean[]>([])
const replyClose = ref<boolean>(true)

const closeAllNestedReplies = () =>
  {
    isVisible.value = isVisible.value.map(() => false);
    focused.value = true;
    replyContent.value = '';
    nestedReply.value = ''
    tag.value = '';
  };

const handleOutsideClick = (event: MouseEvent) =>
  {
    const clickedElement = event.target as HTMLElement;

    if (clickedElement.closest("#replyList"))
    {
      tag.value = '';
      return;
    }
    if (clickedElement.closest("#nestedList"))
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
      rWriterCheck.value = new Array(newValue.dtoList?.length).fill(false);

    }
  if (newValue?.dtoList != undefined)
    {
      newValue?.dtoList.forEach((_, index) =>
      {
        rWriterCheck.value[index] = user.uuid == newValue?.dtoList[index].replyWriterUuid;
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

const tempSize = history.state.size;
const tempPage = history.state.page;
const page = ref(tempPage ? tempPage : 1);
const size = ref(tempSize ? tempSize : 10)

function returnBack()
  {
		const historyURL = history.state.back
		
		if(historyURL == '/post')
      {
       router.push({path: '/community/home', state: {communityName: path.value}})
			}else
      {
        history.replaceState({communityName: path.value, size: size.value, page: page.value}, '')
        router.back()
      }
  }
</script>

<template>
	<html class = "dark">
	<main>
		<div class = "board">
			<div>
				<h2 class = "communityName">{{ path }}</h2>
			</div>
			<hr class = "mb-2 mt-2" style = "background: #25394a; height: 2px; border: 0">
			<el-row>
				<el-col :span = "16">
					<div class = "mt-3">
						<h3>{{ getDto?.title }}</h3>
					</div>
				</el-col>
				<el-col :span = "8">
					<div class = "mt-4" style = "text-align: end">
						<el-text v-if = "getDto" class = "name">{{ formatDate(getDto.createDate) }}</el-text>
					</div>
				</el-col>
				<el-col :span = "16">
					<div>
						<el-text class = "name">{{ getDto?.writerUuid }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text class = "name">{{ getDto?.writer }}</el-text>
					</div>
				</el-col>
				<el-col :span = "8">
					<div style = "text-align: end">
						<el-text class = "name">Like {{ getDto?.vote }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text class = "name">Viewed {{ getDto?.viewCount }}</el-text>
						<el-text>&nbsp/&nbsp</el-text>
						<el-text class = "name">Reply {{ getDto?.rCount }}</el-text>
					</div>
				</el-col>
			</el-row>
			
			<hr class = "mb-2 mt-2" style = "background: rgba(70,130,180,0.17); height: 0.01em; border: 0;">
			<div class = "mt-3" style = "min-height: 15em">
				<el-text v-dompurify-html = "getDto?.content" class = "text"></el-text>
			</div>
			<div class = "mt-3 mb-2" style = "display: flex; justify-content: flex-start">
				<div v-if = "getDto?.fileNames.length">
					<el-popover :show-arrow = "false" :width = "'fit-content'" effect = "dark" placement = "right-start" popper-style = "text-align: center" trigger = "click">
						<template #reference>
							<el-button clss = "button" color = "#4682B42B" round size = "small" style = "justify-self: start;" title = "Attached Files">
								<el-icon size = "15" style = "margin-right: 0.5em">
									<Expand/>
								</el-icon>
								Files List
							</el-button>
						</template>
						<li v-for = "file in getDto?.fileNames" style = "width: fit-content">
							<a
									:download = "file?.split('_').slice(1).join('_')"
									:href = "`/get/${file?.split('_')[0]}/${file?.split('_').slice(1).join('_')}`">
								{{ file?.split('_').slice(1).join('_') }}
							</a>
						</li>
					</el-popover>
				</div>
				<el-button class = "button" color = "#ff25cf" round size = "small" style = "margin-left: auto;" title = "Like!" @click = "like(0)">❤</el-button>
				<el-button placeholder = "Close Reply" round size = "small" style = "margin-left: 0.5em" type = "primary" v-bind:title = "replyClose ? 'ReplyHide' : 'ReplyView'" @click = "replyClose = !replyClose">
					<el-icon size = "15">
						<Hide v-if = "replyClose"/>
						<View v-if = "!replyClose"/>
					</el-icon>
				</el-button>
				<el-button v-if = "writerCheck" round size = "small" style = "margin-left: 0.5em" title = "Modify" type = "warning" @click = "modify">
					<el-icon size = "15">
						<Edit/>
					</el-icon>
				</el-button>
				<el-button v-if = "writerCheck && getDto" round size = "small" style = "margin-left: 0.5em" title = "Delete" type = "danger" @click = "deleteBoardAlert(getDto.rCount)">
					<el-icon size = "15">
						<Delete/>
					</el-icon>
				</el-button>
			</div>
		</div>
		
		<hr style = "background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
		
		<div v-show = "replyClose && rDtoList?.total" class = "reply">
			<div class = "replyList">
				<div class = "p-3 m-3 pt-4"
						 style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
					
					<el-space :size = "20" fill = "fill" style = "display: flex;">
						<ul v-for = "(rDto, index) in rDtoList?.dtoList" key = "rDto.rno" class = "list" style = "padding-left: 0">
							<li class = "list" style = "list-style-type: none;" @click = "toggleNested(index); nestedReply = rDto.rno">
								<div style = "display: grid;">
									<div id="replyList">
									<div style = "display: flex; justify-content: space-between; align-items: center; " >
										<div style = "flex-grow: 1; min-width: 10em; max-width: 10em; margin-right: 0.5em;">
											<el-popover :width = "10" effect = "dark" placement = "top" popper-style = "text-align: center" title = "UUID" trigger = "hover">
												<template #reference>
													<el-button class = "name" link style = "display: inline-block; color: white; max-width: 10em;">
														<span style = "overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">{{ rDto.replyWriter }}</span>
													</el-button>
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
									<div class = "pt-1" style = "display: flex; justify-content: space-between; align-items: center; ">
										<div style = "margin-left: 10.5em; display: inline-block;">
											<el-text>Like: {{ rDto.vote }}</el-text>
										</div>
										<div style = "display: inline-block;">
											<el-button v-if = "rWriterCheck[index]" round size = "small" style = "margin-left: 0.5em" title = "Delete" type = "danger" @click = "deleteReplyAlert(rDto.rno, rDto.isNested)" @click.stop>
												<el-icon size = "15">
													<Delete/>
												</el-icon>
											</el-button>
											<el-button class = "button" color = "#ff25cf" round size = "small" style = "margin-left: 0.5em;" title = "Like!" @click = "rLikeCount(rDto.rno, rDto);" @click.stop>❤</el-button>
										</div>
									</div>
									</div>
									<ul v-for = "(nrDto, i) in nrDtoList[index].dtoList" v-if = "nrDtoList[index]?.dtoList" key = "nrDto.rno" class = "p-2 m-3 mt-2 mb-0" style = "border-radius: 0.5em; border: 0.1em solid #494949; padding-left: 0; background-color: rgb(46,46,46)">
										<li v-show = "isNested[index]" id = "nestedList" style = "list-style-type: none;" @click = "tag = nrDto.replyWriter + ' ' + nrDto.replyWriterUuid + ' ' + 'To'">
											<div style = "display: grid;">
												<div style = "display: flex; justify-content: space-between; align-items: center; ">
													<div style = "flex-grow: 1; min-width: 10em; max-width: 10em; margin-right: 0.5em;">
														<el-popover :width = "10" effect = "dark" placement = "top" popper-style = "text-align: center" title = "UUID" trigger = "hover">
															<template #reference>
																<el-button class = "name" link style = "display: inline-block; color: white; max-width: 10em;">
																	<span style = "overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">{{ nrDto.replyWriter }}</span>
																</el-button>
															</template>
															{{ nrDto.replyWriterUuid }}
														</el-popover>
													</div>
													<div style = "text-align: left; margin-left: auto; flex-grow: 20">
														<span style = "color: #919191">┗　</span>
														<el-text v-if = "tagName[index] != undefined && tagName[index][i] != ''" v-model = "tagName">
															<el-popover v-if = "tagUuid[index][i] != ''" v-model = "tagUuid" :width = "10" effect = "dark" placement = "top" popper-style = "text-align: center" title = "UUID" trigger = "hover">
																<template #reference>
																	<el-button class = "name" link style = "display: inline-block; color: rgba(41,198,255,0.75); max-width: 10em;">
																		<span style = "margin-right: 0.5em; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; max-width: 10em">{{ tagName[index][i] }}</span>
																	</el-button>
																</template>
																{{ tagUuid[index][i] }}
															</el-popover>
														</el-text>
														<el-text class = "text">{{ nrDto.replyContent }}</el-text>
													</div>
													<div style = "margin-left: 10px; text-align: right; flex-grow: 1; min-width: 160px">
														<el-text class = "text">{{ formatDate(nrDto.replyDate) }}</el-text>
													</div>
												</div>
												<div class = "pt-1" style = "display: flex; justify-content: space-between; align-items: center; ">
													<div style = "margin-left: 12.5em; display: inline-block;">
														<el-text>Like: {{ nrDto.vote }}</el-text>
													</div>
													<div style = "display: inline-block;">
														<el-button v-if = "nrWriterCheck && nrWriterCheck[index] && nrWriterCheck[index][i]" round size = "small" style = "margin-left: 0.5em" title = "Delete" type = "danger" @click = "deleteReplyAlert(nrDto.rno, nrDto.isNested)" @click.stop>
															<el-icon size = "15">
																<Delete/>
															</el-icon>
														</el-button>
														<el-button class = "button" color = "#ff25cf" round size = "small" style = "margin-left: 0.5em;" title = "Like!" @click = "rLikeCount(nrDto.rno, nrDto);" @click.stop>❤</el-button>
													</div>
												</div>
											</div>
										</li>
									</ul>
									
									<div v-show = "isVisible[index]" class = "nestedReplyPost p-3 m-3 pb-2 pt-2 mt-2" style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)"
											 @click.stop>
										<div class = "mb-1" style = "color:rgba(97,255,176,0.8)">{{ user.name }}</div>
										<el-input v-model = "replyContent" :autosize = "{minRows: 3}" type = "textarea" v-bind = "{ placeholder: tag ? tag.split(' ')[2] + ' ' + tag.split(' ')[0] : undefined }"/>
										<div style = "text-align: end">
											<el-button class = "mt-2" round size = "small" type = "primary" @click = "reply">Reply</el-button>
										</div>
									</div>
								</div>
								
								<div v-if = "listEnd && listEnd[index]" class = "mt-2" style = "border-bottom: 1px dashed rgba(70,130,180,0.17);"></div>
							</li>
						</ul>
					</el-space>
				
				</div>
				<hr style = "background: rgba(70,130,180,0.17); height: 0.01em; border-width: 0">
			</div>
		</div>
		<div v-if = "focused" class = "replyPost p-3 m-3 pb-2 pt-2"
				 style = "background: rgba(255,255,255,0.06); border-radius: 0.5em; border: 0.1em solid rgba(186,186,186,0.24)">
			<div class = "mb-1" style = "color:rgba(97,255,176,0.8)">{{ user.name }}</div>
			<el-input v-model = "replyContent" :autosize = "{minRows: 3}" type = "textarea" @click.stop/>
			<div style = "text-align: end">
				<el-button class = "mt-2" round size = "small" type = "primary" @click = "reply">Reply</el-button>
			</div>
		</div>
		<div>
			<el-button color="#00bd7e" round style="position: fixed; top: 3vh; right: 2vw" @click="returnBack()">Return Back</el-button>
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