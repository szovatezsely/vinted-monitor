package com.vintedmonitor.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.vintedmonitor.config.VintedProperties
import com.vintedmonitor.dto.VintedItem
import com.vintedmonitor.dto.VintedSearchResponse
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Talks to Vinted's unofficial JSON API.
 *
 * Bootstrap flow:
 *   1. GET base URL once (no cookies). Vinted sets `_vinted_fr_session` (or similar)
 *      and an anonymous CSRF token. OkHttp captures the cookies via [InMemoryCookieJar].
 *   2. Subsequent GETs to /api/v2/catalog/items reuse the cookies and return JSON.
 *   3. On 401/403 we drop cookies and retry once.
 */
@Component
class VintedClient(
    private val props: VintedProperties,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val cookieJar = InMemoryCookieJar()

    private val http: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    fun search(query: String, perPage: Int = 20): List<VintedItem> {
        ensureSession()
        val response = doSearch(query, perPage)
        if (response != null) return response.items

        log.warn("Vinted returned non-OK. Refreshing session and retrying once.")
        cookieJar.clear()
        ensureSession()
        return doSearch(query, perPage)?.items ?: emptyList()
    }

    private fun ensureSession() {
        val baseUrl = props.baseUrl.toHttpUrl()
        if (cookieJar.hasCookiesFor(baseUrl)) return
        val request = Request.Builder()
            .url(baseUrl)
            .header("User-Agent", props.userAgent)
            .header("Accept", "text/html,application/xhtml+xml")
            .header("Accept-Language", "hu-HU,hu;q=0.9,en;q=0.8")
            .get()
            .build()
        http.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                log.warn("Session bootstrap returned HTTP {}", resp.code)
            } else {
                log.debug("Session bootstrap OK; cookies captured.")
            }
        }
    }

    private fun doSearch(query: String, perPage: Int): VintedSearchResponse? {
        val url: HttpUrl = "${props.baseUrl}${props.apiPath}".toHttpUrl().newBuilder()
            .addQueryParameter("search_text", query)
            .addQueryParameter("order", "newest_first")
            .addQueryParameter("per_page", perPage.toString())
            .addQueryParameter("page", "1")
            .build()

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", props.userAgent)
            .header("Accept", "application/json, text/plain, */*")
            .header("Accept-Language", "hu-HU,hu;q=0.9,en;q=0.8")
            .header("Referer", props.baseUrl)
            .header("X-Requested-With", "XMLHttpRequest")
            .get()
            .build()

        return try {
            http.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    log.warn("Vinted /catalog/items returned HTTP {} for query='{}'", resp.code, query)
                    return null
                }
                val body = resp.body?.string() ?: return null
                objectMapper.readValue(body, VintedSearchResponse::class.java)
            }
        } catch (e: IOException) {
            log.warn("IO error calling Vinted for query='{}': {}", query, e.message)
            null
        } catch (e: Exception) {
            log.warn("Failed to parse Vinted response for query='{}': {}", query, e.message)
            null
        }
    }
}

private class InMemoryCookieJar : CookieJar {
    private val store = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return
        val key = url.host
        val list = store.computeIfAbsent(key) { mutableListOf() }
        synchronized(list) {
            // Replace cookies with the same name+path
            cookies.forEach { incoming ->
                list.removeAll { it.name == incoming.name && it.path == incoming.path }
                list.add(incoming)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val list = store[url.host] ?: return emptyList()
        val now = System.currentTimeMillis()
        synchronized(list) {
            list.removeAll { it.expiresAt < now }
            return list.filter { it.matches(url) }.toList()
        }
    }

    fun hasCookiesFor(url: HttpUrl): Boolean {
        val list = store[url.host] ?: return false
        return list.isNotEmpty()
    }

    fun clear() {
        store.clear()
    }
}
