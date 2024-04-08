<script setup>
import {useStore} from "@/stores/index.js";

const store = useStore()

const authStore = localStorage.getItem('auth')
if (authStore) {
    store.auth = JSON.parse(authStore)
}

</script>

<template>
    <router-view/>
<!--    <router-view :key="$route.fullPath"/>-->
</template>

<style scoped>

</style>
