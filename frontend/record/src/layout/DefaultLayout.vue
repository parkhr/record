<template>
  <a-layout style="height: 100vh; width: 100vw;">
    <a-layout-header>
      <!-- <a-menu
        theme="dark"
        mode="horizontal"
        :selectedKeys="[selectedKey]"
        @click="onMenuClick"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item key="/record">
          <router-link to="/record">기록물관리</router-link>
        </a-menu-item>
        <a-menu-item key="/layer">
          <router-link to="/layer">계층관리</router-link>
        </a-menu-item>
        <a-menu-item key="/storage">
          <router-link to="/storage">서고관리</router-link>
        </a-menu-item>
        <a-menu-item key="/reserve">
          <router-link to="/reserve">예약관리</router-link>
        </a-menu-item>
        <a-menu-item key="/system">
          <router-link to="/system">시스템관리</router-link>
        </a-menu-item>
      </a-menu> -->

      <a-menu
        theme="dark"
        mode="horizontal"
        :selectedKeys="[selectedKey]"
        @click="onMenuClick"
        :style="{ lineHeight: '64px' }"
      >
        <a-menu-item v-for="menu in menus" :key="menu.link">
          <router-link :to="menu.link">{{ menu.name }}</router-link>
        </a-menu-item>
      </a-menu>
    </a-layout-header>

    <a-layout-content>
      <a-layout style="padding: 24px 0; background: #fff">
        <a-layout-sider width="200" style="background: #fff">
          <a-menu
            v-model:selectedKeys="selectedKey2"
            mode="inline"
            style="height: 100%"
          >
            <a-menu-item key="9">임시등록</a-menu-item>
            <a-menu-item key="10">option10</a-menu-item>
            <a-menu-item key="11">option11</a-menu-item>
            <a-menu-item key="12">option12</a-menu-item>
          </a-menu>
        </a-layout-sider>
        <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
          <a-row>
            <a-col :span="21">
              <a-page-header
                title="임시등록"
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

const route = useRoute()
const router = useRouter()

// 현재 경로에 따라 selectedKey 설정
const selectedKey = ref(route.path)
const selectedKey2 = ref([''])

watch(route, () => {
  selectedKey.value = route.path
})

// 메뉴 클릭 시 key를 기준으로 라우팅
const onMenuClick = ({ key }) => {
  selectedKey.value = key
  router.push(key)
}


const menus = ref([])

onMounted(async () => {

  let params = {
    menuLevel : 1,
    // parentId 
  }

  const response = await api.get('/api/menu', {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
    },
    params: params
  });

  menus.value = response.data
})
</script>