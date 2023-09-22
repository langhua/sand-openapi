import { fileURLToPath, URL } from 'node:url'
   
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    preserveSymlinks: true
  },
  base: './',
  build: {
    outDir: '../../webapp/openapi/v3',
    chunkSizeWarningLimit: 1500, // 文件大小超过1500kb时显示警告提示
    rollupOptions: {
      output: {
        manualChunks(id: any): string {
          if (id.includes("swagger-ui-dist")) {
              return "swagger-ui-dist" + id.toString().split("swagger-ui-dist")[1]
          }
        }
      }
    }
  },
  envDir: './env',
  optimizeDeps: {
    include: [
      // '@/assets/swagger-editor-dist/swagger-editor-es-bundle.js',
      // '@/assets/swagger-editor-dist/swagger-editor-standalone-preset.js'
    ],
  },
})
