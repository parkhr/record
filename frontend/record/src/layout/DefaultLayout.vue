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
const selectedMenuId = ref('')
const selectedMenuName = ref('')
const selectedChildMenu = ref('')
const selectedChildMenuId = ref('')
const selectedChildMenuName = ref('')

// 메뉴 클릭 시 key를 기준으로 라우팅
const onMenuClick = async (menu) => {
  selectedMenu.value = menu.link
  selectedMenuId.value = menu.id
  selectedMenuName.value = menu.name

  localStorage.setItem('selectedMenu', menu.link)
  localStorage.setItem('selectedMenuId', menu.id)
  localStorage.setItem('selectedMenuName', menu.name)
  
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
  selectedChildMenuId.value = childMenus.value[0].id
  selectedChildMenuName.value = childMenus.value[0].name

  localStorage.setItem('selectedChildMenu', childMenus.value[0].link)
  localStorage.setItem('selectedChildMenuId', childMenus.value[0].id)
  localStorage.setItem('selectedChildMenuName', childMenus.value[0].name)

  router.push(childMenus.value[0].link)
}

const onChildMenuClick = (menu) => {
  selectedChildMenu.value = menu.link
  selectedChildMenuId.value = menu.id
  selectedChildMenuName.value = menu.name

  localStorage.setItem('selectedChildMenu', menu.link)
  localStorage.setItem('selectedChildMenuId', menu.id)
  localStorage.setItem('selectedChildMenuName', menu.name)

  router.push(menu.link)
}

onMounted(async () => {
  try {
    let params = {
      menuLevel : 1,
    }
    let response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    menus.value = response.data

    params = {
      menuLevel : 2,
      parentId : localStorage.getItem('selectedMenuId') || menus.value[menus.value.length - 1].id
    }

    response = await api.get('/api/menu', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    childMenus.value = response.data
  } catch(error) {
    console.log(error)
  }

  // 메뉴가 있는 경우 이전메뉴 불러오기
  if(localStorage.getItem('selectedMenu') != null 
    && localStorage.getItem('selectedMenuId') != null 
    && localStorage.getItem('selectedMenuName') != null
    && localStorage.getItem('selectedChildMenu') != null
    && localStorage.getItem('selectedChildMenuId') != null
    && localStorage.getItem('selectedChildMenuName') != null){
    selectedMenu.value = localStorage.getItem('selectedMenu')
    selectedMenuId.value = localStorage.getItem('selectedMenuId')
    selectedMenuName.value = localStorage.getItem('selectedMenuName')
    selectedChildMenu.value = localStorage.getItem('selectedChildMenu')
    selectedChildMenuId.value = localStorage.getItem('selectedChildMenuId')
    selectedChildMenuName.value = localStorage.getItem('selectedChildMenuName')
  }else {
    selectedMenu.value = menus.value[menus.value.length - 1].link
    selectedMenuName.value = menus.value[menus.value.length - 1].name
    localStorage.setItem('selectedMenu', menus.value[menus.value.length - 1].link)
    localStorage.setItem('selectedMenuId', menus.value[menus.value.length - 1].id)
    localStorage.setItem('selectedMenuName', menus.value[menus.value.length - 1].name)
    selectedChildMenu.value = childMenus.value[0].link
    selectedChildMenuName.value = childMenus.value[0].name
    localStorage.setItem('selectedChildMenu', childMenus.value[0].link)
    localStorage.setItem('selectedChildMenuId', childMenus.value[0].id)
    localStorage.setItem('selectedChildMenuName', childMenus.value[0].name)
  }

  router.push(selectedChildMenu.value)
})
</script>