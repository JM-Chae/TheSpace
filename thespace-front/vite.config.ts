import {fileURLToPath, URL} from 'node:url'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import {VitePWA} from 'vite-plugin-pwa'

export default defineConfig(({ command }) => ({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    ...(command === 'build' ? [VitePWA({
      strategies: 'injectManifest',
      srcDir: 'src',
      filename: 'firebase-sw.ts',
      registerType: 'autoUpdate',
      devOptions: {
        enabled: true,
        type: 'module'
      },
      manifest: {
        name: 'The Space',
        short_name: 'TheSpace',
        description: 'The Space PWA',
        theme_color: '#ffffff'
      }
    })] : [])
  ],
  define: {
    'global': 'window',
  },
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
}))