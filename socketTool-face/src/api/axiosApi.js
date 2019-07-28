import {httpGet, httpPost} from '@/utils/http'

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
