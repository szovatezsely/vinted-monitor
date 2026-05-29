package com.vintedmonitor.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Subset of the Vinted catalog/items JSON response we care about.
 * Vinted's API is not public — fields may change. We tolerate unknown fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VintedSearchResponse(
    val items: List<VintedItem> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VintedItem(
    val id: Long,
    val title: String?,
    val price: VintedPrice?,
    val url: String?,
    val photo: VintedPhoto?,
    @JsonProperty("brand_title") val brandTitle: String? = null,
    // Currency-conversion block. Vinted includes it only when the seller's currency
    // differs from the buyer's (always HUF on vinted.hu) — i.e. a cross-border
    // listing. It's the most reliable "which country is the seller in" signal the
    // catalog response gives us; there is no city/country field here.
    val conversion: VintedConversion? = null
) {
    /** Seller's currency if Vinted included a conversion block, else null. */
    fun sellerCurrency(): String? = conversion?.sellerCurrency?.takeIf { it.isNotBlank() }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class VintedConversion(
    @JsonProperty("seller_currency") val sellerCurrency: String? = null,
    @JsonProperty("buyer_currency") val buyerCurrency: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VintedPrice(
    val amount: String?,
    @JsonProperty("currency_code") val currencyCode: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VintedPhoto(
    val url: String?
)
