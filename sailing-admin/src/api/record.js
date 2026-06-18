import request from '../utils/request'

export function getRecordList(params) {
  return request({
    url: '/member/records',
    method: 'get',
    params
  })
}

export function getMyRecords(params) {
  return request({
    url: '/member/records',
    method: 'get',
    params
  })
}

export function getCoachRecords(params) {
  return request({
    url: '/coach/records',
    method: 'get',
    params
  })
}

export function createRecord(data) {
  return request({
    url: '/coach/records',
    method: 'post',
    data
  })
}

export function getRecordDetail(id) {
  return request({
    url: '/member/records/' + id,
    method: 'get'
  })
}
