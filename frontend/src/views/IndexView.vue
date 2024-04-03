<script setup>
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {get} from "@/net/index.js";

const logout = () => {
    get('/api/auth/logout', (message) => {
        ElMessage.success(message)
        router.push('/')
    })
}
</script>

<template>
    <div>
        欢迎使用本系统
    </div>
    <div>
        <el-button @click="logout" type="danger" plain>登出</el-button>
    </div>
</template>

<style scoped>

</style>