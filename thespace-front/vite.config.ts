import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import {VitePWA} from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    VitePWA({
      registerType: 'autoUpdate',
      devOptions: {
        enabled: true
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg}']
      },
      filename: 'firebase-messaging-sw.js',
      injectRegister: 'auto',
      manifest: {
        name: 'The Space',
        short_name: 'TheSpace',
        description: 'The Space PWA',
        theme_color: '#ffffff'
      }
    })
  ],
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: true,
    proxy: {
      '/**': {
        target: 'http://localhost:8080/',
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
});