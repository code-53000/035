import request from '../utils/request'

export function getBookingList(params) {
  return request({
    url: '/admin/bookings',
    method: 'get',
    params
  })
}

export function getMyBookings(params) {
  return request({
    url: '/member/bookings',
    method: 'get',
    params
  })
}

export function getCoachBookings(params) {
  return request({
    url: '/coach/bookings',
    method: 'get',
    params
  })
}

export function createBooking(data) {
  return request({
    url: '/member/bookings',
    method: 'post',
    data
  })
}

export function cancelBooking(id) {
  return request({
    url: '/member/bookings/' + id + '/cancel',
    method: 'put'
  })
}

export function approveBooking(id, data) {
  return request({
    url: '/admin/bookings/' + id + '/approve',
    method: 'put',
    data: data || { bookingStatus: 'APPROVED' }
  })
}

export function rejectBooking(id, data) {
  return request({
    url: '/admin/bookings/' + id + '/approve',
    method: 'put',
    data: { ...data, bookingStatus: 'REJECTED' }
  })
}

export function confirmBooking(id) {
  return request({
    url: '/coach/bookings/' + id + '/confirm',
    method: 'put'
  })
}
