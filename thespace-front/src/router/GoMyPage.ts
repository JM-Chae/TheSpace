import router from "@/router/index";

export function goMyPage(uuid: any) {
  router.push({ name: 'mypage', query: { uuid } }).catch(() => {});
}