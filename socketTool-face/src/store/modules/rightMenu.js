/**
* 菜单右键
* */
const state = {
    rightMenuVisible: false,
    rightMenuLeft: 0,
    rightMenuTop: 0,
};

const mutations = {

    SET_VISIBLE: (state, data) => {
        state.rightMenuVisible = data;
    },

    SET_LEFT: (state, data) => {
        state.rightMenuLeft = data;
    },
    SET_TOP: (state, data) => {
        state.rightMenuTop = data;
    },
};

const actions = {
    setVisible({ commit }, data){
        commit('SET_VISIBLE', data)
    },
    setLeft({ commit }, data){
        commit('SET_LEFT', data)
    },
    setTop({ commit }, data){
        commit('SET_TOP', data)
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions
}
