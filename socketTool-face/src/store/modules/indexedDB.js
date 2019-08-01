import defaultSettings from '@/settings'


const { dbName } = defaultSettings

const state = {
    dbName: dbName,
    version: 1,
    db: null,   //indexDB 数据库
    httpList: [],
};

const mutations = {
    /*SET_DBNAME: (state, data) => {
        state.dbName = data;
    },
    */
    SET_VERSION: (state, data) => {
        state.version = data;
    },

    SET_DB: (state, data) => {
        state.db = data;
    },
    SET_HTTPLIST: (state, data) => {
        state.httpList = data;
    },
    ADD_HTTPLIST: (state, data) => {
        state.httpList.push(data);
    },
    CLEAR_HTTPLIST: (state) => {
        state.httpList.splice(0)
    }
};

const actions = {
    /*setdbname({ commit }, data){
        commit('SET_DBNAME', data)
    },*/
    setversion({ commit }, data){
        commit('SET_VERSION', data)
    },
    setdb({ commit }, data){
        commit('SET_DB', data)
    },
    sethttplist({ commit }, data){
        commit('SET_HTTPLIST', data)
    },
    addhttplist({ commit }, data){
        commit('ADD_HTTPLIST', data)
    },
    clearhttplist({ commit }, data){
        commit('CLEAR_HTTPLIST', data)
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
