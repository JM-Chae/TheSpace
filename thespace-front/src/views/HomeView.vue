<script lang = "ts" setup>
import {RouterView} from 'vue-router'
import {computed} from "vue";

const login = computed(() => sessionStorage.getItem('login') === "true")


</script>

<template>
	<header>
		<div class = "wrapper">

		</div>
	</header>
	
	<RouterView/>
	<main>
	
	</main>
</template>
