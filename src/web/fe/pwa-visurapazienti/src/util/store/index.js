import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    currentUser: {}
  },
  getters: {
    currentUser: state => {
      return state.currentUser
    }
  },
  mutations: {
    setCurrentUser (state, currentUser) {
      state.currentUser = currentUser
    }
  },
  actions: {}
})
