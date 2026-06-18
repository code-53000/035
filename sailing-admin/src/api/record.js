import request from '../utils/request'

export function getRecordList(params) {
  return request({
    url: '/records',
    method: 'get',
    params
  })
}

export function getMyRecords(params) {
  return request({
    url: '/records/mine',
    method: 'get',
    params
  })
}

export function createRecord(data) {
  return request({
    url: '/records',
    method: 'post',
    data
  })
}

export function getRecordDetail(id) {
  return request({
    url: '/records/' + id,
    method: 'get'
  })
}
