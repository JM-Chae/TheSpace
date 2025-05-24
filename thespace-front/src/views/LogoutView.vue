<script setup lang = "ts">
import axios from "axios";

axios.get("/user/logout", {withCredentials: true}).then(res =>
{
  document.cookie = 'isRemember=false';
  sessionStorage.removeItem("userInfo");
  sessionStorage.setItem("login", "false");
  window.location.href = res.data.redirectUrl;
}
)
</script>
<template>

</template>
