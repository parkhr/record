<template>
  <div style="min-height: 100vh; width: 100vw; display: flex; flex-direction: column; overflow-x: hidden;">
    <a-row>
      <a-col :xs="24" :sm="24" :md="24" :lg="24">
        <a-layout-header>
          <a-row>
            <a-col :xs="0" :sm="0" :md="24">
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
            </a-col>
            <a-col :xs="2" :sm="2" :md="0">
              <a-button type="text" @click="drawerVisible = true" style="margin-top:16px;">
                <MenuOutlined style="color:black; font-size:20px;" />
              </a-button>
            </a-col>
          </a-row>
        </a-layout-header>
      </a-col>
    </a-row>

    <!-- 모바일 Drawer -->
    <a-drawer
      v-model:open="drawerVisible"
      placement="left"
      title="메뉴"
      :closable="true"
    >
      <a-menu mode="inline" :selectedKeys="[selectedMenu]">
        <a-menu-item v-for="menu in menus" :key="menu.link" @click="onMenuClick(menu)">
          {{ menu.name }}
        </a-menu-item>
      </a-menu>
      <a-divider />
      <a-menu mode="inline" :selectedKeys="[selectedChildMenu]">
        <a-menu-item v-for="menu in childMenus" :key="menu.link" @click="onChildMenuClick(menu)">
          {{ menu.name }}
        </a-menu-item>
      </a-menu>
    </a-drawer>

    <a-row style="background: #fff;">
      <a-col :xs="0" :sm="0" :md="2">
        <!-- 사이드 메뉴 -->
        <a-menu mode="inline" :selectedKeys="[selectedChildMenu]">
          <a-menu-item v-for="menu in childMenus" :key="menu.link" @click="onChildMenuClick(menu)">
            {{ menu.name }}
          </a-menu-item>
        </a-menu>
      </a-col>

      <a-col :xs="24" :sm="24" :md="22">
        <!-- 페이지 내용 -->
        <a-row>
          <a-col :span="21">
              <a-page-header :title="selectedChildMenuName"/>
          </a-col>
          <a-col :xs="0" :sm="0" :md="3">
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

    <a-layout-footer style="text-align: center; width: 100%; margin-top: auto;">
      Ant Design ©2018 Created by Ant UED
    </a-layout-footer>
  </div>
</template>

<script setup>
import api from '@/api/axios'
import { message } from 'ant-design-vue'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MenuOutlined } from "@ant-design/icons-vue";

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

const drawerVisible = ref(false);

// 메뉴 클릭 시 key를 기준으로 라우팅
const onMenuClick = async (menu) => {
  try {
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
    if(response.status !== 200) throw new Error();

    childMenus.value = response.data
    selectedChildMenu.value = childMenus.value[0].link
    selectedChildMenuId.value = childMenus.value[0].id
    selectedChildMenuName.value = childMenus.value[0].name

    localStorage.setItem('selectedChildMenu', childMenus.value[0].link)
    localStorage.setItem('selectedChildMenuId', childMenus.value[0].id)
    localStorage.setItem('selectedChildMenuName', childMenus.value[0].name)

    router.push(childMenus.value[0].link)
  } catch(error) {
    message.error('메뉴를 불러올 수 없습니다.')
  }
}

const onChildMenuClick = (menu) => {
  selectedChildMenu.value = menu.link
  selectedChildMenuId.value = menu.id
  selectedChildMenuName.value = menu.name

  localStorage.setItem('selectedChildMenu', menu.link)
  localStorage.setItem('selectedChildMenuId', menu.id)
  localStorage.setItem('selectedChildMenuName', menu.name)

  router.push(menu.link)
  drawerVisible.value = false;
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
    if(response.status !== 200) throw new Error();

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
  } catch(error) {
    message.error('메뉴를 불러올 수 없습니다.')
  }
})

// 뒤로가기, 앞으로가기 시 메뉴 설정
watch(
  () => router.currentRoute.value.fullPath,
  async (newPath, oldPath) => {
    // console.log('라우트 변경 감지:', oldPath, '→', newPath)
    
    let result = null;
    let path = "/" + newPath.split("/")[1]; 

    menus.value.forEach(item => {
      if(item.link === path) {
        result = item;
      }
    })

    if(!result) {
      //TODO 메뉴를 못 찾을 시, 기존 홈으로 이동
    } else {
      selectedMenu.value = result.link
      selectedMenuId.value = result.id
      selectedMenuName.value = result.name

      localStorage.setItem('selectedMenu', result.link)
      localStorage.setItem('selectedMenuId', result.id)
      localStorage.setItem('selectedMenuName', result.name)

      let params = {
        menuLevel : 2,
        parentId : localStorage.getItem('selectedMenuId')
      }

      let response = await api.get('/api/menu', {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
        },
        params: params
      });

      childMenus.value = response.data

      result = null;
      childMenus.value.forEach(item => {
        if(item.link === newPath) {
          result = item;
        }
      })

      if(!result) {
        //TODO 메뉴를 못 찾을 시, 기존 홈으로 이동
      } else {
        selectedChildMenu.value = result.link
        selectedChildMenuId.value = result.id
        selectedChildMenuName.value = result.name

        localStorage.setItem('selectedChildMenu', result.link)
        localStorage.setItem('selectedChildMenuId', result.id)
        localStorage.setItem('selectedChildMenuName', result.name)
      }
    }
  }
)
</script>