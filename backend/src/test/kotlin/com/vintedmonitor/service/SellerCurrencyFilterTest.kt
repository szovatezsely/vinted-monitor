package com.vintedmonitor.service

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SellerCurrencyFilterTest {

    private val filter = SellerCurrencyFilter()

    @Test
    fun `non-HUF seller currency is foreign`() {
        assertTrue(filter.isForeignSellerCurrency("PLN")) // Poland
        assertTrue(filter.isForeignSellerCurrency("RON")) // Romania
        assertTrue(filter.isForeignSellerCurrency("EUR")) // eurozone (e.g. Greece)
        assertTrue(filter.isForeignSellerCurrency("pln")) // case-insensitive
    }

    @Test
    fun `HUF or missing seller currency is domestic`() {
        assertFalse(filter.isForeignSellerCurrency("HUF"))
        assertFalse(filter.isForeignSellerCurrency("huf"))
        assertFalse(filter.isForeignSellerCurrency(null)) // no conversion block
        assertFalse(filter.isForeignSellerCurrency(""))
        assertFalse(filter.isForeignSellerCurrency("   "))
    }
}
