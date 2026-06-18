import request from '../utils/request'

export function getClosureList(params) {
  return request({
    url: '/admin/closures',
    method: 'get',
    params
  })
}

export function createClosure(data) {
  return request({
    url: '/admin/closures',
    method: 'post',
    data
  })
}

export function updateClosure(id, data) {
  return request({
    url: '/admin/closures/' + id,
    method: 'put',
    data
  })
}

export function deleteClosure(id) {
  return request({
    url: '/admin/closures/' + id,
    method: 'delete'
  })
}
