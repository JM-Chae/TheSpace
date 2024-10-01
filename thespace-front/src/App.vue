<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <div class="wrapper">
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/post">POST</RouterLink>
        <RouterLink to="/login">LOGIN</RouterLink>

      </nav>
    </div>
  </header>

  <RouterView />
</template>
