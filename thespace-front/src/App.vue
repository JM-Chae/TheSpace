<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {login} from '@/main'
import {onMounted, ref, watch} from 'vue'

let rawUserInfo = sessionStorage.getItem('userInfo')
const userInfo = ref(rawUserInfo ? JSON.parse(rawUserInfo) : null)

onMounted(() => {
  watch(login, (newValue) => {
    if (newValue) {
      rawUserInfo = sessionStorage.getItem('userInfo');
      userInfo.value = rawUserInfo ? JSON.parse(rawUserInfo) : null;
      console.log(login)
      console.log(userInfo.value)
    }
  })
})

</script>

<template>
  <header>
    <div class="wrapper pb-5" style="height: 10vh">
      <nav style="display: flex">
        <RouterLink to="/space">HOME</RouterLink>
				<RouterLink to="/community">COMMUNITY</RouterLink>
				<RouterLink v-if="login" to="/user/logout">LOGOUT</RouterLink>
        <RouterLink v-else to="/user/login">LOGIN</RouterLink>
        <RouterLink v-if="login" :to="{name: 'mypage', query: { uuid: userInfo?.uuid }}" style="margin-left: auto; margin-right: 0.5em">MY PAGE</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>
