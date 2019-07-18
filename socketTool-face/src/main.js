// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

//element-ui css 不然无法显示左侧菜单栏数据
import 'element-ui/lib/theme-chalk/index.css'

import '@/styles/index.scss' // global css
import '@/icons' // icon

//代码高亮文件引入
import VueHighlightJS from './utils/vue-highlightjs.js'
Vue.use(VueHighlightJS);

// 生产环境中注释掉以下语句
//import '../mock/index.js'

import store from './store/'
Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(store)


//websocket
import ws from '@/utils/ws'
Vue.prototype.$ws = ws

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
