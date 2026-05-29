package com.vintedmonitor.service

import org.springframework.stereotype.Service

/**
 * Decides whether a Vinted listing comes from a foreign seller, based on currency.
 *
 * vinted.hu prices everything in HUF and only attaches a conversion block (carrying
 * the seller's own currency) when the seller's currency differs. So any non-HUF
 * seller currency (PLN = Poland, RON = Romania, EUR = eurozone such as Greece, …)
 * means the seller is abroad; a HUF seller (no conversion block) is domestic.
 */
@Service
class SellerCurrencyFilter {

    private val domesticCurrency = "HUF"

    /** True when [sellerCurrency] indicates the seller is abroad (non-HUF). */
    fun isForeignSellerCurrency(sellerCurrency: String?): Boolean {
        if (sellerCurrency.isNullOrBlank()) return false
        return !sellerCurrency.trim().equals(domesticCurrency, ignoreCase = true)
    }
}
