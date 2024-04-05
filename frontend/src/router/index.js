import { createRouter, createWebHistory } from 'vue-router'
import {useStore} from "@/stores/index.js";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'login',
                    component: () => import('@/components/pages/LoginPage.vue')
                }, {
                    path: 'register',
                    name: 'register',
                    component: () => import('@/components/pages/RegisterPage.vue')
                }, {
                    path: 'forget',
                    name: 'forget-password',
                    component: () => import('@/components/pages/ForgetPasswordPage.vue')
                }
            ]
        }, {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
    const store = useStore()
    if (store.auth.user != null) {  // 用户已登录
        if (to.matched.length === 0 || to.name in ['login', 'register', 'forget-password']) {  // 访问的url不存在或前往登录相关界面
            next('/index')
        } else {
            next()
        }
    } else {  // 用户未登录
        if (to.matched.length === 0 || to.path.startsWith('/index')) {
            next('/')
        } else {
            next()
        }
    }
})

export default router
