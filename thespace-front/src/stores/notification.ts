import axios from "axios";
import router from "@/router";
import {defineStore} from "pinia";
import type {ListRes, Notification} from "@/types/domain";

export const useNotificationsStore = defineStore('notifications', {
      state: () => ({
        notificationList: {} as ListRes<Notification>
      }),
      actions: {
        async initializeNotifications(/*isRead: boolean*/ page: number, size: number) {
          this.notificationList = await getNotifications(page, size)
        },
        prependNotification(newNotification: Notification) {
          this.notificationList.dtoList.unshift(newNotification);
          this.notificationList.total++;
        },
        updateNotification(notificationToUpdate: Notification) {
          const index = this.notificationList.dtoList.findIndex(n => n.nno === notificationToUpdate.nno);
                      if (index !== -1) {
                        this.notificationList.dtoList[index] = notificationToUpdate;
          }
        }
      }
})

async function getNotifications(/*isRead: boolean*/ page: number, size: number) {
  return await axios.get(`/notification`, {params: {/*isRead: isRead*/ page: page, size: size}})
  .then(res => {
    return res.data
  })
}

export async function routToURL(row: any) {
  if (!row.url) return;

  const [path, queryString] = row.url.split('?');

  if (queryString) {
    const query = Object.fromEntries(new URLSearchParams(queryString));
    await router.push({ path: path, query: query });
  } else {
    await router.push({ path: path });
  }
}

export const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const now = new Date();
  const past = new Date(dateString);
  const seconds = Math.floor((now.getTime() - past.getTime()) / 1000);

  if (seconds < 10) return 'just now';
  if (seconds < 60) return `${seconds} seconds ago`;

  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return `${minutes} minute${minutes > 1 ? 's' : ''} ago`;

  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} hour${hours > 1 ? 's' : ''} ago`;

  const days = Math.floor(hours / 24);
  if (days < 30) return `${days} day${days > 1 ? 's' : ''} ago`;

  const months = Math.floor(days / 30.44); // Using average month length
  if (months < 12) return `${months} month${months > 1 ? 's' : ''} ago`;

  const years = Math.floor(days / 365.25);
  return `${years} year${years > 1 ? 's' : ''} ago`;
}