const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  serverInfo: state => state.app.serverInfo,
  serverList: state => state.connect.websocket.serverList,
  websocket: state => state.connect.websocket,
}
export default getters
