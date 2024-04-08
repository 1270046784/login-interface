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

// const dialog = (visible, text) => {
//     return reactive({
//         visible: visible,
//         text: text
//     })
// }

const createFileDialog = reactive({
    visible: false,
    text: ''
})
const changeTitleDialog = reactive({
    visible: false,
    text: ''
})
const removeFileDialog = reactive({
    visible: false
})

const getCurFile = index => {
    curFileIndex.value = index
}

const handleSelect = index => {
    markdownContent.value = userDir[index - 1].text || ''
    renderedMarkdown.value = marked(markdownContent.value)
}

const showDialog = dialog => {
    dialog.visible = true
}

const showDialogAndChangeIndex = (dialog, index) => {
    showDialog(dialog)
    curFileIndex.value = index - 1
}

const shutdownDialog = dialog => {
    dialog.visible = false
    if (dialog.text) {
        dialog.text = ''
    }
}

const updateLocalStorage = () => {
    store.auth.user.userDir = userDir
    localStorage.setItem('auth', JSON.stringify(store.auth))
}

const confirmCreateFile = () => {
    let newTitle = createFileDialog.text
    if (newTitle.trim().length === 0) {
        ElMessageBox.alert('标题不能为空', '提示')
        return
    }
    if (userDir.some(file => file.title === newTitle)) {
        ElMessageBox.alert('标题已存在，请使用一个不同的标题', '错误')
        return
    }
    userDir.push({
        title: newTitle,
        text: ''
    })
    post('/api/user/create-file', {
            username: username,
            title: newTitle
        }, message => {
            ElMessage.success(message)
        }
    )
    updateLocalStorage()
    shutdownDialog(createFileDialog)
}

const confirmChangeTitle = () => {
    let oldTitle = userDir[curFileIndex.value].title
    let newTitle = changeTitleDialog.text
    if (newTitle.trim().length === 0) {
        ElMessageBox.alert('标题不能为空', '提示')
        return
    }
    if (userDir.some(file => file.title === newTitle)) {
        ElMessageBox.alert('标题已存在，请使用一个不同的标题', '错误')
        return
    }
    if (oldTitle === newTitle) {
        ElMessageBox.alert('请不要使用原来的标题', '错误')
        return
    }
    userDir[curFileIndex.value].title = changeTitleDialog.text
    post('/api/user/change-title', {
            username: username,
            oldTitle: oldTitle,
            newTitle: newTitle
        }, message => {
            ElMessage.success(message)
        }
    )
    updateLocalStorage()
    shutdownDialog(changeTitleDialog)
}

const confirmRemoveFile = () => {
    let title = userDir[curFileIndex.value].title
    userDir.splice(curFileIndex.value, 1)
    if (userDir.length > 0) {
        curFileIndex.value = Math.min(curFileIndex.value, userDir.length - 1);
    } else {
        curFileIndex.value = null;
    }
    post('/api/user/remove-file', {
            username: username,
            title: title
        }, message => {
            ElMessage.success(message)
        }
    )
    updateLocalStorage()
    shutdownDialog(removeFileDialog)
}

// 监听编辑区变化，实时更新预览区并自动保存
watch(markdownContent, newValue => {
    let title = userDir[curFileIndex.value].title
    let text = userDir[curFileIndex.value].text
    if (text.value !== newValue) {
        post('/api/user/save-file', {
            username: username,
            title: title,
            text: text
        }, () => {

        })
        userDir[curFileIndex.value].text = newValue
        updateLocalStorage()
        renderedMarkdown.value = marked(newValue)
    }
})

// 初始化渲染第一个文件内容
markdownContent.value = userDir[curFileIndex.value].text
renderedMarkdown.value = marked(markdownContent.value)
</script>

<template>
    <el-container style="height: 100%; margin-top: 5px">
        <!-- 左侧文件列表 -->
        <el-aside width="240px" class="file-list">
            <div style="display: flex; justify-content: center; margin: 10px 20px">
                <el-button @click="showDialog(createFileDialog)" type="success" style="width: 100%">新建</el-button>
            </div>
            <el-menu style="margin-top: 10px" default-active="1" class="el-menu-vertical-demo" @select="handleSelect">
                <el-menu-item
                    v-for="index in userDir.length"
                    :index="index.toString()"
                    :key="index"
                    @click="getCurFile(index)"
                    :class="{'background-lightgreen': curFileIndex === index}">
                    <el-dropdown>
                        <span class="el-dropdown-link">
                            {{ userDir[index - 1].title }}<el-icon class="el-icon-arrow-down el-icon--right"></el-icon>
                        </span>

                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item @click="showDialogAndChangeIndex(changeTitleDialog, index)">重命名</el-dropdown-item>
                                <el-dropdown-item @click="showDialogAndChangeIndex(removeFileDialog, index)">删除</el-dropdown-item>
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

        <el-dialog v-model="createFileDialog.visible" :show-close="false" style="width: 30%">
            <el-input v-model="createFileDialog.text" placeholder="请输入文件标题"></el-input>
            <div style="margin-top: 20px; text-align: center">
                <span slot="footer" class="dialog-footer">
                    <el-button type="danger" @click="shutdownDialog(createFileDialog)">取消</el-button>
                    <el-button type="success" @click="confirmCreateFile">确认</el-button>
                </span>
            </div>
        </el-dialog>

        <el-dialog v-model="changeTitleDialog.visible" :show-close="false" style="width: 30%">
            <el-input v-model="changeTitleDialog.text" placeholder="请输入文件标题"></el-input>
            <div style="margin-top: 20px; text-align: center">
                <span slot="footer" class="dialog-footer">
                    <el-button type="danger" @click="shutdownDialog(changeTitleDialog)">取消</el-button>
                    <el-button type="success" @click="confirmChangeTitle">确认</el-button>
                </span>
            </div>
        </el-dialog>

        <el-dialog v-model="removeFileDialog.visible" :show-close="false" style="width: 30%">
            <div style="text-align: center; font-size: 15px">确定要删除吗</div>
            <div style="margin-top: 20px; text-align: center">
                <span slot="footer" class="dialog-footer">
                    <el-button type="danger" @click="shutdownDialog(removeFileDialog)">取消</el-button>
                    <el-button type="success" @click="confirmRemoveFile">确认</el-button>
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
