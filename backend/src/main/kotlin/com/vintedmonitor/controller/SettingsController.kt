package com.vintedmonitor.controller

import com.vintedmonitor.config.NotificationProperties
import com.vintedmonitor.config.VintedProperties
import com.vintedmonitor.dto.SettingsResponse
import com.vintedmonitor.dto.SettingsUpdate
import com.vintedmonitor.service.EmailService
import com.vintedmonitor.service.PollingScheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/settings")
class SettingsController(
    private val notificationProps: NotificationProperties,
    private val vintedProps: VintedProperties,
    private val emailService: EmailService,
    private val pollingScheduler: PollingScheduler,
    @Value("\${spring.mail.username:}") private val mailUsername: String
) {

    @GetMapping
    fun get(): SettingsResponse = SettingsResponse(
        recipient = notificationProps.recipient,
        from = notificationProps.from.ifBlank { mailUsername },
        mailConfigured = emailService.isConfigured(),
        pollIntervalMs = vintedProps.pollIntervalMs
    )

    @PutMapping
    fun update(@RequestBody update: SettingsUpdate): SettingsResponse {
        update.recipient?.let { notificationProps.recipient = it.trim() }
        return get()
    }

    @PostMapping("/poll-now")
    fun pollNow(): Map<String, String> {
        pollingScheduler.tick()
        return mapOf("status" to "ok")
    }
}
