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

    <div class="five wide column" v-if="getUser.isResolved">
      <div class="ui fluid card">
        <img class="ui wrapped image" :src="userPicture"/>

        <div class="content">
          <div class="header">{{ user.name || user.vanity }}</div>

          <div class="meta">
            <router-link :to="{ name: 'UserProfile', params: { vanity: user.vanity }}">
              @{{ user.vanity }}
            </router-link>
          </div>
        </div>

        <div class="extra content">
          <div class="ui three column grid">
            <div class="text-centered row">
              <div class="column">
                <strong>{{ posts.length }}</strong>
                <br>post{{ posts.length === 1 ? '' : 's' }}
              </div>

              <div class="column">
                <strong>{{ followersCount }}</strong>
                <br>follower{{ followersCount === 1 ? '' : 's' }}
              </div>

              <div class="column">
                <strong>{{ followeesCount || 0 }}</strong>
                <br>following
              </div>
            </div>
          </div>
        </div>

        <template v-if="!isCurrentUser">
          <template v-if="isFollowed">
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

        <div class="ui inverted dimmer" v-bind:class="{ active: isLoading }">
          <div class="ui loader"></div>
        </div>
      </div>
    </div>
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
        followees: [],
        followersCount: 0,
        followeesCount: 0,
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
        return Promise.all([
          HTTP.get(`/users/${this.vanity}/followers`)
            .then(res => res.data)
            .then(followers => {
              this.followers = followers;
              this.followersCount = followers.length;
              this.isFollowed = followers.some(f => f.id === AppState.user.id);
            }),
          HTTP.get(`/users/${this.vanity}/followees`)
            .then(res => res.data)
            .then(followees => {
              this.followees = followees;
              this.followeesCount = followees.length;
            })
        ]);
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

<style>
  .text-centered {
    text-align: center;
  }
</style>
