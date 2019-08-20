import Vue from 'vue'
import Vuex from 'vuex'
import websocket from "../../websocket";

const state = {
    //websocket: new websocket('ws://localhost:9001/websocket/TCP_SERVER'),
    websocket: null,
    serverList: [],
    socket: null,
    httpServerList: [],
}

const mutations = {
    SET_WEBSOCKET:(state, data) => {
        state.websocket = data;
    },

  SET_SERVER_LIST: (state, data) => {
    state.serverList = data;
  },
    SET_HTTP_SERVER_LIST: (state, data) => {
        state.httpServerList = data;
    },
    SET_SOCKET: (state, data) => {
        state.socket = data;
    },
}

const actions = {
  setServerList({ commit }, data){
    commit('SET_SERVER_LIST', data)
  },
    setHttpServerList({ commit }, data){
        commit('SET_HTTP_SERVER_LIST', data)
    },
  setWebsocket({ commit }, data){
    commit('SET_WEBSOCKET', data)
  },
    setSocket({ commit }, data){
        commit('SET_SOCKET', data)
    }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
