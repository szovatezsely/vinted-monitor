package com.vintedmonitor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "vinted")
class VintedProperties {
    lateinit var baseUrl: String
    lateinit var apiPath: String
    lateinit var userAgent: String
    var pollIntervalMs: Long = 900_000
    var perWatchDelayMs: Long = 2_500
}

@Configuration
@ConfigurationProperties(prefix = "notifications")
class NotificationProperties {
    var recipient: String = ""
    var from: String = ""
}

@Configuration
@ConfigurationProperties(prefix = "filter")
class FilterProperties {
    /**
     * When true (default), listings whose title contains letters unique to
     * Greek or Polish are dropped, so only Hungarian-friendly results remain.
     * Toggle from the Settings page or via the FILTER_HUNGARIAN_ONLY env var.
     */
    var hungarianOnly: Boolean = true
}
