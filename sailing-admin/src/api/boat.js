import request from '../utils/request'

export function getBoatList(params) {
  return request({
    url: '/boats',
    method: 'get',
    params
  })
}

export function getAdminBoatList(params) {
  return request({
    url: '/admin/boats',
    method: 'get',
    params
  })
}

export function getBoatDetail(id) {
  return request({
    url: '/boats/' + id,
    method: 'get'
  })
}

export function getAdminBoatDetail(id) {
  return request({
    url: '/admin/boats/' + id,
    method: 'get'
  })
}

export function createBoat(data) {
  return request({
    url: '/admin/boats',
    method: 'post',
    data
  })
}

export function updateBoat(id, data) {
  return request({
    url: '/admin/boats/' + id,
    method: 'put',
    data
  })
}

export function deleteBoat(id) {
  return request({
    url: '/admin/boats/' + id,
    method: 'delete'
  })
}

export function updateBoatStatus(id, status) {
  return request({
    url: '/admin/boats/' + id + '/status',
    method: 'put',
    params: { status }
  })
}
