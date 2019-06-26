// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import '@/styles/index.scss' // global css
import '@/icons' // icon

import store from './store/'
Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(store)
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
