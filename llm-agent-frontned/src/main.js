import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './views/Home.vue'
import SuseAgent from './views/SuseAgent.vue'
import AiSuperAgent from './views/AiSuperAgent.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/suse-agent', component: SuseAgent },
  { path: '/ai-super-agent', component: AiSuperAgent }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')