import {Loading} from 'element-ui'
import store from '@/store/';
import {homeUrl} from "../env/env";
export default {
    num: 0, // 重新连接次数
    loadingInstance: '', // Lading加载
    loadingOptions: { // Loading选项
        lock: true,
        text: '连接断开，正在重连',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.8)'
    },
    // 创建Socket实例
    init (url, callback) {
        let self = this
        if (!url) return false
        let api = url
        const Socket = new WebSocket(api);

        console.log("websocket创建成功 ");
        // 连接建立时触发
        Socket.onopen = function () {
            // 验证webscoket连接是否建立，是否可以进行通信
            // 0表示连接尚未建立
            let reconnectTimer = null
            if (Socket.readyState === 0) {
                // 如果延时器不等于null就先清除定时器
                if (reconnectTimer) {
                    clearTimeout(reconnectTimer)
                }
                reconnectTimer = setTimeout(function () {
                    self.init(url, callback)
                    reconnectTimer = null
                }, 500)
            }
            // 1表示连接已建立，可以进行通信
            if (Socket.readyState === 1) {
                console.log("websocket连接成功 ");
                if (self.loadingInstance) {
                    self.loadingInstance.close()
                }
                // 如果已建立连接直接返回当前实例,保存到vue状态中
                callback(Socket)
            }
        }

        // 接收消息时触发
        Socket.onmessage = function (response) {
            console.log("websocket接收到消息: "+response)
            let data = JSON.parse(response.data);
            if(data.type == "server-list") {
                store.dispatch('connect/setServerList', data.message);  //TCP-SERVER tree列表
            }
        }
        // socket关闭时触发
        Socket.onclose = function () {
            console.log("websocket连接关闭 ");
            self.reconnect(url, callback)
        }
    },

    //发送数据
    sendData (Socket, url, data) {
        let self = this
        if (url) {
            // 验证webscoket连接是否建立，是否可以进行通信
            if (Socket.readyState) {
                // 0表示连接尚未建立
                if (Socket.readyState === 0) {
                    setTimeout(function () {
                        self.sendData(Socket, url, data)
                    }, 300)
                }
                // 1表示连接已建立，可以进行通信
                if (Socket.readyState === 1) {
                    console.log("websocket发送数据");
                    Socket.send(data)
                }
            }
        }
    },
    // 重新连接
    reconnect (url, callback) {
        let windowUrl = window.location.href
        let path = windowUrl.slice(windowUrl.indexOf('#') + 1, windowUrl.length)
        if (this.num > 20) {
            window.location.href = homeUrl;
            //window.location.href = 'http://'+process.env.HOST+':'+process.env.PORT
        }
        this.num++
        if (path === '/') {
            return false
        }
        this.loadingInstance = Loading.service(this.loadingOptions)
        this.init(url, callback)
    },

    saveSocket(Socket) {
        store.dispatch('connect/setSocket', Socket);
    }
}
