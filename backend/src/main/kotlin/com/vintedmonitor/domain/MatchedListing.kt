package com.vintedmonitor.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(
    name = "matched_listings",
    indexes = [Index(name = "idx_matched_found_at", columnList = "found_at")]
)
class MatchedListing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "watch_id", nullable = false)
    var watchId: Long = 0,

    @Column(name = "watch_name", nullable = false)
    var watchName: String = "",

    @Column(name = "item_id", nullable = false)
    var itemId: Long = 0,

    @Column(nullable = false)
    var title: String = "",

    @Column(length = 32)
    var price: String? = null,

    var currency: String? = null,

    @Column(length = 1024)
    var url: String? = null,

    @Column(name = "thumbnail_url", length = 1024)
    var thumbnailUrl: String? = null,

    @Column(name = "found_at", nullable = false)
    var foundAt: Instant = Instant.now(),

    @Column(name = "notified")
    var notified: Boolean = false
)
