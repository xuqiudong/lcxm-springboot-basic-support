import { createApp } from 'vue'
import App from './App.vue'
import './style.css'

// 仅引入Element Plus基础样式（组件通过自动导入，无需手动注册）
import 'element-plus/theme-chalk/index.css'

// 直接挂载根组件，无任何全局状态/组件注册
createApp(App).mount('#app')
