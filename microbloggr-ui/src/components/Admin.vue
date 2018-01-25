<template>
  <div>
    <h1>Administration</h1>
    <h3>Users</h3>

    <table class="ui very basic celled table">
      <thead>
      <tr>
        <th>User</th>
        <th>Registered</th>
        <th>Actions</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="user in users" :key="user.id">
        <td>
          <h4 class="ui image header">
            <img :src="getUserPicture(user)" class="ui mini rounded image">
            <div class="content">
              <router-link :to="`/${user.vanity}`">
                {{ user.name || user.vanity }}
              </router-link>

              <router-link :to="`/${user.vanity}`">
                <div class="sub header">@{{ user.vanity }}</div>
              </router-link>
            </div>
          </h4>
        </td>
        <td>
          {{ user.createdAt | moment("from") }}
        </td>
        <td>
          <button class="ui negative basic button" @click="deleteUser(user, $event)" :disabled="isCurrentUser(user)">
            Delete
          </button>
        </td>
      </tr>
      </tbody>
    </table>

  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import { HTTP } from "../support/http-common"

  export default {
    name: 'Admin',

    data() {
      return {
        users: []
      }
    },

    asyncMethods: {
      getUsers() {
        return HTTP.get('/users')
          .then(res => res.data)
          .then(users => {
            this.users = users;
            return users;
          });
      }
    },

    methods: {
      getUserPicture(user) {
        if (user.pictureId) {
          return `${HTTP.defaults.baseURL}/pictures/${user.pictureId}`;
        }

        return `https://www.gravatar.com/avatar/${user.emailHash}?s=640&d=retro`
      },

      deleteUser(user, event) {
        event.srcElement.disabled = true;
        return HTTP.delete(`/users/${user.id}`);
      },

      isCurrentUser(user) {
        return user.id === AppState.user.id;
      }
    },

    created() {
      this.getUsers.execute();
    }
  }
</script>

<style></style>
