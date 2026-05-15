package com.vintedmonitor.controller

import com.vintedmonitor.dto.MatchResponse
import com.vintedmonitor.service.WatchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/feed")
class FeedController(private val watchService: WatchService) {

    @GetMapping
    fun recent(@RequestParam(defaultValue = "50") limit: Int): List<MatchResponse> =
        watchService.recentMatches(limit.coerceIn(1, 500)).map(MatchResponse::from)
}
