import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import 'element-plus/dist/index.css'
import axios from "axios";

const app = createApp(App)

axios.defaults.baseURL = import.meta.env.VITE_API_BASEURL  // 跨域访问后端host

app.config.devtools = import.meta.env.MODE === 'development';  // 根据开发或生产环境开关dev-tools

app.use(createPinia())
app.use(router)

app.mount('#app')
