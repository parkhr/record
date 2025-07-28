<template>
  <div style="min-height: 100vh; width: 100vw; display: flex; flex-direction: column; overflow-x: hidden;">
    <a-layout-header>
      <a-menu
        theme="dark"
        mode="horizontal"
        :selectedKeys="[selectedMenu]"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item v-for="menu in menus" :key="menu.link" @click="onMenuClick(menu)">
          {{ menu.name }}
        </a-menu-item>
      </a-menu>
    </a-layout-header>

    <div style="background: #fff;">
      <a-row style="background: #fff;">
        <a-col :span="2" style="background: #fff;">
          <!-- 사이드 메뉴 -->
          <a-menu mode="inline" :selectedKeys="[selectedChildMenu]">
            <a-menu-item v-for="menu in childMenus" :key="menu.link" @click="onChildMenuClick(menu)">
              {{ menu.name }}
            </a-menu-item>
          </a-menu>
        </a-col>

        <a-col :span="22">
          <!-- 페이지 내용 -->
          <a-row>
            <a-col :span="21">
              <!-- <a-page-header :title="selectedChildMenuName" @back="() => null" /> -->
                <a-page-header :title="selectedChildMenuName"/>
            </a-col>
            <a-col :span="3">
              <div style="display: flex; align-items: center; height: 100%;">
                <a-breadcrumb>
                  <a-breadcrumb-item>{{selectedMenuName}}</a-breadcrumb-item>
                  <a-breadcrumb-item>{{selectedChildMenuName}}</a-breadcrumb-item>
                </a-breadcrumb>
              </div>
            </a-col>
          </a-row>
          <router-view />
        </a-col>
      </a-row>
    </div>

    <a-layout-footer style="text-align: center; width: 100%; margin-top: auto;">
      Ant Design ©2018 Created by Ant UED
    </a-layout-footer>
  </div>
</template>

<script setup>
import api from '@/api/axios'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()

const menus = ref([])
const childMenus = ref([])

// 현재 경로에 따라 selectedKey 설정
const selectedMenu = ref('')
const selectedMenuName = ref('')
const selectedChildMenu = ref('')
const selectedChildMenuName = ref('')

// 메뉴 클릭 시 key를 기준으로 라우팅
const onMenuClick = async (menu) => {
  selectedMenu.value = menu.link
  selectedMenuName.value = menu.name
  
  // child menu 의 가장 첫번째를 찾아 routing
  const params = {
    menuLevel : 2,
    parentId : menu.id
  }

  const response = await api.get('/api/menu', {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
    },
    params: params
  });

  childMenus.value = response.data
  selectedChildMenu.value = childMenus.value[0].link
  selectedChildMenuName.value = childMenus.value[0].name

  router.push(childMenus.value[0].link)
}

const onChildMenuClick = (menu) => {
  selectedChildMenu.value = menu.link
  selectedChildMenuName.value = menu.name
  router.push(menu.link)
}

onMounted(async () => {
  let params = {
    menuLevel : 1,
  }

  try {
    let response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    menus.value = response.data
    selectedMenu.value = menus.value[menus.value.length - 1].link
    selectedMenuName.value = menus.value[menus.value.length - 1].name

    params = {
      menuLevel : 2,
      parentId : menus.value[menus.value.length - 1].id
    }

    response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    childMenus.value = response.data
    selectedChildMenu.value = childMenus.value[0].link
    selectedChildMenuName.value = childMenus.value[0].name

    router.push(selectedChildMenu.value)
  } catch(error) {
    
  }
})
</script>