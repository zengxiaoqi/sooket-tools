const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  serverInfo: state => state.app.serverInfo,
  serverList: state => state.connect.serverList,
  websocket: state => state.connect.websocket,
}
export default getters
