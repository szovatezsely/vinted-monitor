package com.vintedmonitor.service

import com.vintedmonitor.config.NotificationProperties
import com.vintedmonitor.domain.MatchedListing
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val notificationProps: NotificationProperties,
    @Value("\${spring.mail.username:}") private val mailUsername: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun isConfigured(): Boolean =
        mailUsername.isNotBlank() && notificationProps.recipient.isNotBlank()

    fun sendDigest(matches: List<MatchedListing>) {
        if (matches.isEmpty()) return
        if (!isConfigured()) {
            log.warn("Skipping email: mail credentials or recipient not configured.")
            return
        }

        val byWatch = matches.groupBy { it.watchName }
        val subject = if (byWatch.size == 1) {
            "Vinted: ${matches.size} új találat — ${byWatch.keys.first()}"
        } else {
            "Vinted: ${matches.size} új találat (${byWatch.size} keresés)"
        }

        val body = buildString {
            byWatch.forEach { (watchName, items) ->
                appendLine("== $watchName (${items.size}) ==")
                items.forEach { m ->
                    val priceLabel = listOfNotNull(m.price, m.currency).joinToString(" ")
                    appendLine("• ${m.title}")
                    if (priceLabel.isNotBlank()) appendLine("  Ár: $priceLabel")
                    if (m.url != null) appendLine("  ${m.url}")
                    appendLine()
                }
            }
        }

        val message = SimpleMailMessage().apply {
            from = notificationProps.from.ifBlank { mailUsername }
            setTo(notificationProps.recipient)
            this.subject = subject
            this.text = body
        }

        try {
            mailSender.send(message)
            log.info("Sent digest with {} matches to {}", matches.size, notificationProps.recipient)
        } catch (e: Exception) {
            log.error("Failed to send digest email: {}", e.message, e)
        }
    }
}
