<script setup lang = "ts">
import axios from "axios";

sessionStorage.removeItem("userInfo")
sessionStorage.setItem("login", "false")
axios.get("/user/logout", {withCredentials: true}).then(res =>
{
  document.cookie = 'isRemember=false';
  window.location.href = res.data.redirectUrl
}
)
</script>
<template>

</template>
