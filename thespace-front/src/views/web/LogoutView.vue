<script setup lang = "ts">
import axios from "axios";

axios.post("/user/logout", {withCredentials: true}).then(res =>
{
  document.cookie = 'isRemember=false; path=/; false';
  sessionStorage.removeItem("userInfo");
  sessionStorage.setItem("login", "false");
  window.location.href = res.data.redirectUrl;
}
)
</script>
<template>

</template>
