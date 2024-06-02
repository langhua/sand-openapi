import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import commonjs from '@rollup/plugin-commonjs'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    // 注意：commonjs要放在第一个
    commonjs(),
    vue(),
    vueJsx()
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
    preserveSymlinks: true
  },
  base: process.env.NODE_ENV === 'production' ? '/openapi/v3/' : './',
  // base: './',
  build: {
    outDir: '../../webapp/openapi/v3/',
    chunkSizeWarningLimit: 1500, // 文件大小超过1500kb时显示警告提示
    rollupOptions: {
      output: {
        manualChunks(id: string | string[]): string {
          if (id.includes("swagger-ui-dist")) {
            return "swagger-ui-dist" + id.toString().split("swagger-ui-dist")[1]
          } else if (id.includes("swagger-editor-dist")) {
            return "swagger-editor-dist" + id.toString().split("swagger-editor-dist")[1]
          }
        }
      }
    },
  },
  envDir: './env',
  optimizeDeps: {
    include: [
      '@/assets/swagger-editor-dist/swagger-editor-es-bundle.js',
      '@/assets/swagger-editor-dist/swagger-editor-standalone-preset.js'
    ],
  },
  server: {
    proxy: {
      '/sanddav/': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
        followRedirects: true,
      },
      '/openapi/control/': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
        followRedirects: true,
      },
      '/petstore/': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
      },
      '/sample/': {
        target: 'https://localhost:8443/petstore/app',
        changeOrigin: true,
        secure: false,
      },
      '/oauth/': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false,
      },
    }
  }
})
