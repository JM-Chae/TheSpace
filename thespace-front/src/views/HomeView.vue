<script lang = "ts" setup>
import {RouterLink, RouterView} from 'vue-router'
import {computed} from "vue";

const login = computed(() => sessionStorage.getItem('login') === "true")


</script>

<template>
	<header>
		<div class = "wrapper">
			<nav>
				<RouterLink to = "/space">Home</RouterLink>
				<RouterLink to = "/post">POST</RouterLink>
				<RouterLink to = "/list">LIST</RouterLink>
				<RouterLink v-if = "login" to = "/user/logout">LOGOUT</RouterLink>
				<RouterLink v-else to = "/user/login">LOGIN</RouterLink>
			</nav>
		</div>
	</header>
	
	<RouterView/>
	<main>
	
	</main>
</template>
