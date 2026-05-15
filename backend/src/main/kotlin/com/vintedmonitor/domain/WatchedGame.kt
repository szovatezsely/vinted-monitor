package com.vintedmonitor.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "watched_games")
class WatchedGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "search_text", nullable = false)
    var searchText: String = "",

    @Column(name = "max_price")
    var maxPrice: Int? = null,

    @Column(name = "min_price")
    var minPrice: Int? = null,

    var enabled: Boolean = true,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "last_polled_at")
    var lastPolledAt: Instant? = null
)
