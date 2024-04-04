<script setup>
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const active = ref(0)  // 忘记密码的步骤

const form = reactive({
    password: '',
    confirmPassword: '',
    email: '',
    verifyCode: ''
})

/**
 * 确认密码的规则
 * @param rule
 * @param value
 * @param callback
 */
const confirmPasswordValidator = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请确认密码'))
    } else if (value !== form.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const formRef = ref()
const isEmailValid = ref(false)  // 验证邮箱是否合法

const onValidate = (prop, isValid) => {
    if (prop === 'email') {
        isEmailValid.value = isValid
    }
}

const rules = {
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 4, max: 20, message: '密码需要在4-20个字符之间', trigger: 'blur' },
    ],
    confirmPassword: [
        { validator: confirmPasswordValidator, trigger: ['blur', 'change'] }
    ],
    email: [
        { required: true, message: '请输入电子邮件地址', trigger: 'blur' },
        { type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'] }
    ],
    verifyCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' },
    ]
}

const sendValidateEmail = () => {
    post('/api/auth/send-to-existed-email', {
        email: form.email
    }, (message) => {
        ElMessage.success(message)
    })
}

const validateEmail = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post('/api/auth/validate-email', {
                email: form.email,
                verifyCode: form.verifyCode
            }, (message) => {
                ElMessage.success(message)
                active.value = 1
            })
        } else {
            ElMessage.warning('请重新进行验证')
        }
    })
}

const resetPassword = () => {
    formRef.value.validate((isValid) => {
        if (isValid) {
            post('/api/auth/reset-password', {
                password: form.password,
                email: form.email
            }, (message) => {
                ElMessage.success(message)
                router.push('/')
            })
        } else {
            ElMessage.warning('请重新输入和确认新的密码')
        }
    })
}
</script>

<template>
    <!-- 第一步：发送验证码 -->
    <div style="text-align: center; margin: 0 20px" v-if="active === 0">
        <div style="text-align: center; margin-top: 180px">
            <div style="font-size: 25px; font-weight: bold">发送验证码</div>
            <div style="font-size: 14px; color: grey">请输入您的电子邮件地址</div>
        </div>

        <div style="margin-top: 40px">
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="发送验证码"/>
                <el-step title="重新设定密码"/>
            </el-steps>
        </div>

        <div style="margin-top: 40px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="email">
                    <el-input v-model="form.email" type="text" placeholder="电子邮件地址">
                        <template #prefix>
                            <el-icon><Message/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="verifyCode">
                    <el-row :gutter="10" style="width: 100%">
                        <el-col :span="17">
                            <el-input v-model="form.verifyCode" type="text" placeholder="输入验证码">
                                <template #prefix>
                                    <el-icon><EditPen/></el-icon>
                                </template>
                            </el-input>
                        </el-col>
                        <el-col :span="5">
                            <el-button @click="sendValidateEmail" type="success" :disabled="!isEmailValid">获取验证码</el-button>
                        </el-col>
                    </el-row>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin-top: 80px">
            <el-button @click="validateEmail" style="width: 270px" type="success" plain>下一步</el-button>
        </div>
    </div>

    <!-- 第二步：重置密码 -->
    <div style="text-align: center; margin: 0 20px" v-if="active === 1">
        <div style="text-align: center; margin-top: 180px">
            <div style="font-size: 25px; font-weight: bold">重新设定密码</div>
            <div style="font-size: 14px; color: grey">请输入并确认您的新密码</div>
        </div>

        <div style="margin-top: 40px">
            <el-steps :active="active" finish-status="success" align-center>
                <el-step title="发送验证码"/>
                <el-step title="重新设定密码"/>
            </el-steps>
        </div>

        <div style="margin-top: 40px">
            <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="输入新密码">
                        <template #prefix>
                            <el-icon><Lock/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="confirmPassword">
                    <el-input v-model="form.confirmPassword" type="password" placeholder="确认新密码">
                        <template #prefix>
                            <el-icon><Lock/></el-icon>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
        </div>

        <div style="margin-top: 80px">
            <el-button @click="resetPassword" style="width: 270px" type="danger" plain>重置密码</el-button>
        </div>
    </div>
</template>

<style scoped>

</style>