import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/HomeView.vue'
import Login from '@/views/LoginView.vue'
import Editor from '@/views/EditorView.vue'
const env = import.meta.env

const router = createRouter({
  history: createWebHistory(env.BASE_URL),
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
