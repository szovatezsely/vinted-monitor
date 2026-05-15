package com.vintedmonitor.service

import com.vintedmonitor.domain.MatchedListing
import com.vintedmonitor.domain.SeenItem
import com.vintedmonitor.domain.WatchedGame
import com.vintedmonitor.dto.VintedItem
import com.vintedmonitor.dto.WatchRequest
import com.vintedmonitor.repository.MatchedListingRepository
import com.vintedmonitor.repository.SeenItemRepository
import com.vintedmonitor.repository.WatchedGameRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class WatchService(
    private val watchRepo: WatchedGameRepository,
    private val seenRepo: SeenItemRepository,
    private val matchRepo: MatchedListingRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun listAll(): List<WatchedGame> = watchRepo.findAll()
    fun listEnabled(): List<WatchedGame> = watchRepo.findAllByEnabledTrue()

    @Transactional
    fun create(req: WatchRequest): WatchedGame {
        val w = WatchedGame(
            name = req.name.trim(),
            searchText = req.searchText.trim(),
            minPrice = req.minPrice,
            maxPrice = req.maxPrice,
            enabled = req.enabled,
            createdAt = Instant.now()
        )
        return watchRepo.save(w)
    }

    @Transactional
    fun update(id: Long, req: WatchRequest): WatchedGame? {
        val w = watchRepo.findById(id).orElse(null) ?: return null
        w.name = req.name.trim()
        w.searchText = req.searchText.trim()
        w.minPrice = req.minPrice
        w.maxPrice = req.maxPrice
        w.enabled = req.enabled
        return watchRepo.save(w)
    }

    @Transactional
    fun delete(id: Long): Boolean {
        if (!watchRepo.existsById(id)) return false
        watchRepo.deleteById(id)
        return true
    }

    fun recentMatches(limit: Int): List<MatchedListing> =
        matchRepo.findAllByOrderByFoundAtDesc(PageRequest.of(0, limit))

    /**
     * Filters the freshly-fetched [items] down to those not yet seen for this watch,
     * persists the new ones to seen_items, and records matched_listings entries.
     * Returns the newly matched listings (for emailing).
     */
    @Transactional
    fun recordNewMatches(watch: WatchedGame, items: List<VintedItem>): List<MatchedListing> {
        val watchId = watch.id ?: return emptyList()
        if (items.isEmpty()) return emptyList()

        val filtered = items.filter { passesPriceFilter(watch, it) }
        if (filtered.isEmpty()) return emptyList()

        val incomingIds = filtered.map { it.id }
        val existing = seenRepo.findExistingItemIds(watchId, incomingIds).toSet()
        val fresh = filtered.filter { it.id !in existing }
        if (fresh.isEmpty()) return emptyList()

        val now = Instant.now()
        seenRepo.saveAll(fresh.map { SeenItem(watchId = watchId, itemId = it.id, firstSeenAt = now) })

        val matches = fresh.map { item ->
            MatchedListing(
                watchId = watchId,
                watchName = watch.name,
                itemId = item.id,
                title = item.title ?: "(no title)",
                price = item.price?.amount,
                currency = item.price?.currencyCode,
                url = item.url,
                thumbnailUrl = item.photo?.url,
                foundAt = now,
                notified = false
            )
        }
        val saved = matchRepo.saveAll(matches).toList()
        log.info("Watch '{}' [{}]: {} new matches (of {} returned)", watch.name, watchId, saved.size, items.size)
        return saved
    }

    @Transactional
    fun markPolled(watch: WatchedGame) {
        watch.lastPolledAt = Instant.now()
        watchRepo.save(watch)
    }

    @Transactional
    fun markNotified(matches: List<MatchedListing>) {
        if (matches.isEmpty()) return
        matches.forEach { it.notified = true }
        matchRepo.saveAll(matches)
    }

    private fun passesPriceFilter(watch: WatchedGame, item: VintedItem): Boolean {
        val raw = item.price?.amount ?: return true
        val price = raw.toDoubleOrNull() ?: return true
        watch.minPrice?.let { if (price < it) return false }
        watch.maxPrice?.let { if (price > it) return false }
        return true
    }
}
