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

    /**
     * Diagnostic: when true, log the raw JSON of the first item in each search
     * response. Use it once to discover which location fields Vinted actually
     * returns, then turn it back off.
     */
    var logRawItems: Boolean = false
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
     * When true (default), listings from non-HUF sellers (i.e. abroad) are
     * dropped, so only Hungarian results remain. Toggle from the Settings page
     * or via the FILTER_HUNGARIAN_ONLY env var.
     */
    var hungarianOnly: Boolean = true
}
