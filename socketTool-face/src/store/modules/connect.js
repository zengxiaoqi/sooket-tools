import Vue from 'vue'
import Vuex from 'vuex'
import websocket from "../../websocket";

const state = {
  websocket: new websocket('ws://localhost:9001/websocket/TCP_SERVER')
}

const mutations = {
    SET_WEBSOCKET:(state, data) => {
        state.websocket = data;
    },

  SET_SERVER_LIST: (state, data) => {
    state.websocket.serverList = data;
  },
}

const actions = {
  setServerList({ commit }, data){
    commit('SET_SERVER_LIST', data)
  },
  setWebsocket({ commit }, data){
    commit('SET_WEBSOCKET', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
