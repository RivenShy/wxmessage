// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
// import router from './router'
import router from './router/router';
// import indexRouter from './router/index';
// import VueRouter from 'vue-router';

Vue.config.productionTip = false
Vue.use(router)
// Vue.use(VueRouter)
// const router = new VueRouter({
//   routes: indexRouter
// })
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})


// new Vue({
//   router,
//   // store,
//   // i18n,
//   render: h => h(App)
// }).$mount('#app')
