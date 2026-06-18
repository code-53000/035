import request from '../utils/request'

export function getCoachList(params) {
  return request({
    url: '/coaches',
    method: 'get',
    params
  })
}

export function getMySchedule(params) {
  return request({
    url: '/coaches/schedule/mine',
    method: 'get',
    params
  })
}

export function getCoachSchedule(coachId, params) {
  return request({
    url: '/coaches/' + coachId + '/schedule',
    method: 'get',
    params
  })
}

export function addSchedule(data) {
  return request({
    url: '/coaches/schedule',
    method: 'post',
    data
  })
}

export function updateSchedule(id, data) {
  return request({
    url: '/coaches/schedule/' + id,
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: '/coaches/schedule/' + id,
    method: 'delete'
  })
}
