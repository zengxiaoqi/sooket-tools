<template>
  <div id="app">
    <!--<img src="./assets/logo.png">-->
      <!--<router-view/>-->
      <!-- keep-alive 页面刷新内容保留 -->
      <keep-alive>
        <router-view/>
      </keep-alive>
  </div>
</template>

<script>
import {websocketUrl} from "./env/env";
import IndexedDB from "@/utils/indexedDB.js"
import store from '@/store/';
export default {
  name: 'App',
    created() {
        //在页面加载时读取sessionStorage里的状态信息
        if (sessionStorage.getItem("store") ) {
            this.$store.replaceState(Object.assign({}, this.$store.state,JSON.parse(sessionStorage.getItem("store"))));
            sessionStorage.removeItem('store');
        }

        //在页面刷新时将vuex里的信息保存到sessionStorage里
        window.addEventListener("beforeunload",()=>{
            sessionStorage.setItem("store",JSON.stringify(this.$store.state))
        })

        // 创建websocket连接
        let Socket = this.$store.state.socket;
        if(Socket == null) {
            //this.$ws.init(process.env.VUE_APP_WEBSOCKET_URL,this.$ws.saveSocket)
            this.$ws.init(websocketUrl,this.$ws.saveSocket)
        }

        //创建indexedDB数据库
        IndexedDB.openDB(store.state.indexedDB.dbName,
                        store.state.indexedDB.version,
                        store.state.indexedDB.db,
                        this.dbInfo,function (db) {
            console.log("保存DB信息");
            console.log(db);
            store.dispatch("indexedDB/setdb", db);
        });
    },
    data(){
        return {
            dbInfo: [{
                name: "http_info",
                key: "id"
            },{
                name: "clientTree_info",
                key: "id"
            },{
                name: "http_server_info",
                key: "id"
            }],
        }
    },
}
</script>

<style>
/*#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  !*margin-top: 60px;*!
}*/
</style>
