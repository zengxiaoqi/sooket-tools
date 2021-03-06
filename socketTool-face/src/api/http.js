import request from '@/utils/request'

export function createServer(data) {
    return request({
        url: '/http/createServer',
        method: 'post',
        data
    })
}
export function sendResp(data) {
    return request({
        url: '/http/sendResp',
        method: 'post',
        data
    })
}

export function getHttpMessage(data) {
    return request({
        url: '/http/getHttpMessage',
        method: 'post',
        data
    })
}

export function delHtppNode(data) {
    return request({
        url: '/http/delHtppNode',
        method: 'post',
        data
    })
}

export function getHttpServerTree(params) {
    return request({
        url: '/http/getHttpServerTree',
        method: 'get',
        params: params,
    })
}
