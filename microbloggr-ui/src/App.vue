<template>
  <div id="app">
    <div class="ui fixed inverted menu">
      <div class="ui container">
        <router-link class="header item" to="/">
          Microbloggr
        </router-link>

        <div class="ui simple dropdown item right" v-if="auth">
          <sui-image
            :src="userPicture"
            class="spaced avatar"
          />

          <span>{{ user.name || user.vanity }}</span>
          <i class="dropdown icon"></i>

          <sui-menu>
            <router-link to="/me" class="item">
              <sui-icon name="user"/>
              Profile
            </router-link>
            <router-link to="/settings" class="item">
              <sui-icon name="settings"/>
              Settings
            </router-link>

            <template v-if="admin">
              <sui-divider/>
              <router-link to="/admin" class="item">
                <sui-icon name="spy"/>
                Admin
              </router-link>
            </template>

            <sui-divider/>

            <router-link to="/logout" class="item">
              <sui-icon name="power"/>
              Log out
            </router-link>
          </sui-menu>
        </div>
      </div>
    </div>

    <sui-container id="main-container">
      <router-view/>
    </sui-container>
  </div>
</template>

<script>
  import AppState from "./support/AppState";
  import HTTP from "./support/http";

  export default {
    name: 'App',

    data() {
      return {
        get auth() {
          return AppState.sessionId !== undefined;
        },

        get admin() {
          return AppState.isAdmin;
        },

        get user() {
          return AppState.user;
        }
      }
    },

    computed: {
      userPicture() {
        if (this.user.pictureId) {
          return `${HTTP.defaults.baseURL}/pictures/${this.user.pictureId}`;
        }

        return `https://www.gravatar.com/avatar/${this.user.emailHash}?s=640&d=retro`
      }
    }
  }
</script>

<style>
  #app {
    font-family: 'Lato', 'Helvetica Neue', Arial, Helvetica, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }

  .ui.menu .item img.logo {
    margin-right: 1.5em;
  }

  #main-container {
    padding-top: 7em;
  }
</style>
