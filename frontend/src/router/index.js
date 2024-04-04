import { createRouter, createWebHistory } from 'vue-router'

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

export default router
