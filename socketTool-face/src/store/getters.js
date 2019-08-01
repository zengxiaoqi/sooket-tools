const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  serverInfo: state => state.app.serverInfo,
  serverList: state => state.connect.serverList,
  websocket: state => state.connect.websocket,
  dbName : state => state.indexedDB.dbName,
  db : state => state.indexedDB.db,
  version : state => state.indexedDB.version,
}
export default getters
