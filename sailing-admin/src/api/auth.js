import request from '../utils/request'
import { setToken, setUser, removeToken } from '../utils/auth'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  }).then(res => {
    const token = res.data?.token
    if (token) {
      setToken(token)
    }
    const user = res.data ? { id: res.data.userId, username: res.data.username, realName: res.data.realName, role: res.data.role, phone: res.data.phone } : null
    if (user) {
      setUser(user)
    }
    return res
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  }).finally(() => {
    removeToken()
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/userinfo',
    method: 'get'
  }).then(res => {
    if (res.data) {
      setUser(res.data)
    }
    return res
  })
}
