package com.vintedmonitor.service

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LanguageFilterTest {

    private val filter = LanguageFilter()

    @Test
    fun `plain hungarian titles are kept`() {
        // Includes Hungarian-specific accents: á é í ó ö ő ú ü ű
        assertFalse(filter.containsForeignLetters("Catan társasjáték új állapotban"))
        assertFalse(filter.containsForeignLetters("Gyűrűk Ura kártyajáték őszi árú"))
        assertFalse(filter.containsForeignLetters("Plain ASCII board game"))
    }

    @Test
    fun `the shared letter o-acute does not count as polish`() {
        // 'ó' exists in both Hungarian and Polish — must NOT trigger the filter,
        // otherwise legitimate Hungarian listings get dropped.
        assertFalse(filter.containsForeignLetters("Diplomáció óra"))
    }

    @Test
    fun `greek titles are dropped`() {
        assertTrue(filter.containsForeignLetters("Επιτραπέζιο παιχνίδι"))
        assertTrue(filter.containsForeignLetters("Catan ελληνικά"))
    }

    @Test
    fun `polish-only letters are dropped`() {
        // ł, ż, ę, ą, ś, ć, ń, ź — none occur in Hungarian.
        assertTrue(filter.containsForeignLetters("Gra planszowa wysyłka")) // ł
        assertTrue(filter.containsForeignLetters("Książę używana"))        // ą ż ę
        assertTrue(filter.containsForeignLetters("świetna gra"))           // ś
    }

    @Test
    fun `null or blank titles contain no foreign letters`() {
        assertFalse(filter.containsForeignLetters(null))
        assertFalse(filter.containsForeignLetters(""))
        assertFalse(filter.containsForeignLetters("   "))
    }
}
