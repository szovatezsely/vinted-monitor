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
        class="text-sm font-medium px-4 py-1.5 rounded-full border border-vinted text-vinted bg-white hover:bg-vinted-50 transition-colors"
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
        class="bg-white border border-slate-200 rounded-xl overflow-hidden flex items-center shadow-sm hover:shadow-md transition-shadow"
      >
        <div v-if="m.thumbnailUrl" class="p-3 flex-shrink-0">
          <img
            :src="m.thumbnailUrl"
            :alt="m.title"
            class="w-24 h-24 object-cover rounded-lg"
          />
        </div>
        <div class="p-3 flex-1 min-w-0">
          <span class="inline-block text-xs font-medium text-vinted bg-vinted-50 px-2 py-0.5 rounded-full">{{ m.watchName }}</span>
          <a
            v-if="m.url"
            :href="m.url"
            target="_blank"
            rel="noopener"
            class="block font-semibold text-slate-900 truncate hover:text-vinted mt-1"
          >{{ m.title }}</a>
          <div v-else class="font-semibold text-slate-900 truncate mt-1">{{ m.title }}</div>
          <div class="text-sm mt-1">
            <span v-if="m.price" class="text-vinted font-bold">{{ m.price }} {{ m.currency }}</span>
          </div>
          <div class="text-xs text-slate-400 mt-1">
            {{ new Date(m.foundAt).toLocaleString() }}
          </div>
        </div>
      </li>
    </ul>
  </section>
</template>
