package com.vintedmonitor.service

import org.springframework.stereotype.Service

/**
 * Heuristic language gate for listing titles.
 *
 * Vinted (.hu) mixes in Greek and Polish listings. Both languages use letters
 * that simply do not occur in Hungarian, so the presence of any such letter is
 * a reliable "this is not a Hungarian listing" signal.
 *
 *  - Greek: the entire Greek/Coptic and Greek Extended Unicode blocks.
 *  - Polish: the Latin-with-diacritics letters unique to Polish.
 *
 * IMPORTANT: 'ó' is deliberately NOT treated as Polish — Hungarian uses 'ó'
 * too (along with 'ö'/'ő'), so flagging it would drop legitimate Hungarian
 * listings. Only Polish letters with no Hungarian counterpart are listed.
 */
@Service
class LanguageFilter {

    /** Letters that appear in Polish but never in Hungarian (note: 'ó' excluded). */
    private val polishOnlyLetters: Set<Char> = setOf(
        'ą', 'ć', 'ę', 'ł', 'ń', 'ś', 'ź', 'ż',
        'Ą', 'Ć', 'Ę', 'Ł', 'Ń', 'Ś', 'Ź', 'Ż'
    )

    /** True for any character in the Greek/Coptic or Greek Extended Unicode blocks. */
    private fun isGreek(ch: Char): Boolean {
        val c = ch.code
        return c in 0x0370..0x03FF ||   // Greek and Coptic
            c in 0x1F00..0x1FFF         // Greek Extended
    }

    /**
     * Returns true if [text] contains at least one letter that marks it as a
     * Greek or Polish listing. Null/blank text contains no such marker.
     */
    fun containsForeignLetters(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        return text.any { isGreek(it) || it in polishOnlyLetters }
    }
}
