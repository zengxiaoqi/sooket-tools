// http.js
import axios from 'axios'

import {baseUrl} from "../env/env";

// 环境的切换
if (process.env.NODE_ENV === 'development') {
    axios.defaults.baseURL = baseUrl;
} else if (process.env.NODE_ENV === 'production') {
    axios.defaults.baseURL = baseUrl;
}

// 请求拦截器
axios.interceptors.request.use(
    config => {
        //token && (config.headers.Authorization = token)
        return config
    },
    error => {
        return Promise.error(error)
    })

axios.defaults.timeout = 10000;

//axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';

// 响应拦截器
axios.interceptors.response.use(response => {
    if (response.status === 200) {
        if (response.data.code === 511) {
            // 未授权调取授权接口
        } else if (response.data.code === 510) {
            // 未登录跳转登录页
        } else {
            //成功
            return Promise.resolve(response)
        }
    } else {
        return Promise.reject(response)
    }
}, error => {
    // 我们可以在这里对异常状态作统一处理
    if (error.response.status) {
        // 处理请求失败的情况
        // 对不同返回码对相应处理
        return Promise.reject(error.response)
    }
})

// get 请求
export function httpGet({
                            url,
                            headers = {},
                            params = {}
                        }) {
    return new Promise((resolve, reject) => {
        axios.get(url, {
            headers,
            params
        }).then((res) => {
            resolve(res.data)
        }).catch(err => {
            reject(err)
        })
    })
}

// post请求
export function httpPost({
                             url,
                             headers = {},
                             params = {},
                             data = {},
                         }) {
    return new Promise((resolve, reject) => {
        axios({
            url,
            method: 'post',
            // 发送的数据
            data,
            // url参数
            params,
            headers

        }).then(res => {
            resolve(res.data)
        })
    })
}
/*export function httpPost({
                             url,
                             headers = {},
                             params = {},
                             data = {},
                         }) {
    return new Promise((resolve, reject) => {
        axios({
            url,
            method: 'post',
            transformRequest: [function (data) {
                let ret = '';
                for (let it in data) {
                    ret += encodeURIComponent(it) + '=' + encodeURIComponent(data[it]) + '&'
                }
                return ret
            }],
            // 发送的数据
            data,
            // url参数
            params,
            headers

        }).then(res => {
            resolve(res.data)
        })
    })
}*/
