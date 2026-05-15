package com.vintedmonitor.repository

import com.vintedmonitor.domain.AppSetting
import com.vintedmonitor.domain.MatchedListing
import com.vintedmonitor.domain.SeenItem
import com.vintedmonitor.domain.SeenItemKey
import com.vintedmonitor.domain.WatchedGame
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WatchedGameRepository : JpaRepository<WatchedGame, Long> {
    fun findAllByEnabledTrue(): List<WatchedGame>
}

@Repository
interface SeenItemRepository : JpaRepository<SeenItem, SeenItemKey> {

    @Query("select s.itemId from SeenItem s where s.watchId = :watchId and s.itemId in :itemIds")
    fun findExistingItemIds(
        @Param("watchId") watchId: Long,
        @Param("itemIds") itemIds: Collection<Long>
    ): List<Long>
}

@Repository
interface MatchedListingRepository : JpaRepository<MatchedListing, Long> {
    fun findAllByOrderByFoundAtDesc(pageable: Pageable): List<MatchedListing>
    fun findAllByWatchIdOrderByFoundAtDesc(watchId: Long, pageable: Pageable): List<MatchedListing>
}

@Repository
interface AppSettingRepository : JpaRepository<AppSetting, String>
