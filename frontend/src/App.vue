<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-white border-b border-slate-200">
      <div class="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
        <h1 class="text-xl font-semibold text-slate-900">Vinted Monitor</h1>
        <nav class="flex gap-2">
          <RouterLink
            to="/feed"
            class="px-3 py-1.5 rounded text-sm font-medium text-slate-600 hover:bg-slate-100"
            active-class="bg-slate-900 text-white hover:bg-slate-900"
          >Feed</RouterLink>
          <RouterLink
            to="/watches"
            class="px-3 py-1.5 rounded text-sm font-medium text-slate-600 hover:bg-slate-100"
            active-class="bg-slate-900 text-white hover:bg-slate-900"
          >Watches</RouterLink>
          <RouterLink
            to="/settings"
            class="px-3 py-1.5 rounded text-sm font-medium text-slate-600 hover:bg-slate-100"
            active-class="bg-slate-900 text-white hover:bg-slate-900"
          >Settings</RouterLink>
        </nav>
      </div>
    </header>

    <main class="flex-1 max-w-5xl w-full mx-auto px-6 py-6">
      <RouterView />
    </main>
  </div>
</template>
