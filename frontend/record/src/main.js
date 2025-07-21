import './assets/main.css'

import { createApp } from 'vue'
import Antd from 'ant-design-vue';
import App from './App.vue'
import router from './router'
import 'ant-design-vue/dist/reset.css';
import api from './api/axios'

const app = createApp(App)

app.use(router)

app.config.globalProperties.$axios = api

api.interceptors.response.use(
  response => response, error => {
    const res = error.response

    if (res && (res.status === 403 || res.status === 401)) {
      alert('로그인이 만료되었습니다. 다시 로그인해주세요.')
      sessionStorage.removeItem('accessToken')
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

app.use(Antd).mount('#app');
