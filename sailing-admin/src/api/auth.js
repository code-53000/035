import request from '../utils/request'
import { setToken, setUser, removeToken } from '../utils/auth'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  }).then(res => {
    if (res.data?.token) {
      setToken(res.data.token)
      if (res.data.user) {
        setUser(res.data.user)
      }
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
