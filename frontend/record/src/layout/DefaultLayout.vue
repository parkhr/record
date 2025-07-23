<template>
  <a-layout style="height: 100vh; width: 100vw;">
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

    <a-layout-content>
      <a-layout style="padding: 24px 0; background: #fff">
        <a-layout-sider width="200" style="background: #fff">
          <a-menu
            mode="inline"
            :selectedKeys="[selectedChildMenu]"
            style="height: 100%"
          >
            <a-menu-item v-for="menu in childMenus" :key="menu.link" @click="onChildMenuClick(menu)">
              <!-- <router-link :to="menu.link">{{ menu.name }}</router-link> -->
               {{ menu.name }}
            </a-menu-item>
          </a-menu>
        </a-layout-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
          <a-row>
            <a-col :span="21">
              <a-page-header
                :title="selectedChildMenuName"
                @back="() => null">              
              </a-page-header>
            </a-col>
            <a-col :span="3">
              <div style="display: flex; align-items: center; height: 100%;">
                <a-breadcrumb>
                  <a-breadcrumb-item>기록물관리</a-breadcrumb-item>
                  <a-breadcrumb-item><a href="">임시등록</a></a-breadcrumb-item>
                </a-breadcrumb>
              </div>
            </a-col>
          </a-row>
        
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout-content>

    <a-layout-footer style="text-align: center">
      Ant Design ©2018 Created by Ant UED
    </a-layout-footer>
  </a-layout>
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
const selectedChildMenu = ref('')
const selectedChildMenuName = ref('')

// 메뉴 클릭 시 key를 기준으로 라우팅
const onMenuClick = async (menu) => {
  selectedMenu.value = menu.link
  
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