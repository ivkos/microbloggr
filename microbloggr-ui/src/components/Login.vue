<template>
  <sui-grid class="middle aligned center aligned">
    <div class="column">
      <sui-form class="large">
        <sui-segment piled>
          <sui-form-field>
            <sui-input name="identity" placeholder="Email address or username" icon="user" icon-position="left"/>
          </sui-form-field>

          <sui-form-field>
            <sui-input type="password" name="password" placeholder="Password" icon="lock" icon-position="left"/>
          </sui-form-field>

          <sui-button fluid large primary>Login</sui-button>
        </sui-segment>

      </sui-form>

      <sui-message class="tiny bottom attached">
        Don't have an account?
        <router-link to="signup">Sign Up</router-link>
      </sui-message>
    </div>
  </sui-grid>
</template>

<script>
  export default {
    name: 'Login'
  }
</script>

<style scoped>
  body {
    background-color: #DADADA;
  }

  .column {
    max-width: 450px;
  }
</style>
