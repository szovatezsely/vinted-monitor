import axios from 'axios'

export const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

export interface Watch {
  id: number
  name: string
  searchText: string
  minPrice: number | null
  maxPrice: number | null
  enabled: boolean
  createdAt: string
  lastPolledAt: string | null
}

export interface WatchInput {
  name: string
  searchText: string
  minPrice: number | null
  maxPrice: number | null
  enabled: boolean
}

export interface Match {
  id: number
  watchId: number
  watchName: string
  itemId: number
  title: string
  price: string | null
  currency: string | null
  url: string | null
  thumbnailUrl: string | null
  foundAt: string
  notified: boolean
}

export interface Settings {
  recipient: string
  from: string
  mailConfigured: boolean
  pollIntervalMs: number
}

export const watchesApi = {
  list: () => api.get<Watch[]>('/watches').then(r => r.data),
  create: (input: WatchInput) => api.post<Watch>('/watches', input).then(r => r.data),
  update: (id: number, input: WatchInput) =>
    api.put<Watch>(`/watches/${id}`, input).then(r => r.data),
  remove: (id: number) => api.delete(`/watches/${id}`).then(() => undefined)
}

export const feedApi = {
  recent: (limit = 50) =>
    api.get<Match[]>(`/feed?limit=${limit}`).then(r => r.data)
}

export const settingsApi = {
  get: () => api.get<Settings>('/settings').then(r => r.data),
  update: (recipient: string) =>
    api.put<Settings>('/settings', { recipient }).then(r => r.data),
  pollNow: () => api.post('/settings/poll-now').then(() => undefined)
}
