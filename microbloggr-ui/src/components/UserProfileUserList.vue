<template>
  <div>
    <h2>
      <span v-if="type === 'followers'">Followers</span>
      <span v-if="type === 'followees'">Following</span>
    </h2>

    <template v-if="getUserList.resolvedWithEmpty">
      <div class="ui stacked segment">
        This user is not
        <span v-if="type === 'followers'">followed by</span>
        <span v-if="type === 'followees'">following</span>
        anyone.
      </div>
    </template>

    <template v-if="getUserList.isResolved">
      <div class="ui selection link list">
        <router-link :to="`/${u.vanity}`" class="item" v-for="u in userList" :key="u.id">
          <img class="ui mini avatar image" :src="getUserPicture(u)"/>
          <div class="content">
            <a class="header">{{ u.name || u.vanity }}</a>
            <div class="description">@{{ u.vanity }}</div>
          </div>
        </router-link>
      </div>
    </template>

    <div class="ui inverted dimmer" :class="{ active: getUserList.isPending }">
      <div class="ui loader"></div>
    </div>
  </div>
</template>

<script>
  import HTTP from "../support/http"

  export default {
    name: 'UserProfileUserList',
    props: ['type', 'vanity'],

    data() {
      return {
        userList: []
      }
    },

    asyncMethods: {
      getUserList() {
        return HTTP.get(`/users/${this.vanity}/${this.type}`)
          .then(res => res.data)
          .then(userList => {
            this.userList = userList;
            return userList;
          });
      }
    },

    methods: {
      getUserPicture(user) {
        if (user.pictureId) {
          return `${HTTP.defaults.baseURL}/pictures/${user.pictureId}`;
        }

        return `https://www.gravatar.com/avatar/${user.emailHash}?s=640&d=retro`
      }
    },

    created() {
      this.getUserList.execute();
    },

    watch: {
      type() {
        this.getUserList.execute();
      }
    }
  }
</script>
