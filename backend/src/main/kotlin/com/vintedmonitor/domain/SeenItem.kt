package com.vintedmonitor.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "seen_items")
@IdClass(SeenItemKey::class)
class SeenItem(
    @Id
    @Column(name = "watch_id")
    var watchId: Long = 0,

    @Id
    @Column(name = "item_id")
    var itemId: Long = 0,

    @Column(name = "first_seen_at", nullable = false)
    var firstSeenAt: Instant = Instant.now()
)

data class SeenItemKey(
    var watchId: Long = 0,
    var itemId: Long = 0
) : Serializable
