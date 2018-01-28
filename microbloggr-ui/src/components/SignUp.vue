<template>
  <div class="ui grid middle aligned center aligned">
    <div class="column">
      <form class="ui form large" @submit.prevent="doSignUp">
        <div class="ui piled segment">
          <div class="field">
            <div class="ui left icon input">
              <input v-model="form.name" name="name" placeholder="Your name"/>
              <i class="id card outline icon"></i>
            </div>
          </div>

          <div class="field" :class="{ error: vanityUnavailable || errors.has('username') }">
            <div class="ui left icon input" :class="{ loading: vanityLoading }">
              <input v-model="form.vanity" name="username" placeholder="Desired username"
                     autocomplete="off"
                     v-validate="{ required: true, regex: /^([A-Za-z0-9_](?:(?:[A-Za-z0-9_]|(?:\.(?!\.))){0,28}(?:[A-Za-z0-9_]))?)$/ }"/>
              <i class="at icon"></i>
            </div>
            <div class="ui mini bottom attached negative message" v-show="vanityUnavailable || errors.has('username')">
              {{ errors.first('username') || "This username is taken." }}
            </div>
          </div>

          <div class="field" :class="{ error: emailUnavailable || errors.has('email')}">
            <div class="ui left icon input" :class="{ loading: emailLoading }">
              <input v-model="form.email" name="email" placeholder="Email" v-validate="'required|email'"/>
              <i class="mail outline icon"></i>
            </div>
            <div class="ui mini bottom attached negative message" v-show="emailUnavailable || errors.has('email')">
              {{ errors.first('email') || "This email is already registered." }}
            </div>
          </div>


          <div class="field" :class="{ error: errors.has('password') }">
            <div class="ui left icon input">
              <input v-model="form.password" type="password" name="password" placeholder="Password"
                     v-validate="'required'"/>
              <i class="lock icon"></i>
            </div>
            <div class="ui mini bottom attached negative message" v-show="errors.has('password')">
              {{ errors.first('password') }}
            </div>
          </div>

          <button type="submit" class="ui fluid large primary button" :disabled="hasErrors">Sign Up</button>

          <div class="ui message large negative" :hidden="errorHidden">
            <i class="large warning sign icon"></i>
            {{ errorMessage }}
          </div>
        </div>
      </form>

      <div class="ui tiny bottom attached message">
        Already have an account?
        <router-link to="login">Login</router-link>
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
        form: {},

        vanityLoading: false,
        vanityUnavailable: false,

        emailLoading: false,
        emailUnavailable: false,

        isActive: false,
        errorHidden: true,
        errorMessage: "Something went wrong",

        hasErrors: false
      }
    },

    methods: {
      doSignUp() {
        this.errorHidden = true;
        this.isActive = true;

        this.$validator.validateAll()
          .then(result => {
            if (!result) {
              return;
            }

            return HTTP.post('/register', this.form)
              .then(res => res.data)
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
                this.$router.replace("/me");
              })
              .catch(err => {
                this.errorHidden = false;
                this.errorMessage = err.response.data.message || "Something went wrong";
              })
              .finally(() => {
                this.isActive = false;
              })
          })
          .finally(() => {
            this.isActive = false;
          });
      }
    },

    watch: {
      ['form.email'](val) {
        this.emailLoading = true;
        HTTP.post("/check_email", { email: val })
          .then(res => res.data)
          .then(data => {
            this.emailUnavailable = !data.available;
          })
          .catch(err => { console.error(err) })
          .finally(() => {
            this.emailLoading = false;
          });
      },

      ['form.vanity'](val) {
        this.vanityLoading = true;
        HTTP.post("/check_vanity", { vanity: val })
          .then(res => res.data)
          .then(data => {
            this.vanityUnavailable = !data.available;
          })
          .catch(err => { console.error(err) })
          .finally(() => {
            this.vanityLoading = false;
          });
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
