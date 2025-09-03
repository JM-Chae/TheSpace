import {useNotificationsStore} from '@/stores/notification';

export function initializeServiceWorkerListener() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.addEventListener('message', (event) => {
      console.log("Message from SW:", event.data);
      if (event.data && event.data.type === 'notification-received') {
        try {
          const notificationStore = useNotificationsStore();
          notificationStore.prependNotification(event.data.payload);
        } catch (error) {
          console.error("Error handling message from service worker:", error);
        }
      }
    });
  }
}
