import Admin from '@/components/Admin'
import Feed from '@/components/Feed'
import Login from '@/components/Login'
import MeRedirector from '@/components/MeRedirector'
import NotFound from '@/components/NotFound'
import Settings from '@/components/Settings'
import SignUp from '@/components/SignUp'
import UserProfile from '@/components/UserProfile'
import UserProfileAllPosts from '@/components/UserProfileAllPosts'
import UserProfileSinglePost from '@/components/UserProfileSinglePost'
import UserProfileUserList from '@/components/UserProfileUserList'
import Vue from 'vue'
import Router from 'vue-router'
import AppState from "../support/AppState";

Vue.use(Router);

const router = new Router({
  mode: "history",

  routes: [
    {
      path: '/404',
      name: 'NotFound',
      component: NotFound
    },

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
      path: '/:vanity',
      component: UserProfile,
      props: true,
      children: [
        { path: "", component: UserProfileAllPosts, props: true },
        { path: "posts/:postId", component: UserProfileSinglePost, props: true },
        { path: "followers", component: UserProfileUserList, props: (route) => ({type: 'followers', vanity: route.params.vanity}) },
        { path: "following", component: UserProfileUserList, props: (route) => ({type: 'followees', vanity: route.params.vanity}) }
      ]
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
    },

    {
      path: '*',
      redirect: '/404'
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
