<template>
  <div>
    <template v-if="getPost.isResolved">
      <post-card :post="post"/>
    </template>

    <div class="ui inverted dimmer" :class="{ active: getPost.isPending }">
      <div class="ui loader"></div>
    </div>
  </div>
</template>

<script>
  import HTTP from "../support/http"
  import PostCard from "./PostCard"

  export default {
    components: { PostCard },
    name: 'UserProfileSinglePost',
    props: ['postId'],
    data() {
      return {
        post: {}
      }
    },
    asyncMethods: {
      getPost() {
        return HTTP.get(`/posts/${this.postId}`)
          .then(res => res.data)
          .then(post => {
            this.post = post;
            return post;
          }).catch(err => {
            if (err.response.status >= 400) {
              this.$router.replace({ name: 'NotFound' })
            }

            throw err;
          });
      }
    },
    created() {
      this.getPost.execute();
    }
  }
</script>
