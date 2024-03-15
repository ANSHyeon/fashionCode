package com.anshyeon.fashioncode.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatText {

    private const val DATE_YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private val currentLocale
        get() = SystemConfiguration.currentLocale

    fun getCurrentTime(): String {
        return applyDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN)
    }

    private fun getCurrentDate(): Date {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
    }

    private fun applyDateFormat(pattern: String): String {
        val formatter = SimpleDateFormat(pattern, currentLocale)
        val currentDate = getCurrentDate()
        return formatter.format(currentDate)
    }
}