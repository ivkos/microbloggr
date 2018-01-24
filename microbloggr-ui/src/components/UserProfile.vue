<template>
  <div class="ui two column grid">
    <catch-async-error :method="getUser">
      <div v-if="getUser.rejectedWith">
        Could not load user due to an error. Details: {{getUser.rejectedWith.message}}
      </div>
    </catch-async-error>

    <div class="ui eleven centered column">
      <template v-if="!singlePost">
        <h2>Posts</h2>

        <template v-if="getPosts.resolvedWithEmpty">
          <div class="ui stacked segment">This user hasn't posted anything.</div>
        </template>

        <template v-if="getPosts.resolvedWithSomething">
          <div class="ui cards">
            <post-card v-for="p in posts" :key="p.id" :post="p"/>
          </div>
        </template>
      </template>

      <template v-if="singlePost">
        <post-card :post="post"/>
      </template>

      <div class="ui inverted dimmer" v-bind:class="{ active: getPosts.isPending || getSinglePost.isPending }">
        <div class="ui loader"></div>
      </div>
    </div>

    <sui-grid-column :width="5" v-if="getUser.isResolved">
      <sui-card class="fluid">
        <sui-image wrapped :src="userPicture"/>

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
  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import { HTTP } from "../support/http-common";
  import PostCard from "./PostCard";

  export default {
    components: { PostCard },
    name: 'UserProfile',
    props: ['vanity', 'postId'],

    data() {
      return {
        singlePost: false,
        actualPromise: undefined,
        actualAction: undefined,
        isLoading: true,

        user: undefined,
        isCurrentUser: undefined,

        followers: [],
        followersCount: 0,
        isFollowed: undefined,

        posts: [],
        post: undefined
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
            return posts;
          });
      },

      getSinglePost() {
        return HTTP.get(`/posts/${this.postId}`)
          .then(res => res.data)
          .then(post => {
            this.post = post;
            return post;
          })
      }
    },

    methods: {
      toggleFollow() {
        const verb = this.isFollowed ? 'delete' : 'put';

        this.followersCount = this.followersCount + (this.isFollowed ? -1 : 1);
        this.isFollowed = !this.isFollowed;

        return HTTP[verb](`/users/${this.user.id}/followers`);
      },

      initialize() {
        this.getUser.execute();

        this.singlePost = this.postId !== undefined;
        this.actualPromise = this.singlePost ? this.getSinglePost.execute() : this.getPosts.execute();

        Promise.all([
          this.getFollowers.execute(),
          this.actualPromise
        ]).finally(() => {
          this.isLoading = false;
        });
      }
    },

    created() {
      this.initialize();
    },

    watch: {
      postId(val) {
        this.initialize();
      }
    }
  }
</script>

<style></style>
