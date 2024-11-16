<script setup lang="ts">
import {RouterLink, RouterView} from 'vue-router'
import {onMounted, ref} from "vue";
import axios from "axios";

async function getInfo()
  {
    try
      {
        await axios.get("/user/info", {withCredentials: true})
					.then(res =>
          {
            sessionStorage.setItem('login', 'true');
            sessionStorage.setItem("userInfo", JSON.stringify(res.data));
          });
        login.value = sessionStorage.getItem('login') == 'true';
      } catch (error)
      {
        return console.error("Error fetching user info" + error);
      }
  }

onMounted(()=>
  {
    const isRemember = window.document.cookie.split(';').find(cookie=>cookie.startsWith('isRemember='))?.split('=')[1]
		if(isRemember == 'true' && sessionStorage.getItem('login') != 'ture')
      {
        axios.get(`/user`, {withCredentials: true})
          .then(() =>
          {
            getInfo();
          })
      }
  }
)

const login =  ref(false);
</script>

<template>
  <header>
    <div class="wrapper pb-5">
      <nav>
        <RouterLink to="/space">Home</RouterLink>
        <RouterLink to="/post">POST</RouterLink>
				<RouterLink to="/list">LIST</RouterLink>
				<RouterLink v-if="login" to="/user/logout">LOGOUT</RouterLink>
        <RouterLink v-else to="/user/login">LOGIN</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>
