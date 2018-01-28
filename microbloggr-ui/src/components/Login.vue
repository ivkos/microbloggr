<template>
  <div class="ui grid middle aligned center aligned">
    <div class="column">
      <form class="ui form large" @submit.prevent="doLogin">
        <div class="ui piled segment">
          <div class="field">
            <div class="ui left icon input">
              <input name="identity" v-model="identity" placeholder="Email address or username"/>
              <i class="user icon"></i>
            </div>
          </div>

          <div class="field">
            <div class="ui left icon input">
              <input type="password" v-model="password" name="password" placeholder="Password"/>
              <i class="lock icon"></i>
            </div>
          </div>

          <button type="submit" class="ui button fluid large primary button">Login</button>

          <div class="ui message large negative" :hidden="errorHidden">
            <i class="large warning sign icon"></i>
            {{ errorMessage }}
          </div>
        </div>
      </form>

      <div class="ui message tiny bottom attached">
        Don't have an account?
        <router-link to="signup">Sign Up</router-link>
      </div>

      <div class="ui inverted dimmer" v-bind:class="{ active: isActive }">
        <div class="ui loader"></div>
      </div>
    </div>
  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import HTTP from "../support/http";

  export default {
    name: 'Login',

    data() {
      return {
        identity: undefined,
        password: undefined,

        isActive: false,
        errorHidden: true,
        errorMessage: "Something went wrong"
      }
    },

    methods: {
      doLogin() {
        this.errorHidden = true;
        this.isActive = true;

        return HTTP.post("/login", {
          identity: this.identity,
          password: this.password
        }).then(res => res.data)
          .then(session => {
            AppState.sessionId = session.sessionId;
            AppState.isAdmin = session.admin;

            return HTTP.get("/users/me")
              .then(res => res.data)
              .then(user => {
                AppState.user = user;
              })
          })
          .then(() => {
            this.$router.replace("/");
          })
          .catch(err => {
            this.errorHidden = false;
            this.errorMessage = err.response.data.message || "Something went wrong";
          })
          .finally(() => {
            this.isActive = false;
          })
      }
    }
  }
</script>

<style scoped>
  body {
    background-color: #DADADA;
  }

  .column {
    max-width: 450px;
  }
</style>
