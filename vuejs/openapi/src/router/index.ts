import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Editor from '@/views/Editor.vue'
const env = import.meta.env

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: env.VITE_OPENAPI_BASE_URL,
      name: 'home',
      component: Home
    },
    {
      path: env.VITE_OPENAPI_BASE_URL + 'viewfile',
      name: 'viewfile',
      component: Home
    },
    {
      path: env.VITE_OPENAPI_BASE_URL + 'login',
      name: 'login',
      component: Login
    },
    {
      path: env.VITE_OPENAPI_BASE_URL + 'editor',
      name: 'editor',
      component: Editor
    }
  ]
})

export default router
