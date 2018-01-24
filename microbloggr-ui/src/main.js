// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import AsyncMethods from 'vue-async-methods'
import SemanticUiVue from 'semantic-ui-vue'
import 'semantic-ui-css/semantic.min.css'

Vue.use(AsyncMethods);
Vue.use(SemanticUiVue);

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
