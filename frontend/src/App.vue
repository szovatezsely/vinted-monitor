<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <header class="bg-white border-b border-slate-200">
      <div class="max-w-5xl mx-auto px-6 py-4 flex items-center justify-between">
        <h1 class="text-xl font-extrabold tracking-tight text-vinted">Vinted Monitor</h1>
        <nav class="flex gap-2">
          <RouterLink
            to="/feed"
            class="px-3.5 py-1.5 rounded-full text-sm font-medium text-slate-600 hover:bg-vinted-50 hover:text-vinted"
            active-class="!bg-vinted !text-white"
          >Feed</RouterLink>
          <RouterLink
            to="/watches"
            class="px-3.5 py-1.5 rounded-full text-sm font-medium text-slate-600 hover:bg-vinted-50 hover:text-vinted"
            active-class="!bg-vinted !text-white"
          >Watches</RouterLink>
          <RouterLink
            to="/settings"
            class="px-3.5 py-1.5 rounded-full text-sm font-medium text-slate-600 hover:bg-vinted-50 hover:text-vinted"
            active-class="!bg-vinted !text-white"
          >Settings</RouterLink>
        </nav>
      </div>
    </header>

    <main class="flex-1 max-w-5xl w-full mx-auto px-6 py-6">
      <RouterView />
    </main>
  </div>
</template>
