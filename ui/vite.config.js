import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  css: {
    preprocessorOptions: {
      sass: {
        api: 'modern-compiler'
      }
    }
  },
  plugins: [
    vue(),
  ],
  server: {
    port: 27697,
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
