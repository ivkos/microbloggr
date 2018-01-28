<template/>

<script>
  import HTTP from "../support/http";

  export default {
    name: 'MeRedirector',
    async created() {
      const res = await HTTP.get("/users/me");
      this.$router.push(`/${res.data.vanity}`);
    }
  }
</script>
