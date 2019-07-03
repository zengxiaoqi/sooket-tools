import Vue from 'vue'
import Vuex from 'vuex'
import websocket from "../../websocket";

const state = {
  websocket: new websocket('ws://localhost:9001/websocket/DPS007')
}

const mutations = {
  SET_SERVER_LIST: (state, data) => {
    state.websocket.serverList = data;
  },
}

const actions = {
  setServerList({ commit }, data){
    commit('SET_SERVER_LIST', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
