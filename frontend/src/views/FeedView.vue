<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { feedApi, type Match } from '../api/client'

const items = ref<Match[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    items.value = await feedApi.recent(100)
  } catch (e: any) {
    error.value = e?.message ?? 'Failed to load feed'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section>
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-semibold">Recent matches</h2>
      <button
        class="text-sm px-3 py-1.5 rounded border border-slate-300 bg-white hover:bg-slate-50"
        @click="load"
      >
        Refresh
      </button>
    </div>

    <div v-if="error" class="text-sm text-red-600 mb-3">{{ error }}</div>

    <div v-if="loading" class="text-slate-500 text-sm">Loading…</div>
    <div v-else-if="items.length === 0" class="text-slate-500 text-sm">
      No matches yet. They'll appear here once the next poll finds new listings.
    </div>

    <ul v-else class="grid grid-cols-1 md:grid-cols-2 gap-3">
      <li
        v-for="m in items"
        :key="m.id"
        class="bg-white border border-slate-200 rounded-lg overflow-hidden flex"
      >
        <img
          v-if="m.thumbnailUrl"
          :src="m.thumbnailUrl"
          :alt="m.title"
          class="w-24 h-24 object-cover flex-shrink-0"
        />
        <div class="p-3 flex-1 min-w-0">
          <div class="text-xs text-slate-500">{{ m.watchName }}</div>
          <a
            v-if="m.url"
            :href="m.url"
            target="_blank"
            rel="noopener"
            class="block font-medium text-slate-900 truncate hover:underline"
          >{{ m.title }}</a>
          <div v-else class="font-medium text-slate-900 truncate">{{ m.title }}</div>
          <div class="text-sm text-slate-700 mt-1">
            <span v-if="m.price">{{ m.price }} {{ m.currency }}</span>
          </div>
          <div class="text-xs text-slate-400 mt-1">
            {{ new Date(m.foundAt).toLocaleString() }}
          </div>
        </div>
      </li>
    </ul>
  </section>
</template>
