import Feed from '@/components/Feed'
import Login from '@/components/Login'
import MeRedirector from '@/components/MeRedirector'
import Settings from '@/components/Settings'
import Admin from '@/components/Admin'
import SignUp from '@/components/SignUp'
import UserProfile from '@/components/UserProfile'
import Vue from 'vue'
import Router from 'vue-router'
import AppState from "../support/AppState";

Vue.use(Router);

const router = new Router({
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
      name: 'MeRedirector',
      component: MeRedirector
    },

    {
      path: '/settings',
      name: 'Settings',
      component: Settings
    },

    {
      path: '/admin',
      name: 'Admin',
      component: Admin,
      beforeEnter: (to, from, next) => {
        if (!AppState.isAdmin) {
          next("/");
          return;
        }

        next();
      }
    },

    {
      path: '/logout',
      name: 'LogOut',
      beforeEnter: (to, from, next) => {
        AppState.clear();
        next("/login");
      }
    },

    {
      path: '/:vanity/:post',
      name: 'Post'
    },

    {
      path: '/:vanity',
      name: 'UserProfile',
      component: UserProfile,
      props: true
    },

    {
      path: '/:vanity/posts/:postId',
      name: 'UserProfileSinglePost',
      component: UserProfile,
      props: true
    },

    {
      path: '/',
      name: 'Feed',
      component: Feed
    }
  ]
});

router.beforeEach((to, from, next) => {
  if (!AppState.sessionId && !['Login', 'SignUp', 'LogOut'].includes(to.name)) {
    router.replace('/login')
  } else {
    next()
  }
});

export default router;
