<template>
  <div class="ui two column grid">
    <div class="centered column">
      <div class="ui tall stacked segment">
        <h3>New Post</h3>
        <form class="ui form" enctype="multipart/form-data" @submit.prevent="createPost">
          <div class="field">
            <textarea rows="2" v-model="newPost.content" placeholder="What's on your mind?"></textarea>
          </div>

          <label for="file" class="ui left floated icon button"
                 :class="{loading: isUploading, positive:newPost.pictureId, primary:isUploading}">
            <i class="image icon" :class="{image: !newPost.pictureId, checkmark: newPost.pictureId}"/>
          </label>
          <input type="file" name="file" id="file" accept="image/*" style="display:none" :disabled="isUploading"
                 @change="fileChanged">

          <button class="ui right floated primary button" type="submit">Post</button>

          <div class="ui inverted dimmer" v-bind:class="{ active: isCreatingNewPost }">
            <div class="ui loader"></div>
          </div>

          <br><br>
        </form>
      </div>

      <br><br>

      <div class="ui cards">
        <post-card v-for="p in posts" :key="p.id" :post="p"/>
      </div>

      <div class="ui inverted dimmer" v-bind:class="{ active: getFeed.isPending }">
        <div class="ui loader"></div>
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
    name: 'Feed',

    data() {
      return {
        isUploading: false,
        isCreatingNewPost: false,
        posts: [],
        newPost: {
          content: undefined,
          pictureId: undefined
        }
      }
    },

    methods: {
      createPost() {
        if (!this.newPost.content && !this.newPost.pictureId) {
          return;
        }

        this.isCreatingNewPost = true;

        return HTTP.post("/posts", {
          content: this.newPost.content,
          pictureId: this.newPost.pictureId
        })
          .then(res => res.data)
          .then(post => {
            this.$router.push(`/${AppState.user.vanity}/posts/${post.id}`);
          })
          .finally(() => {
            this.isCreatingNewPost = false;
          })
      },

      fileChanged(e) {
        let file = e.srcElement.files[0];
        if (!file) return;

        this.newPost.pictureId = undefined;
        this.isUploading = true;

        const data = new FormData();
        data.append('file', file);

        HTTP.post("/pictures", data)
          .then(res => res.data)
          .then(picture => {
            this.newPost.pictureId = picture.id;
          })
          .finally(() => {
            this.isUploading = false;
          });
      }
    },

    asyncMethods: {
      getFeed() {
        return HTTP.get('/feed')
          .then(res => res.data)
          .then(posts => {
            this.posts = posts;
            return posts;
          });
      }
    },

    created() {
      this.getFeed.execute();
    }
  }
</script>

<style scoped>

</style>
