import request from '../utils/request'

export function getBoatList(params) {
  return request({
    url: '/boats',
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

export function createBoat(data) {
  return request({
    url: '/boats',
    method: 'post',
    data
  })
}

export function updateBoat(id, data) {
  return request({
    url: '/boats/' + id,
    method: 'put',
    data
  })
}

export function deleteBoat(id) {
  return request({
    url: '/boats/' + id,
    method: 'delete'
  })
}
