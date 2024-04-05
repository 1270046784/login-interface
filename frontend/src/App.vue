<script setup>
import {get} from "@/net/index.js";
import {useStore} from "@/stores/index.js";
import router from "@/router/index.js";

const store = useStore()

if (store.auth.user == null) {
    get('/api/user/info', message => {
        store.auth.user = message
        router.push('/index')
    }, () => {
        store.auth.user = null
    })
}

</script>

<template>
    <router-view :key="$route.fullPath"/>
</template>

<style scoped>

</style>
