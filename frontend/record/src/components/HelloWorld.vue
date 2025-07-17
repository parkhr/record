<script setup>
defineProps({
  msg: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <div class="greetings">
    <h1 class="green">{{ msg }}</h1>
    <h3>
      You’ve successfully created a project with
      <a href="https://vite.dev/" target="_blank" rel="noopener">Vite</a> +
      <a href="https://vuejs.org/" target="_blank" rel="noopener">Vue 3</a>.
    </h3>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
  data() {
    return {
      message: '',
    };
  },
  methods: {
    async login() {
      try {
        const requestBody = {
          name: 'admin',
          password: 'admin'
        };

        const response = await axios.post('http://localhost:8081/login', requestBody, {
          headers: {
            'Content-Type': 'application/json'
          }
        });

        sessionStorage.setItem('accessToken', response.data);
        
        console.log(response)
      } catch (error) {;
        console.error(error)
      }
    },

    async fetchRecords() {
      try {

        const response = await axios.get('http://localhost:8081/api/record/search', {
          headers: {
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          }
        });
        
        console.log(response)
      } catch (error) {;
        console.error(error)
      }
    }
  },
  mounted() {
    this.login(); // 컴포넌트가 마운트되면 자동으로 호출
    this.fetchRecords(); // 컴포넌트가 마운트되면 자동으로 호출
  }
};
</script>

<style scoped>
h1 {
  font-weight: 500;
  font-size: 2.6rem;
  position: relative;
  top: -10px;
}

h3 {
  font-size: 1.2rem;
}

.greetings h1,
.greetings h3 {
  text-align: center;
}

@media (min-width: 1024px) {
  .greetings h1,
  .greetings h3 {
    text-align: left;
  }
}
</style>
