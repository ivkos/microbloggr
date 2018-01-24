<template>
  <div class="ui raised fluid card" v-if="!deleted">
    <div class="content">
      <div class="right floated meta">
        <router-link :to="`/${post.author.vanity}/posts/${post.id}`">{{ post.createdAt | moment("from") }}</router-link>
      </div>

      <router-link :to="`/${post.author.vanity}`">
        <img class="ui avatar image" :src="`https://www.gravatar.com/avatar/${post.author.emailHash}?s=64&d=retro`"/>
      </router-link>

      <router-link :to="`/${post.author.vanity}`">
        {{ post.author.name || post.author.vanity }}
      </router-link>
    </div>

    <div class="content" v-if="post.content">
      <p class="post-text">{{ post.content }}</p>
    </div>

    <div class="image" v-if="post.pictureId">
      <img class="ui image wrapped" :src="getPictureUrl(post.pictureId)"/>
    </div>

    <div class="content">
      <span class="left floated">
        <i class="heart like icon" :class="{outline: !post.liked}" @click="toggleLike"></i>
        <template v-if="post.likes === 0">
          Be the first one to like this.
        </template>

        <template v-else>
          <template v-if="post.liked">
            Liked by you <span v-if="post.likes >= 2">and {{ post.likes - 1 }} other {{ post.likes > 2 ? 'people' : 'person' }}</span>
          </template>

          <template v-else>
            {{ post.likes }} like{{ post.likes === 1 ? '' : 's' }}
          </template>
        </template>
      </span>

      <span class="right floated" v-show="deletable">
        <i class="delete like icon" @click="deletePost"/>
      </span>
    </div>
  </div>
</template>

<script>
  import AppState from "../support/AppState";
  import { HTTP } from "../support/http-common";

  export default {
    name: 'post-card',
    props: ['post'],

    data() {
      return {
        deleted: false,

        deletable() {
          return AppState.isAdmin || this.post.author.id === AppState.user.id
        }
      }
    },

    methods: {
      toggleLike() {
        const verb = this.post.liked ? 'delete' : 'put';

        this.post.likes = this.post.likes + (this.post.liked ? -1 : 1);
        this.post.liked = !this.post.liked;

        return HTTP[verb](`/posts/${this.post.id}/likes`);
      },

      deletePost() {
        this.deleted = true;
        return HTTP.delete(`/posts/${this.post.id}`);
      },

      getPictureUrl(id) {
        return `${HTTP.defaults.baseURL}/pictures/${id}`;
      }
    }
  }
</script>

<style scoped>
  .post-text {
    font-size: 16pt;
  }
</style>