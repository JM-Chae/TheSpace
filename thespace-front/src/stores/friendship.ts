import axios from "axios";
import type {FriendshipInfo, Notification} from "@/types/domain";
import {ElMessageBox} from "element-plus";
import {useNotificationsStore} from "@/stores/notification";

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