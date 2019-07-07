let baseUrl = '';
let homeUrl = '';
let websocketUrl;

if(process.env.NODE_ENV == 'production'){
    baseUrl = 'http://127.0.0.1:9001';
    homeUrl = 'http://localhost:8085';
    websocketUrl = 'ws://localhost:9001/websocket/TCP_SERVER';
} else if (process.env.NODE_ENV == 'development') {
    baseUrl = 'http://127.0.0.1:9001';
    homeUrl = 'http://localhost:8085';
    websocketUrl = 'ws://localhost:9001/websocket/TCP_SERVER';
}

export {
    baseUrl,
    homeUrl,
    websocketUrl,
}
