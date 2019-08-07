const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  serverInfo: state => state.app.serverInfo,
  serverList: state => state.connect.serverList,
  websocket: state => state.connect.websocket,
  dbName : state => state.indexedDB.dbName,
  db : state => state.indexedDB.db,
  version : state => state.indexedDB.version,
  rightMenuVisible: state => state.rightMenu.rightMenuVisible,
  rightMenuLeft: state => state.rightMenu.rightMenuLeft,
  rightMenuTop: state => state.rightMenu.rightMenuTop,
}
export default getters
