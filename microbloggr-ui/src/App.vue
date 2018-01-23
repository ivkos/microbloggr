<template>
  <div id="app">
    <div class="ui fixed inverted menu">
      <div class="ui container">
        <router-link class="header item" to="/">
          <img class="logo" src="https://avatars0.githubusercontent.com/u/991028">
          Microbloggr
        </router-link>

        <div class="ui simple dropdown item right">
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
  export default {
    name: 'App'
  }
</script>

<style>
  #app {
    font-family: 'Lato', 'Helvetica Neue', Arial, Helvetica, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    padding-top: 60px;
  }

  .ui.menu .item img.logo {
    margin-right: 1.5em;
  }

  #main-container {
    margin-top: 7em;
  }
</style>
