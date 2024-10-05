<script setup lang = "ts">
import axios from "axios";
sessionStorage.removeItem("userInfo")
localStorage.setItem("login", "false")
axios.get("http://localhost:8080/user/logout", {withCredentials: true})
</script>
