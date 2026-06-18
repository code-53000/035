import request from '../utils/request'

export function getClosureList(params) {
  return request({
    url: '/closures',
    method: 'get',
    params
  })
}

export function createClosure(data) {
  return request({
    url: '/closures',
    method: 'post',
    data
  })
}

export function updateClosure(id, data) {
  return request({
    url: '/closures/' + id,
    method: 'put',
    data
  })
}

export function deleteClosure(id) {
  return request({
    url: '/closures/' + id,
    method: 'delete'
  })
}
