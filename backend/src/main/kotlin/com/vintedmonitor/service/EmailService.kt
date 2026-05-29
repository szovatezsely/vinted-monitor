package com.vintedmonitor.service

import com.vintedmonitor.config.NotificationProperties
import com.vintedmonitor.domain.MatchedListing
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
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

        try {
            // Multipart/alternative: HTML (with bold topic headers) + plain-text fallback.
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")
            helper.setFrom(notificationProps.from.ifBlank { mailUsername })
            helper.setTo(notificationProps.recipient)
            helper.setSubject(subject)
            helper.setText(buildPlainBody(byWatch), buildHtmlBody(byWatch))
            mailSender.send(message)
            log.info("Sent digest with {} matches to {}", matches.size, notificationProps.recipient)
        } catch (e: Exception) {
            log.error("Failed to send digest email: {}", e.message, e)
        }
    }

    /** Plain-text fallback — identical to the original digest format. */
    private fun buildPlainBody(byWatch: Map<String, List<MatchedListing>>): String = buildString {
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

    /** HTML version — the per-watch topic header is rendered in bold. */
    private fun buildHtmlBody(byWatch: Map<String, List<MatchedListing>>): String = buildString {
        append("<div style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#111;\">")
        byWatch.forEach { (watchName, items) ->
            append("<p style=\"margin:16px 0 4px;\"><strong>")
            append(esc("$watchName (${items.size})"))
            append("</strong></p>")
            append("<ul style=\"margin:0;padding-left:18px;\">")
            items.forEach { m ->
                val priceLabel = listOfNotNull(m.price, m.currency).joinToString(" ")
                append("<li style=\"margin-bottom:10px;\">")
                append(esc(m.title))
                if (priceLabel.isNotBlank()) append("<br>Ár: ").append(esc(priceLabel))
                val url = m.url
                if (url != null) {
                    val u = esc(url)
                    append("<br><a href=\"").append(u).append("\">").append(u).append("</a>")
                }
                append("</li>")
            }
            append("</ul>")
        }
        append("</div>")
    }

    /** Escape text before embedding it in HTML (titles/URLs are untrusted). */
    private fun esc(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}
