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
    url: '/sendRespons',
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
        url: '/startServer',
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

export function delServer(params) {
    return request({
        url: '/delServer',
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

export function getSocketInfo(params) {
    return request({
        url: '/getSocketInfo',
        method: 'get',
        params:params
    })
}

export function getNodeTree() {
    return request({
        url: '/getNodeTree',
        method: 'get',
        params:[]
    })
}


export function createClient(data) {
    return request({
        url: '/createClient',
        method: 'post',
        data
    })
}


export function getClientList() {
    return request({
        url: '/getClientList',
        method: 'get',
        params:[]
    })
}

export function stopClient(data) {
    return request({
        url: '/stopClient',
        method: 'post',
        data
    })
}

export function openClient(data) {
    return request({
        url: '/openClient',
        method: 'post',
        data
    })
}


export function delClient(data) {
    return request({
        url: '/delClient',
        method: 'post',
        data
    })
}


export function sendMsg(data) {
    return request({
        url: '/sendMsg',
        method: 'post',
        data
    })
}
