<script setup>
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";

const form = reactive({
    username: '',
    password: '',
    confirm_password: '',
    email: '',
    verify_code: ''
})

/**
 * 验证用户名是否合法
 * @param rule
 * @param value
 * @param callback
 */
const validateUsername = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('用户名不能为空'))
    } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
        callback(new Error('用户名只能包含中文或英文'))
    } else {
        callback()
    }
}

/**
 * 确认密码的规则
 * @param rule
 * @param value
 * @param callback
 */
const validateConfirmPassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请确认密码'))
    } else if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

/**
 * 用户名密码校验规则
 */
const rules = {
    username: [
        { validator: validateUsername, trigger: ['blur', 'change'] },
        { min: 2, max: 10, message: '用户名需要在2-10个字符之间', trigger: ['blur', 'change'] },
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 4, max: 20, message: '密码需要在4-20个字符之间', trigger: ['blur', 'change'] },
    ],
    confirm_password: [
        { validator: validateConfirmPassword, trigger: ['blur', 'change'] }
    ],
    email: [
        { required: true, message: '请输入电子邮件地址', trigger: 'blur' },
        { type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] }
    ],
    verify_code: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
    ]
}

const formRef = ref()
const isEmailValid = ref(false)

const onValidate = (prop, isValid) => {
    if (prop === 'email') {
        isEmailValid.value = isValid
    }
}

const register = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {

        } else {
            ElMessage.warning('请完整填写注册表单')
        }
    })
}

const validateEmail = () => {
    post('/api/auth/valid-email', {
        email: form.email
    }, (message) => {
        ElMessage.success(message)
    })
}
</script>

<template>
    <div style="text-align: center; margin: 0 20px">
        <div style="margin-top: 180px">
            <div style="font-size: 25px; font-weight: bold">注册新用户</div>
            <div style="font-size: 14px; color: grey">填写信息以完成注册</div>
        </div>
        <div style="margin-top: 80px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" type="text" placeholder="输入用户名">
                        <template #prefix>
                            <el-icon><User/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="输入密码">
                        <template #prefix>
                            <el-icon><Lock/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="confirm_password">
                    <el-input v-model="form.confirm_password" type="password" placeholder="确认密码">
                        <template #prefix>
                            <el-icon><Lock/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="email">
                    <el-input v-model="form.email" type="text" placeholder="电子邮件地址">
                        <template #prefix>
                            <el-icon><Message/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="verify_code">
                    <el-row :gutter="10" style="width: 100%">
                        <el-col :span="17">
                            <el-input v-model="form.verify_code" type="text" placeholder="输入验证码">
                                <template #prefix>
                                    <el-icon><EditPen/></el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="5">
                            <el-button @click="validateEmail" type="success" :disabled="!isEmailValid">获取验证码</el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>
        <div style="margin-top: 80px">
            <el-button @click="register" style="width: 270px" type="warning" plain>立即注册</el-button>
        </div>
        <div style="margin-top: 20px">
            <span style="font-size: 14px; line-height: 15px; color: grey">已有账号？</span>
            <el-link @click="router.push('/')" type="primary" style="translate: 0 -1px">
                立即登录
            </el-link>
        </div>
    </div>
</template>

<style scoped>

</style>