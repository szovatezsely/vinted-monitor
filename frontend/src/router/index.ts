import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/feed' },
  { path: '/feed', name: 'feed', component: () => import('../views/FeedView.vue') },
  { path: '/watches', name: 'watches', component: () => import('../views/WatchesView.vue') },
  { path: '/settings', name: 'settings', component: () => import('../views/SettingsView.vue') }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
