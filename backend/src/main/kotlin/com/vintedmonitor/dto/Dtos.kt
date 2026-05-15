package com.vintedmonitor.dto

import com.vintedmonitor.domain.MatchedListing
import com.vintedmonitor.domain.WatchedGame
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class WatchRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val searchText: String,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val enabled: Boolean = true
)

data class WatchResponse(
    val id: Long,
    val name: String,
    val searchText: String,
    val minPrice: Int?,
    val maxPrice: Int?,
    val enabled: Boolean,
    val createdAt: Instant,
    val lastPolledAt: Instant?
) {
    companion object {
        fun from(w: WatchedGame) = WatchResponse(
            id = w.id ?: 0,
            name = w.name,
            searchText = w.searchText,
            minPrice = w.minPrice,
            maxPrice = w.maxPrice,
            enabled = w.enabled,
            createdAt = w.createdAt,
            lastPolledAt = w.lastPolledAt
        )
    }
}

data class MatchResponse(
    val id: Long,
    val watchId: Long,
    val watchName: String,
    val itemId: Long,
    val title: String,
    val price: String?,
    val currency: String?,
    val url: String?,
    val thumbnailUrl: String?,
    val foundAt: Instant,
    val notified: Boolean
) {
    companion object {
        fun from(m: MatchedListing) = MatchResponse(
            id = m.id ?: 0,
            watchId = m.watchId,
            watchName = m.watchName,
            itemId = m.itemId,
            title = m.title,
            price = m.price,
            currency = m.currency,
            url = m.url,
            thumbnailUrl = m.thumbnailUrl,
            foundAt = m.foundAt,
            notified = m.notified
        )
    }
}

data class SettingsResponse(
    val recipient: String,
    val from: String,
    val mailConfigured: Boolean,
    val pollIntervalMs: Long
)

data class SettingsUpdate(
    val recipient: String?
)
