<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { watchesApi, type Watch, type WatchInput } from '../api/client'

const watches = ref<Watch[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

const blankForm = (): WatchInput => ({
  name: '',
  searchText: '',
  minPrice: null,
  maxPrice: null,
  enabled: true
})

const form = reactive<WatchInput>(blankForm())
const editingId = ref<number | null>(null)

async function load() {
  loading.value = true
  error.value = null
  try {
    watches.value = await watchesApi.list()
  } catch (e: any) {
    error.value = e?.message ?? 'Failed to load watches'
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(form, blankForm())
  editingId.value = null
}

function edit(w: Watch) {
  editingId.value = w.id
  form.name = w.name
  form.searchText = w.searchText
  form.minPrice = w.minPrice
  form.maxPrice = w.maxPrice
  form.enabled = w.enabled
}

async function save() {
  if (!form.name.trim() || !form.searchText.trim()) return
  try {
    if (editingId.value != null) {
      await watchesApi.update(editingId.value, { ...form })
    } else {
      await watchesApi.create({ ...form })
    }
    resetForm()
    await load()
  } catch (e: any) {
    error.value = e?.message ?? 'Save failed'
  }
}

async function remove(id: number) {
  if (!confirm('Delete this watch?')) return
  await watchesApi.remove(id)
  await load()
}

onMounted(load)
</script>

<template>
  <section>
    <div class="bg-white border border-slate-200 rounded-xl p-5 mb-6 shadow-sm">
      <h2 class="text-lg font-semibold mb-4">
        {{ editingId == null ? 'Add a watch' : 'Edit watch' }}
      </h2>

      <form class="grid grid-cols-1 md:grid-cols-2 gap-4" @submit.prevent="save">
        <label class="block">
          <span class="text-sm font-medium text-slate-700">Label</span>
          <input
            v-model="form.name"
            type="text"
            placeholder="Catan"
            class="mt-1 block w-full rounded-lg border-slate-300 shadow-sm border px-3 py-2 focus:outline-none focus:border-vinted focus:ring-2 focus:ring-vinted-200"
            required
          />
        </label>
        <label class="block">
          <span class="text-sm font-medium text-slate-700">Vinted search text</span>
          <input
            v-model="form.searchText"
            type="text"
            placeholder="Settlers of Catan"
            class="mt-1 block w-full rounded-lg border-slate-300 shadow-sm border px-3 py-2 focus:outline-none focus:border-vinted focus:ring-2 focus:ring-vinted-200"
            required
          />
        </label>
        <label class="block">
          <span class="text-sm font-medium text-slate-700">Min price (HUF)</span>
          <input
            v-model.number="form.minPrice"
            type="number"
            min="0"
            class="mt-1 block w-full rounded-lg border-slate-300 shadow-sm border px-3 py-2 focus:outline-none focus:border-vinted focus:ring-2 focus:ring-vinted-200"
          />
        </label>
        <label class="block">
          <span class="text-sm font-medium text-slate-700">Max price (HUF)</span>
          <input
            v-model.number="form.maxPrice"
            type="number"
            min="0"
            class="mt-1 block w-full rounded-lg border-slate-300 shadow-sm border px-3 py-2 focus:outline-none focus:border-vinted focus:ring-2 focus:ring-vinted-200"
          />
        </label>
        <label class="flex items-center gap-2 md:col-span-2">
          <input v-model="form.enabled" type="checkbox" class="rounded accent-vinted" />
          <span class="text-sm text-slate-700">Enabled</span>
        </label>

        <div class="md:col-span-2 flex gap-2">
          <button
            type="submit"
            class="px-5 py-2 rounded-full bg-vinted text-white text-sm font-semibold hover:bg-vinted-700 transition-colors"
          >
            {{ editingId == null ? 'Add watch' : 'Save changes' }}
          </button>
          <button
            v-if="editingId != null"
            type="button"
            class="px-5 py-2 rounded-full border border-slate-300 text-sm font-medium hover:bg-slate-50"
            @click="resetForm"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>

    <div v-if="error" class="text-sm text-red-600 mb-3">{{ error }}</div>

    <div class="bg-white border border-slate-200 rounded-xl overflow-hidden shadow-sm">
      <table class="min-w-full text-sm">
        <thead class="bg-vinted-50 text-vinted-800">
          <tr>
            <th class="text-left px-4 py-2">Label</th>
            <th class="text-left px-4 py-2">Search</th>
            <th class="text-left px-4 py-2">Price</th>
            <th class="text-left px-4 py-2">Enabled</th>
            <th class="text-left px-4 py-2">Last poll</th>
            <th class="px-4 py-2"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="6" class="px-4 py-6 text-center text-slate-500">Loading…</td>
          </tr>
          <tr v-else-if="watches.length === 0">
            <td colspan="6" class="px-4 py-6 text-center text-slate-500">
              No watches yet. Add one above.
            </td>
          </tr>
          <tr v-for="w in watches" :key="w.id" class="border-t border-slate-100">
            <td class="px-4 py-2 font-medium">{{ w.name }}</td>
            <td class="px-4 py-2 text-slate-600">{{ w.searchText }}</td>
            <td class="px-4 py-2 text-slate-600">
              {{ w.minPrice ?? '—' }} – {{ w.maxPrice ?? '—' }}
            </td>
            <td class="px-4 py-2">
              <span
                :class="w.enabled ? 'text-emerald-700' : 'text-slate-400'"
                class="text-xs font-medium"
              >{{ w.enabled ? 'on' : 'off' }}</span>
            </td>
            <td class="px-4 py-2 text-slate-500 text-xs">
              {{ w.lastPolledAt ? new Date(w.lastPolledAt).toLocaleString() : 'never' }}
            </td>
            <td class="px-4 py-2 text-right">
              <button class="text-xs font-medium text-vinted hover:underline mr-2" @click="edit(w)">
                Edit
              </button>
              <button class="text-xs text-red-600 hover:underline" @click="remove(w.id)">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
