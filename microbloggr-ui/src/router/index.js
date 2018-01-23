import Login from '@/components/Login'
import SignUp from '@/components/SignUp'
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

export default new Router({
  mode: "history",

  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login
    },

    {
      path: '/signup',
      name: 'SignUp',
      component: SignUp
    },

    {
      path: '/me',
      redirect: to => {
        return { path: '/ivkos' }
      }
    },

    {
      path: '/logout',
      redirect: to => {
        return { path: '/login' }
      }
    },

    {
      path: '/:vanity',
      name: 'UserProfile'
    },

    {
      path: '/:vanity/:post',
      name: 'Post'
    }
  ]
})
