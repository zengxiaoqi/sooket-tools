import request from '@/utils/request'

export function getServerInfo(params) {
  return request({
    url: '/getServerList',
    method: 'get',
    params: params,
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

export function getRcvMsg(params) {
    return request({
        url: '/getRcvMsg',
        method: 'get',
        params:params
    })
}


export function startServer(data) {
    return request({
        url: '/createServer',
        method: 'post',
        data
    })
}

export function closeServer(params) {
    return request({
        url: '/stopServer',
        method: 'get',
        params:params
    })
}

export function getIP() {
    return request({
        url: '/getIP',
        method: 'get',
        params:{}
    })
}
