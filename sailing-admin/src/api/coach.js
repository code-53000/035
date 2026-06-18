import request from '../utils/request'

export function getCoachList(params) {
  return request({
    url: '/admin/coaches/list',
    method: 'get',
    params
  })
}

export function getMySchedule(params) {
  return request({
    url: '/coach/schedule',
    method: 'get',
    params
  })
}

export function getCoachScheduleList(params) {
  return request({
    url: '/admin/coaches/schedules',
    method: 'get',
    params
  })
}

export function addSchedule(data) {
  return request({
    url: '/admin/coaches/schedules',
    method: 'post',
    data
  })
}

export function updateSchedule(id, data) {
  return request({
    url: '/admin/coaches/schedules/' + id,
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: '/admin/coaches/schedules/' + id,
    method: 'delete'
  })
}
