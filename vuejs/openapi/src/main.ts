import { createApp } from 'vue'
import '@/style.css'
import App from '@/App.vue'
import { createPinia } from 'pinia'
import { i18n } from '@/i18n'

import ElementPlus from 'element-plus'
import 'element-plus/theme-chalk/index.css'
import router from '@/router'

const app = createApp(App)
app.use(ElementPlus)
app.use(createPinia())
app.use(router)
app.use(i18n)
app.mount('#app')
