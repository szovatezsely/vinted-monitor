package com.vintedmonitor.service

import com.vintedmonitor.config.VintedProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PollingScheduler(
    private val vintedClient: VintedClient,
    private val watchService: WatchService,
    private val emailService: EmailService,
    private val props: VintedProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Polls every [VintedProperties.pollIntervalMs] milliseconds.
     * Initial delay matches the interval so we don't slam Vinted on app startup.
     */
    @Scheduled(
        fixedRateString = "\${vinted.poll-interval-ms}",
        initialDelayString = "\${vinted.poll-interval-ms}"
    )
    fun tick() {
        val watches = watchService.listEnabled()
        if (watches.isEmpty()) {
            log.debug("No enabled watches; skipping tick.")
            return
        }
        log.info("Polling Vinted for {} watch(es).", watches.size)

        val allNew = mutableListOf<com.vintedmonitor.domain.MatchedListing>()
        for (w in watches) {
            try {
                val items = vintedClient.search(w.searchText)
                val fresh = watchService.recordNewMatches(w, items)
                watchService.markPolled(w)
                allNew.addAll(fresh)
            } catch (e: Exception) {
                log.warn("Watch '{}' failed: {}", w.name, e.message)
            }
            // Polite pacing between watches in a single tick.
            Thread.sleep(props.perWatchDelayMs)
        }

        if (allNew.isNotEmpty()) {
            emailService.sendDigest(allNew)
            watchService.markNotified(allNew)
        }
    }
}
