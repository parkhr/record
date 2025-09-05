import './assets/main.css'

import { createApp } from 'vue'
import Antd, { message } from 'ant-design-vue';
import App from './App.vue'
import { createI18n } from 'vue-i18n'
import ko from './locales/ko.json'
import en from './locales/en.json'
import zh from './locales/zh.json'
import router from './router'
import 'ant-design-vue/dist/reset.css';
import api from './api/axios'

const i18n = createI18n({
  locale: 'ko',
  fallbackLocale: 'en',
  messages: { ko, en, zh }
})

const app = createApp(App).use(i18n)

app.use(router)

app.config.globalProperties.$axios = api

api.interceptors.response.use(
  response => response, error => {
    const res = error.response

    if (res && (res.status === 403 || res.status === 401)) {

      // 로그인 페이지인 경우 세션 토큰을 제거하지 않음
      // 로그인 페이지로 리다이렉트하지 않음
      if (res.config.url === '/login') {
        return Promise.reject(error)    
      }

      message.warning('로그인이 만료되었습니다. 다시 로그인해주세요.')
      sessionStorage.removeItem('accessToken')
      router.push('/login')
    }
    
    return Promise.reject(error)
  }
)

app.use(Antd).mount('#app');
