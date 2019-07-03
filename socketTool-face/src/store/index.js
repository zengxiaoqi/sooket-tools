import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import connect from './modules/connect'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    settings,
    connect
  },
  getters
})

export default store
