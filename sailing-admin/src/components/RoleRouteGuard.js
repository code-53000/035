import { getToken, getRole } from '../utils/auth'
import { ElMessage } from 'element-plus'

export function createRoleGuard(router) {
  router.beforeEach((to, from, next) => {
    const token = getToken()

    if (to.path === '/login') {
      if (token) {
        next('/')
      } else {
        next()
      }
      return
    }

    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }

    if (to.meta && to.meta.roles) {
      const userRole = getRole()
      if (!userRole || !to.meta.roles.includes(userRole)) {
        ElMessage.error('没有权限访问该页面')
        next('/')
        return
      }
    }

    next()
  })
}
