import axios from "axios";
import type {friendshipInfo} from "@/types/domain";
import {ElMessageBox} from "element-plus";

export async function getFriendshipInfo(toUserUuid: any) {

  const res = await axios.get<friendshipInfo>('/friend', {params: {toUserUuid: toUserUuid}});

  return res.data;
}

export async function friendshipRequest(toUserUuid: any) {

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

export async function friendshipUnblock(toUserUuid: any, fid: any) {

  await ElMessageBox.confirm('Are you sure you want to unblock to them? Really?', 'User Unblock',
      {
        cancelButtonText: 'Hmm, I\'ll think, nope.',
        confirmButtonText: 'OK, let\'s do this.',
        type: "warning",
        dangerouslyUseHTMLString: true,
        center: true,
      }).then( async () =>
          await axios.put(`/friend/${fid}/block`).then()
  )
  return  getFriendshipInfo(toUserUuid).then()
}

export async function friendshipBlock(toUserUuid: any) {

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

export async function friendshipRequestDelete(toUserUuid: any, fid: any) {

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