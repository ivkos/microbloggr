<template>
  <sui-grid>
    <catch-async-error :method="getUser">
      <div v-if="getUser.rejectedWith">
        Could not load user due to an error. Details: {{getUser.rejectedWith.message}}
      </div>
    </catch-async-error>

    <sui-grid-row>
      <sui-grid-column :width="11">
        <h2>Posts</h2>
        <template v-if="getPosts.resolvedWithEmpty">
          <div class="ui stacked segment">This user hasn't posted anything.</div>
        </template>

        <template v-if="getPosts.isResolved">
        </template>

        <div class="ui inverted dimmer" v-bind:class="{ active: getPosts.isPending }">
          <div class="ui loader"></div>
        </div>
      </sui-grid-column>

      <sui-grid-column :width="5" v-if="getUser.isResolved">
        <sui-card class="fluid">
          <sui-image wrapped :src="'https://www.gravatar.com/avatar/' + user.emailHash + '?s=640&d=retro'"/>

          <sui-card-content>
            <sui-card-header>{{ user.name || user.vanity }}</sui-card-header>

            <sui-card-meta>
              <router-link :to="{ name: 'UserProfile', params: { vanity: user.vanity }}">@{{ user.vanity }}
              </router-link>
            </sui-card-meta>
          </sui-card-content>

          <sui-card-content extra>
            <span>
              <sui-icon name="comment outline"/>{{ posts.length }} post{{ posts.length === 1 ? '' : 's' }}
            </span>

            <span slot="right">
              <sui-icon name="user outline"/>{{ followersCount }} follower{{ followersCount === 1 ? '' : 's' }}
            </span>
          </sui-card-content>

          <template v-if="!isCurrentUser">
            <template v-if="isFollowed">
              <div class="ui animated vertical bottom attached basic primary button"
                   @click="toggleFollow">
                <div class="visible content">
                  <sui-icon name="checkmark"/>
                  Following
                </div>

                <div class="hidden content">
                  <sui-icon name="remove user"/>
                  Unfollow
                </div>
              </div>
            </template>
            <template v-else>
              <div class="ui bottom attached primary button" @click="toggleFollow">
                <sui-icon name="add user"/>
                Follow
              </div>
            </template>
          </template>

          <div class="ui inverted dimmer" v-bind:class="{ active: isLoading }">
            <div class="ui loader"></div>
          </div>
        </sui-card>
      </sui-grid-column>
    </sui-grid-row>
  </sui-grid>
</template>

<script>
  import AppState from "../support/AppState";
  import { HTTP } from "../support/http-common";

  export default {
    name: 'UserProfile',
    props: ['vanity'],

    data() {
      return {
        isLoading: true,

        user: undefined,
        isCurrentUser: undefined,

        followers: [],
        followersCount: 0,
        isFollowed: undefined,

        posts: []
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
      },

      getFollowers() {
        return HTTP.get(`/users/${this.vanity}/followers`)
          .then(res => res.data)
          .then(followers => {
            this.followers = followers;
            this.followersCount = followers.length;
            this.isFollowed = followers.some(f => f.id === AppState.user.id);
          });
      },

      getPosts() {
        return HTTP.get(`/users/${this.vanity}/posts`)
          .then(res => res.data)
          .then(posts => {
            this.posts = posts;
          });
      }
    },

    methods: {
      toggleFollow() {
        const verb = this.isFollowed ? 'delete' : 'put';

        this.followersCount = this.followersCount + (this.isFollowed ? -1 : 1);
        this.isFollowed = !this.isFollowed;

        return HTTP[verb](`/users/${this.user.id}/followers`);
      }
    },

    created() {
      this.getUser.execute();

      Promise.all([
        this.getFollowers.execute(),
        this.getPosts.execute()
      ]).finally(() => {
        this.isLoading = false;
      });
    }
  }
</script>

<style></style>
