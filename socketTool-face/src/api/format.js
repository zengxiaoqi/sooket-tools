import request from '@/utils/request'

export function formatStr(data) {
  return request({
    url: '/formatStr',
    method: 'post',
    data
  })
}
