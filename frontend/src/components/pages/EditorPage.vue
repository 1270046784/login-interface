<script setup>
import { ref, watch, reactive } from 'vue'
import {ElMenu, ElMenuItem, ElInput, ElAside, ElMain, ElContainer, ElMessageBox, ElMessage} from 'element-plus'
import { marked } from 'marked'
import {useStore} from "@/stores/index.js";
import {post} from "@/net/index.js";

const markdownContent = ref('')
const renderedMarkdown = ref('')
const store = useStore()
store.auth = JSON.parse(localStorage.getItem('auth'))
const username = store.auth.user.username
const userDir = store.auth.user.userDir
const curFileIndex = ref(0)

const dialogVisible = ref(false)
const dialogData = reactive({
    title: ''
})

const getCurFile = index => {
    curFileIndex.value = index
}

const handleSelect = index => {
    markdownContent.value = userDir[index - 1].text || ''
    renderedMarkdown.value = marked(markdownContent.value)
}

const createFile = () => {
    dialogVisible.value = true;
}

const cancelNewFile = () => {
    dialogData.title = '';
    dialogVisible.value = false;
}

const confirmNewFile = () => {
    if (dialogData.title.trim().length === 0) {
        ElMessageBox.alert('标题不能为空', '提示');
        return;
    }
    if (userDir.some(file => file.title === dialogData.title)) {
        ElMessageBox.alert('标题已存在，请使用一个不同的标题', '错误');
        return;
    }
    userDir.push({ title: dialogData.title, text: '' })
    post('/api/user/create-file', {
            username: username,
            title: dialogData.title
        }, message => {
            ElMessage.success(message)
        }
    )
    store.auth.user.userDir = userDir
    localStorage.setItem('auth', JSON.stringify(store.auth))
    dialogData.title = '';
    dialogVisible.value = false;

}



// 监听编辑区变化，实时更新预览区
watch(markdownContent, newValue => {
    renderedMarkdown.value = marked(newValue)
})

// 初始化渲染第一个文件内容
markdownContent.value = userDir[0].text
renderedMarkdown.value = marked(markdownContent.value)
</script>

<template>
    <el-container style="height: 100%; margin-top: 5px">
        <!-- 左侧文件列表 -->
        <el-aside width="240px" class="file-list">
            <div style="display: flex; justify-content: center; margin: 10px 10px">
                <el-button @click="createFile" type="success" style="width: 100%">新建</el-button>
            </div>
            <el-menu style="margin-top: 10px" default-active="1" class="el-menu-vertical-demo" @select="handleSelect">
                <el-menu-item
                    v-for="index in userDir.length"
                    :index="index.toString()"
                    :key="index"
                    @click="getCurFile"
                    :class="{'background-lightgreen': curFileIndex === index}">
                    <el-dropdown>
                        <span class="el-dropdown-link">
                            {{ userDir[index - 1].title }}<el-icon class="el-icon-arrow-down el-icon--right"></el-icon>
                        </span>

                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item>重命名</el-dropdown-item>
                                <el-dropdown-item>删除</el-dropdown-item>
                                <el-dropdown-item>保存</el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </el-menu-item>
            </el-menu>
        </el-aside>

        <!-- 中间输入区 -->
        <el-main class="content-editor">
            <el-input
                type="textarea"
                :rows="43"
                placeholder="请输入内容"
                v-model="markdownContent"
                style="width: 100%; height: 100%; resize: none;"
            ></el-input>
        </el-main>

        <!-- 右侧Markdown预览 -->
        <el-aside width="40%" class="markdown-preview">
            <div v-html="renderedMarkdown"></div>
        </el-aside>

        <el-dialog v-model="dialogVisible" :show-close="false" style="width: 30%">
            <el-input v-model="dialogData.title" placeholder="请输入文件标题"></el-input>
            <div style="margin-top: 20px">
                <span slot="footer" class="dialog-footer">
                    <el-button type="danger" @click="cancelNewFile">取消</el-button>
                    <el-button type="success" @click="confirmNewFile">确认</el-button>
                </span>
            </div>
        </el-dialog>
    </el-container>
</template>

<style scoped>
.file-list, .markdown-preview {
    border: 1px solid #eaecef;
    box-shadow: 0 2px 4px rgba(0,0,0,.1);
}

.file-list {
    overflow-y: auto;
}

.markdown-preview {
    background-color: #fff;
    padding: 20px;
    overflow-y: auto;
}

.content-editor {
    padding: 10px;
}

.background-lightgreen {
    background-color: lightgreen;
}
</style>
