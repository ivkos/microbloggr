<template>
  <div class="ui two column grid">
    <div class="eleven centered column">
      <router-view/>
    </div>

    <div class="five wide column" v-if="getUser.isResolved">
      <div class="ui fluid card">
        <img class="ui fluid image" :src="userPicture"/>

        <div class="content">
          <div class="header">{{ user.name || user.vanity }}</div>

          <div class="meta">
            <router-link :to="`/${user.vanity}`">
              @{{ user.vanity }}
            </router-link>
          </div>
        </div>

        <div class="extra content">
          <div class="ui three column grid">
            <div class="text-centered row">
              <router-link :to="`/${user.vanity}`" class="column">
                <strong>{{ user.postsCount || 0 }}</strong>
                <br>post{{ (user.postsCount || 0) === 1 ? '' : 's' }}
              </router-link>

              <router-link :to="`/${user.vanity}/followers`" class="column">
                <strong>{{ user.followersCount || 0 }}</strong>
                <br>follower{{ (user.followersCount || 0) === 1 ? '' : 's' }}
              </router-link>

              <router-link :to="`/${user.vanity}/following`" class="column">
                <strong>{{ user.followeesCount || 0 }}</strong>
                <br>following
              </router-link>
            </div>
          </div>
        </div>

        <template v-if="!isCurrentUser">
          <template v-if="user.followed">
            <div class="ui animated vertical bottom attached basic primary button"
                 @click="toggleFollow">
              <div class="visible content">
                <i class="icon checkmark"/>
                Following
              </div>

              <div class="hidden content">
                <i class="icon remove user"/>
                Unfollow
              </div>
            </div>
          </template>
          <template v-else>
            <div class="ui bottom attached primary button" @click="toggleFollow">
              <i class="icon add user"/>
              Follow
            </div>
          </template>
        </template>

        <div class="ui inverted dimmer" :class="{ active: getUser.isPending }">
          <div class="ui loader"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import HTTP from "../support/http";

  export default {
    name: 'UserProfile',
    props: ['vanity'],

    data() {
      return {
        user: {},
        isCurrentUser: true
      }
    },

    computed: {
      userPicture() {
        if (this.user.pictureId) {
          return `${HTTP.defaults.baseURL}/pictures/${this.user.pictureId}`;
        }

        return `https://www.gravatar.com/avatar/${this.user.emailHash}?s=640&d=retro`
      }
    },

    asyncMethods: {
      getUser() {
        return HTTP.get(`/users/${this.vanity}`)
          .then(res => res.data)
          .then(user => {
            this.user = user;
            this.isCurrentUser = AppState.user.id === user.id;
          })
      }
    },

    methods: {
      toggleFollow() {
        const verb = this.user.followed ? 'delete' : 'put';

        this.user.followersCount = this.user.followersCount + (this.user.followed ? -1 : 1);
        this.user.followed = !this.user.followed;

        return HTTP[verb](`/users/${this.user.id}/followers`);
      }
    },

    created() {
      this.getUser.execute();
    },

    watch: {
      vanity() {
        this.getUser.execute();
      }
    }
  }
</script>

<style>
  .text-centered {
    text-align: center;
  }
</style>
