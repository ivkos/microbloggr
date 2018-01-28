<template>
  <div class="ui two column grid">
    <div class="eleven wide column">
      <h1>Settings</h1>

      <form class="ui form large" @submit.prevent="saveSettings">
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
              <input v-model="form.email" name="email" placeholder="Email" v-validate="'email'"/>
              <i class="mail outline icon"></i>
            </div>
            <div class="ui mini bottom attached negative message" v-show="emailUnavailable || errors.has('email')">
              {{ errors.first('email') || "This email is already registered." }}
            </div>
          </div>


          <div class="field" :class="{ error: errors.has('password') }">
            <div class="ui left icon input">
              <input v-model="form.password" type="password" name="password" placeholder="Password"/>
              <i class="lock icon"></i>
            </div>
            <div class="ui mini bottom attached negative message" v-show="errors.has('password')">
              {{ errors.first('password') }}
            </div>
          </div>

          <label for="file" class="ui left floated icon button"
                 :class="{loading: isUploading, positive:form.pictureId, primary:isUploading}">
            <i class="image icon" :class="{image: !form.pictureId, checkmark: form.pictureId}"/>
          </label>
          <input type="file" name="file" id="file" accept="image/*" style="display:none" :disabled="isUploading"
                 @change="fileChanged">
          <br><br><br>

          <button type="submit" class="ui fluid large primary button" :disabled="hasErrors">Save</button>

          <div class="ui message large negative" :hidden="errorHidden">
            <i class="large warning sign icon"></i>
            {{ errorMessage }}
          </div>
        </div>
      </form>

      <div class="ui inverted dimmer" v-bind:class="{ active: isActive }">
        <div class="ui loader"></div>
      </div>
    </div>

    <div class="five wide column">
      <div class="ui fluid card">
        <img class="ui fluid image" :src="userPicture"/>

        <div class="content">
          <div class="header">{{ form.name || form.vanity }}</div>

          <div class="meta">
            <router-link :to="`/${user.vanity}`">@{{ form.vanity }}</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import { HTTP } from "../support/http-common";

  export default {
    name: 'Settings',

    data() {
      return {
        form: {},

        isUploading: false,

        vanityLoading: false,
        vanityUnavailable: false,

        emailLoading: false,
        emailUnavailable: false,

        isActive: false,
        errorHidden: true,
        errorMessage: "Something went wrong",

        hasErrors: false,

        get user() {
          return AppState.user;
        }
      }
    },

    computed: {
      userPicture() {
        const pictureIdInUse = this.form.pictureId || this.user.pictureId;

        if (pictureIdInUse) {
          return `${HTTP.defaults.baseURL}/pictures/${pictureIdInUse}`;
        }

        return `https://www.gravatar.com/avatar/${this.user.emailHash}?s=640&d=retro`
      }
    },

    methods: {
      saveSettings() {
        this.errorHidden = true;
        this.isActive = true;

        this.$validator.validateAll()
          .then(result => {
            if (!result) {
              return;
            }

            return HTTP.patch('/users/me', this.form)
              .then(res => res.data)
              .then(user => {
                AppState.user = user;
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
      },

      fileChanged(e) {
        let file = e.srcElement.files[0];
        if (!file) return;

        this.form.pictureId = undefined;
        this.isUploading = true;

        const data = new FormData();
        data.append('file', file);

        HTTP.post("/pictures", data)
          .then(res => res.data)
          .then(picture => {
            this.form.pictureId = picture.id;
          })
          .finally(() => {
            this.isUploading = false;
          });
      }
    },

    created() {
      this.form = {
        name: this.user.name,
        vanity: this.user.vanity,
        email: undefined,
        password: undefined,
        pictureId: undefined
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
        if (val === AppState.user.vanity) {
          this.vanityUnavailable = false;
          return;
        }

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

<style></style>
