<script setup>
import { ElMessage } from "element-plus";
import router from "@/router/index.js";
import { get } from "@/net/index.js";
import { useStore } from "@/stores/index.js";

const store = useStore()

const logout = () => {
    get('/api/auth/logout', message => {
        ElMessage.success(message)
        store.auth.user = null
        localStorage.removeItem('auth')
        router.push('/')
    })
}

const toTest = () => {
    router.push('/index/test')
}
</script>

<template>
    <div class="top-bar">
        <div class="art-text">Unnamed</div>
        <div class="user-info">
            欢迎您，<span>{{ store.auth.user.username }}</span>先生/女士
            <el-button @click="logout" type="danger" plain size="small">登出</el-button>
            <el-button @click="toTest" type="success" plain size="small">自爆按钮</el-button>
        </div>
    </div>
    <router-view/>
</template>

<style scoped>
.top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 20px;
    background-color: #f5f5f5;
}

.art-text {
    font-style: italic;
    font-weight: bold;
    font-size: 20px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 12px; /* 调整用户名和按钮之间的间距 */
    font-size: 16px;
}

.user-info span {
    margin-right: 8px; /* 用户名和按钮之间的间距 */
}
</style>
