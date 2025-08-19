<template>
  <a-layout style="height: 100vh; width: 100vw;">
    <a-layout-content>
      <div class="login-wrapper">
          <a-form
            class="login-form"
            :model="formState"
            name="basic"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }"
            autocomplete="off"
            @finish="onFinish"
          >
            <a-form-item
              label="아이디"
              name="username"
              :rules="[{ required: true, message: '아이디를 입력하세요.' }]"
            >
              <a-input v-model:value="formState.username" />
            </a-form-item>

            <a-form-item
              label="비밀번호"
              name="password"
              :rules="[{ required: true, message: '비밀번호를 입력하세요.' }]"
            >
              <a-input-password v-model:value="formState.password" />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
              <a-button type="primary" html-type="submit">Submit</a-button>
            </a-form-item>
          </a-form>
      </div>
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import api from '@/api/axios'
const router = useRouter()

const formState = reactive({
  username: '',
  password: '',
})

const onFinish = async (values) => {
  try {
    const requestBody = {
      name: formState.username,
      password: formState.password
    }

    let response = await api.post('/login', requestBody, {
      headers: {
        'Content-Type': 'application/json'
      }
    })

    sessionStorage.setItem('accessToken', response.data)

    // 로그인 시, 지정된 메뉴로 이동
    //TODO API 연동 코드 중복제거
    let params = {
      menuLevel : 1,
    }
    response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    const menus = response.data;

    params = {
      menuLevel : 2,
      parentId : localStorage.getItem('selectedMenuId') || menus[menus.length - 1].id
    }

    response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    const childMenus = response.data

    localStorage.setItem('selectedMenu', menus[menus.length - 1].link)
    localStorage.setItem('selectedMenuId', menus[menus.length - 1].id)
    localStorage.setItem('selectedMenuName', menus[menus.length - 1].name)

    localStorage.setItem('selectedChildMenu', childMenus[0].link)
    localStorage.setItem('selectedChildMenuId', childMenus[0].id)
    localStorage.setItem('selectedChildMenuName', childMenus[0].name)

    router.push('/')

  } catch (error) {
    console.log(error)
    message.error('로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.');
  }
}
</script>

<style scoped>
.login-wrapper {
  height: 100vh;
  display: flex;
  justify-content: center; /* 가로 중앙 정렬 */
  align-items: center;     /* 세로 중앙 정렬 */
  background-color: #f0f2f5;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>