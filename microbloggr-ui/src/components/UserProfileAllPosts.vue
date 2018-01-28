<template>
  <div>
    <h2>Posts</h2>

    <template v-if="getPosts.resolvedWithEmpty">
      <div class="ui stacked segment">This user hasn't posted anything.</div>
    </template>

    <template v-if="getPosts.resolvedWithSomething">
      <div class="ui cards">
        <post-card v-for="p in posts" :key="p.id" :post="p"/>
      </div>
    </template>

    <div class="ui inverted dimmer" v-bind:class="{ active: getPosts.isPending }">
      <div class="ui loader"></div>
    </div>
  </div>
</template>

<script>
  import { HTTP } from "../support/http-common"
  import PostCard from "./PostCard"

  export default {
    components: { PostCard },
    name: 'UserProfileAllPosts',
    props: ['vanity'],
    data() {
      return {
        posts: []
      }
    },
    asyncMethods: {
      getPosts() {
        return HTTP.get(`/users/${this.vanity}/posts`)
          .then(res => res.data)
          .then(posts => {
            this.posts = posts;
            return posts;
          });
      }
    },
    created() {
      this.getPosts.execute();
    }
  }
</script>
