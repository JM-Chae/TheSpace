import axios from "axios";
import type {Friend, FriendshipInfo, ListRes, Notification} from "@/types/domain";
import {ElMessageBox} from "element-plus";
import {useNotificationsStore} from "@/stores/notification";
import router from "@/router";
import {defineStore} from "pinia";

export const useFriendsStore = defineStore('friends', {
  state: () => ({
    friendsList: {} as ListRes<Friend>,
    page: 1 as number,
    size: 10 as number,
    keyword: "" as string
  }),
  actions: {
    async initializeFriends() {
      this.friendsList = await getFriendsList(this.page, this.size, this.keyword)
    },
    async updateFriendsList() {
      this.page++;
      const nextPage = await getFriendsList(this.page, this.size, this.keyword)
      this.friendsList.dtoList.push(...nextPage.dtoList)
      return nextPage.dtoList.length;
    }
  }
})

export const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  date.setHours(date.getHours());
  return date.toLocaleDateString('ko-KR')
}

export async function getFriendshipInfo(toUserUuid: string) {

  const res = await axios.get<FriendshipInfo>('/friend', {params: {toUserUuid: toUserUuid}});

  return res.data;
}

export async function friendshipRequest(toUserUuid: string) {

  await ElMessageBox.confirm('Are you sure you want to send a friend request to them?', 'Friend Request',
      {
        cancelButtonText: 'Oh, Nope.',
        confirmButtonText: 'OK, let\'s go.',
        type: "warning",
        dangerouslyUseHTMLString: true,
        center: true,
      }).then( async () =>
      await axios.post('/friend', {}, {params: {toUserUuid: toUserUuid}}).then()
  ).catch(console.error);
  return  getFriendshipInfo(toUserUuid).then()
}

export async function friendshipUnblock(toUserUuid: string, fid: number) {

  await ElMessageBox.confirm('Are you sure you want to unblock to them? Really?', 'User Unblock',
      {
        cancelButtonText: 'Hmm, I\'ll think, nope.',
        confirmButtonText: 'OK, let\'s do this.',
        type: "warning",
        dangerouslyUseHTMLString: true,
        center: true,
      }).then( async () =>
          await axios.put(`/friend/${fid}/unblock`).then()
  )
  return  getFriendshipInfo(toUserUuid).then()
}

export async function friendshipNote(fid: number) {
  return await ElMessageBox.prompt("What would you like to note?", "Add a note", {
    confirmButtonText: 'Confirm',
    cancelButtonText: 'Cancel',
    dangerouslyUseHTMLString: true,
    center: true
  }
  ).then( async ({value}) => {
    await axios.put(`/friend/${fid}/note`, null, {params: {note: value}})
    return value;
  })
}

export async function friendshipBlock(toUserUuid: string) {

  await ElMessageBox.confirm('Are you sure you want to block them? My god, what did they do to you?', 'User block',
      {
        cancelButtonText: 'Umm... on second thought...',
        confirmButtonText: 'Yes, plz',
        type: "warning",
        dangerouslyUseHTMLString: true,
        center: true,
      }).then( async () =>
          await axios.post('/friend/block', {}, {params: {toUserUuid: toUserUuid}}).then()
  )
  return  getFriendshipInfo(toUserUuid).then()
}

export async function friendshipRequestDelete(toUserUuid: string, fid: number) {

  await ElMessageBox.confirm('Are you sure you want to cancel a friend request to them? No worries. It happens.', 'Cancel Friend Request',
      {
        cancelButtonText: 'Never mind.',
        confirmButtonText: 'Yes, thank you.',
        type: "warning",
        dangerouslyUseHTMLString: true,
        center: true,
      }).then( async () =>
          await axios.delete(`/friend/${fid}`).then()
  )
  return  getFriendshipInfo(toUserUuid).then()
}

export async function acceptRequest(notification: Notification) {
  await axios.post(`/friend/${notification.dataPayload.fid}/accept`).then(() => {
    //update already notification.
    const updatedNotification = notification;
    updatedNotification.dataPayload.status = 'ACCEPTED'
    useNotificationsStore().updateNotification(updatedNotification);
  })
}

export async function rejectRequest(notification: Notification) {
  await axios.delete(`/friend/${notification.dataPayload.fid}/reject`).then(() => {
    //update already notification.
    const updatedNotification = notification;
    updatedNotification.dataPayload.status = 'REJECTED'
    useNotificationsStore().updateNotification(updatedNotification);
  })
}

export async function getFriendsList(page: number, size: number, keyword: string) {
  return (await axios.get(`/friend/list`, {params: {page: page, size: size, keyword: keyword}})).data
}

export async function routToFriendMypage(row: any) {
  if (!row.fuuid) return;
  await router.push(`/mypage?uuid=${row.fuuid}`);
}