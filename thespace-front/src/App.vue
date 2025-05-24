<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {login} from '@/main'
</script>

<template>
  <header>
    <div class="wrapper pb-5" style="height: 10vh">
      <nav>
        <RouterLink to="/space">HOME</RouterLink>
				<RouterLink to="/community">COMMUNITY</RouterLink>
				<RouterLink v-if="login" to="/user/logout">LOGOUT</RouterLink>
        <RouterLink v-else to="/user/login">LOGIN</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>
