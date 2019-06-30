import request from '@/utils/request'

export function getServerInfo(params) {
  return request({
    url: '/TcpServerData',
    method: 'post',
    params
  })
}

export function createServer(data) {
  return request({
    url: '/createServer',
    method: 'post',
    data
  })
}

export function sendData(data) {
  return request({
    url: '/sendData',
    method: 'post',
    data
  })
}

export function getRcvMsg(param) {
    return request({
        url: '/getRcvMsg',
        method: 'get',
        param
    })
}
