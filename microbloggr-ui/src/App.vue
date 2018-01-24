<template>
  <div id="app">
    <div class="ui fixed inverted menu">
      <div class="ui container">
        <router-link class="header item" to="/">
          <img class="logo" src="https://avatars0.githubusercontent.com/u/991028">
          Microbloggr
        </router-link>

        <div class="ui simple dropdown item right" v-if="auth">
          <sui-image
            src="https://avatars0.githubusercontent.com/u/991028"
            class="spaced avatar"
          />

          <span>ivkos</span>
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

  export default {
    name: 'App',

    data() {
      return {
        get auth() {
          return AppState.sessionId !== undefined;
        },

        get admin() {
          return AppState.isAdmin;
        }
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
