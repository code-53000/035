import request from '../utils/request'

export function getBookingList(params) {
  return request({
    url: '/bookings',
    method: 'get',
    params
  })
}

export function getMyBookings(params) {
  return request({
    url: '/bookings/mine',
    method: 'get',
    params
  })
}

export function getCoachBookings(params) {
  return request({
    url: '/bookings/coach',
    method: 'get',
    params
  })
}

export function createBooking(data) {
  return request({
    url: '/bookings',
    method: 'post',
    data
  })
}

export function cancelBooking(id) {
  return request({
    url: '/bookings/' + id + '/cancel',
    method: 'put'
  })
}

export function approveBooking(id) {
  return request({
    url: '/bookings/' + id + '/approve',
    method: 'put'
  })
}

export function rejectBooking(id, data) {
  return request({
    url: '/bookings/' + id + '/reject',
    method: 'put',
    data
  })
}

export function confirmBooking(id) {
  return request({
    url: '/bookings/' + id + '/confirm',
    method: 'put'
  })
}
