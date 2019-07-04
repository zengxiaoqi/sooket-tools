export default class websocket{
    constructor(url){
        this.sock = null; // socket连接
        this.serverList = [];
        this.init (url);
    }

    init (url) {
        this.sock = new WebSocket(url)
        this.sock.onopen = () => { this.onSockOpen() }
        this.sock.onmessage = (data) => { this.onSockMessage(data) }
        this.sock.onerror = (data) => { this.onSocketError(data) }
        this.sock.onclose = (data) => { this.onSockClose(data) }
    }

    send (data) {
        if (this.sock) {
            this.sock.send(JSON.stringify(data))
        }
    }

    onSockOpen () {
        console.log("WebSocket连接成功");
    }

    onSockMessage (e) {
        //对接收报文处理
        console.log(e);
        let data = JSON.parse(e.data);
        if(data.type == "server-list") {
            this.serverList = data.message;  //TCP-SERVER tree列表
        }

    }

    onSocketError(e){
        console.log("WebSocket连接发生错误");
    }

    onSockClose (e) {
        console.log("connection closed (" + e.code + ")");
    }

    getServerList(){
        return this.serverList;
    }
}
