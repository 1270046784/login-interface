<script setup>
import { ref, watch, reactive } from 'vue'
import { ElMenu, ElMenuItem, ElInput, ElAside, ElMain, ElContainer } from 'element-plus'
import { marked } from 'marked'

const markdownContent = ref('')
const renderedMarkdown = ref('')

// 假设的文件内容，实际应用中应从服务器获取
const files = reactive({
    '1': '# 文件1\n这是文件1的内容。',
    '2': '# 文件2\n这是文件2的内容。'
})

const curFileIndex = ref(0)
const fileCount = ref(10)

const getCurFile = index => {
    curFileIndex.value = index
}

const handleSelect = index => {
    markdownContent.value = files[index] || ''
    renderedMarkdown.value = marked(markdownContent.value)
}

// 监听编辑区变化，实时更新预览区
watch(markdownContent, newValue => {
    renderedMarkdown.value = marked(newValue)
})

// 初始化渲染第一个文件内容
markdownContent.value = files['1']
renderedMarkdown.value = marked(markdownContent.value)
</script>

<template>
    <el-container style="height: 100%; margin-top: 5px">
        <!-- 左侧文件列表 -->
        <el-aside width="240px" class="file-list">
            <div style="display: flex; justify-content: center; margin: 10px 10px">
                <el-button type="success" style="width: 100%">新建</el-button>
            </div>
            <el-menu style="margin-top: 10px" default-active="1" class="el-menu-vertical-demo" @select="handleSelect">
                <el-menu-item
                    v-for="index in fileCount"
                    :index="index.toString()"
                    :key="index"
                    @click="getCurFile"
                    :class="{'background-lightgreen': curFileIndex === index}">
                    <el-dropdown>
                        <span class="el-dropdown-link">
                            文件{{ index }}<el-icon class="el-icon-arrow-down el-icon--right"></el-icon>
                        </span>

                        <template #dropdown>
                            <el-dropdown-menu>
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
