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
