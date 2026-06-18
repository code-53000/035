import { createRouter, createWebHistory } from 'vue-router'
import { createRoleGuard } from '../components/RoleRouteGuard'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/boats',
    children: [
      {
        path: 'boats',
        name: 'BoatList',
        component: () => import('../views/member/BoatList.vue'),
        meta: { roles: ['MEMBER', 'COACH', 'ADMIN'], title: '船只列表' }
      },
      {
        path: 'my-bookings',
        name: 'MyBookings',
        component: () => import('../views/member/MyBookings.vue'),
        meta: { roles: ['MEMBER'], title: '我的预约' }
      },
      {
        path: 'my-records',
        name: 'MyRecords',
        component: () => import('../views/member/MyRecords.vue'),
        meta: { roles: ['MEMBER'], title: '我的出海记录' }
      },
      {
        path: 'my-schedule',
        name: 'MySchedule',
        component: () => import('../views/coach/MySchedule.vue'),
        meta: { roles: ['COACH'], title: '我的排班' }
      },
      {
        path: 'coach-bookings',
        name: 'CoachBookings',
        component: () => import('../views/coach/MyBookings.vue'),
        meta: { roles: ['COACH'], title: '我的预约' }
      },
      {
        path: 'admin/boats',
        name: 'AdminBoatManage',
        component: () => import('../views/admin/BoatManage.vue'),
        meta: { roles: ['ADMIN'], title: '船只管理' }
      },
      {
        path: 'admin/coaches',
        name: 'AdminCoachSchedule',
        component: () => import('../views/admin/CoachSchedule.vue'),
        meta: { roles: ['ADMIN'], title: '教练排班管理' }
      },
      {
        path: 'admin/bookings',
        name: 'AdminBookingAudit',
        component: () => import('../views/admin/BookingAudit.vue'),
        meta: { roles: ['ADMIN'], title: '预约审批' }
      },
      {
        path: 'admin/closures',
        name: 'AdminClosureManage',
        component: () => import('../views/admin/ClosureManage.vue'),
        meta: { roles: ['ADMIN'], title: '封场管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

createRoleGuard(router)

export default router
