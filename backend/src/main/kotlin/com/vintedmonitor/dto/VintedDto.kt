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
    @JsonProperty("brand_title") val brandTitle: String? = null
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
