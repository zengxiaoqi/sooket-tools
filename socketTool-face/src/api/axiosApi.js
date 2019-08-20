import {httpGet, httpPost,httpURI,httpData} from '@/utils/http'

export function axiosGet(url,headers,params) {
    return httpGet({
        url: url,
        headers: headers,
        params: params,
    })
}

export function axiosPost(url,headers,params,data) {
    return httpPost({
        url: url,
        headers: headers,
        params: params,
        data: data,
    })
}

export function axiosURI(url,method,headers,params) {
    return httpURI({
        url: url,
        method: method,
        headers: headers,
        params: params,
    })
}

export function axiosData(url,method,headers,params,data) {
    return httpData({
        url: url,
        method: method,
        headers: headers,
        params: params,
        data: data,
    })
}
