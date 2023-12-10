import { createRouter, createWebHistory } from 'vue-router'
import FileTree from '@/views/FileTree.vue'
// import ServiceDetails from '@/views/ServiceDetails.vue'
const env = import.meta.env

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: env.VITE_OPENAPI_BASE_URL,
      name: 'fileTree',
      component: FileTree
    },
    // {
    //   path: env.VITE_MDNS_BASE_URL + 'details/:type/:base64Name',
    //   name: 'details',
    //   component: ServiceDetails
    // }
  ]
})

export default router
