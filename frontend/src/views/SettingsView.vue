<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { settingsApi, type Settings } from '../api/client'

const settings = ref<Settings | null>(null)
const recipient = ref('')
const loading = ref(false)
const saving = ref(false)
const polling = ref(false)
const message = ref<string | null>(null)
const error = ref<string | null>(null)

async function load() {
  loading.value = true
  try {
    const s = await settingsApi.get()
    settings.value = s
    recipient.value = s.recipient
  } catch (e: any) {
    error.value = e?.message ?? 'Failed to load settings'
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  error.value = null
  message.value = null
  try {
    settings.value = await settingsApi.update(recipient.value.trim())
    message.value = 'Saved.'
  } catch (e: any) {
    error.value = e?.message ?? 'Save failed'
  } finally {
    saving.value = false
  }
}

async function pollNow() {
  polling.value = true
  error.value = null
  message.value = null
  try {
    await settingsApi.pollNow()
    message.value = 'Poll triggered. Check the feed in a few seconds.'
  } catch (e: any) {
    error.value = e?.message ?? 'Poll failed'
  } finally {
    polling.value = false
  }
}

onMounted(load)
</script>

<template>
  <section class="max-w-xl">
    <h2 class="text-lg font-semibold mb-4">Settings</h2>

    <div v-if="loading" class="text-slate-500 text-sm">Loading…</div>

    <div v-else-if="settings" class="bg-white border border-slate-200 rounded-lg p-5 space-y-4">
      <div>
        <label class="block text-sm font-medium text-slate-700">Notification recipient</label>
        <input
          v-model="recipient"
          type="email"
          placeholder="you@example.com"
          class="mt-1 block w-full rounded border-slate-300 shadow-sm border px-3 py-2"
        />
        <p class="text-xs text-slate-500 mt-1">
          Email address that receives the digest when new matches are found.
        </p>
      </div>

      <div class="text-sm text-slate-600 space-y-1">
        <div><strong>From:</strong> {{ settings.from || '(not set — configure MAIL_USERNAME)' }}</div>
        <div>
          <strong>SMTP configured:</strong>
          <span :class="settings.mailConfigured ? 'text-emerald-700' : 'text-red-600'">
            {{ settings.mailConfigured ? 'yes' : 'no' }}
          </span>
        </div>
        <div><strong>Poll interval:</strong> {{ Math.round(settings.pollIntervalMs / 60000) }} min</div>
      </div>

      <div class="flex gap-2 pt-2">
        <button
          class="px-4 py-2 rounded bg-slate-900 text-white text-sm font-medium hover:bg-slate-800 disabled:opacity-50"
          :disabled="saving"
          @click="save"
        >
          {{ saving ? 'Saving…' : 'Save' }}
        </button>
        <button
          class="px-4 py-2 rounded border border-slate-300 text-sm hover:bg-slate-50 disabled:opacity-50"
          :disabled="polling"
          @click="pollNow"
        >
          {{ polling ? 'Polling…' : 'Poll now' }}
        </button>
      </div>

      <div v-if="message" class="text-sm text-emerald-700">{{ message }}</div>
      <div v-if="error" class="text-sm text-red-600">{{ error }}</div>
    </div>
  </section>
</template>
