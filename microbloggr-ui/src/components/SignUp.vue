<template>
  <sui-grid class="middle aligned center aligned">
    <div class="column">
      <sui-form class="large">
        <sui-segment piled>
          <sui-form-field>
            <sui-input name="name" placeholder="Your name" icon="id card outline" icon-position="left"/>
          </sui-form-field>

          <sui-form-field>
            <sui-input name="vanity" placeholder="Desired username" icon="at" icon-position="left"/>
          </sui-form-field>

          <sui-form-field>
            <sui-input name="email" placeholder="Email" icon="mail outline" icon-position="left"/>
          </sui-form-field>

          <sui-form-field>
            <sui-input type="password" name="password" placeholder="Password" icon="lock" icon-position="left"/>
          </sui-form-field>

          <sui-button fluid large primary>Sign Up</sui-button>
        </sui-segment>
      </sui-form>

      <sui-message class="tiny bottom attached">
        Already have an account?
        <router-link to="login">Login</router-link>
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
