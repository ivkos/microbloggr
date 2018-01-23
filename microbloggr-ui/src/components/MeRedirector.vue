<template />

<script>
  import { HTTP } from "../support/http-common";

  export default {
    name: 'MeRedirector',
    async created() {
      var res = await HTTP.get("/users/me");

      this.$router.push('/' + res.data.vanity);
    }
  }
</script>
