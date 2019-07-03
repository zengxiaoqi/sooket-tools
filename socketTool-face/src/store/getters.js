const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  serverInfo: state => state.app.serverInfo,
  serverList: state => state.connect.websocket.serverList,
}
export default getters
