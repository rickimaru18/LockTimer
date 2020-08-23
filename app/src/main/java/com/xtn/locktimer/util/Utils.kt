package com.xtn.locktimer.util

import java.util.*

object Utils {

    /**
     * Get if language is Japanese.
     *
     * @return TRUE if Japanese, FALSE otherwise.
     */
    fun isLanguageJapanese() = Locale.getDefault().language == "ja"

}