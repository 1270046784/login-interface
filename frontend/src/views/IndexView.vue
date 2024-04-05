<script setup>
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {get} from "@/net/index.js";
import {useStore} from "@/stores/index.js";

const store = useStore()

const logout = () => {
    get('/api/auth/logout', message => {
        ElMessage.success(message)
        store.auth.user = null
        router.push('/')
    })
}
</script>

<template>
    <div>
        欢迎使用本系统，{{store.auth.user.username}}先生/女士
    </div>
    <div>
        <el-button @click="logout" type="danger" plain>登出</el-button>
    </div>
</template>

<style scoped>

</style>