import axios from "axios";

export async function loadCsrfToken() {
  const res = await axios.get('/user/csrf');
  const csrfToken = res.data.token;
  const csrfHeader = res.data.headerName;

  console.log(csrfToken);

  axios.defaults.headers.common[csrfHeader] = csrfToken;
}