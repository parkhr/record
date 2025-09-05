import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layout/DefaultLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/',
      component: DefaultLayout,
      children: [
        {
          path: '/record/list',
          name: 'record',
          component: () => import('../views/RecordView.vue'),
        },
        {
          path: '/layer',
          name: 'layer',
          component: () => import('../views/LayerView.vue'),
        },
        {
          path: '/storage',
          name: 'storage',
          component: () => import('../views/StorageView.vue'),
        },
        {
          path: '/reserve',
          name: 'reserve',
          component: () => import('../views/ReserveView.vue'),
        },
        {
          path: '/system',
          name: 'system',
          component: () => import('../views/SystemView.vue'),
        },
        {
          path: '/system/role',
          name: 'systemRole',
          component: () => import('../views/SystemRoleView.vue'),
        },
        {
          path: '/system/menu',
          name: 'systemMenu',
          component: () => import('../views/SystemMenuView.vue'),
        },
        {
          path: '/economy/spend',
          name: 'economySpend',
          component: () => import('../views/EconomySpend.vue'),
        },
        {
          path: '/economy/active',
          name: 'economyActive',
          component: () => import('../views/EconomyActive.vue'),
        },
        {
          path: '/economy/dashboard',
          name: 'economyDashboard',
          component: () => import('../views/EconomyDashboard.vue'),
        },
        {
          path: '/economy/todo',
          name: 'todo',
          component: () => import('../views/TodoView.vue'),
        },
        {
          path: '/economy/word',
          name: 'word',
          component: () => import('../views/WordView.vue'),
        },
        {
          path: '/gov/application',
          name: 'application',
          component: () => import('../views/Application.vue'),
        }
      ]
    },
  ],
})

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('accessToken')

  // 로그인 페이지는 토큰 없이 접근 허용
  if (to.path === '/login') {
    if (token) {
      // 이미 로그인된 상태라면 기록 페이지로 리디렉션
      next('/')
      return
    }

    next()
    return
  }

  // 나머지 페이지는 토큰 없으면 로그인으로 리디렉션
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router
