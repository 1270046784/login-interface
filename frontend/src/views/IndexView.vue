<script setup>
import { ElMessage } from "element-plus";
import router from "@/router/index.js";
import { get } from "@/net/index.js";
import { useStore } from "@/stores/index.js";

const store = useStore()

const logout = () => {
    get('/api/auth/logout', message => {
        ElMessage.success(message)
        localStorage.removeItem('auth')
        store.auth.user = null
        router.push('/')
    })
}
</script>

<template>
    <div class="top-bar">
        <div class="art-text">Unnamed Markdown Editor</div>
        <div class="user-info">
            欢迎您，
            <span>{{ store.auth && store.auth.user ? store.auth.user.username : 'null' }}</span>
            先生/女士
            <el-button @click="logout" type="danger" plain size="small">登出</el-button>
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
