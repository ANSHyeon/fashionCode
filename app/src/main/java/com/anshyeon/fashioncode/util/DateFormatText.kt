package com.anshyeon.fashioncode.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatText {

    private const val DATE_YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private val currentLocale
        get() = SystemConfiguration.currentLocale

    private fun convertToDate(dateString: String): Date {
        return SimpleDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN, currentLocale).parse(dateString)
            ?: Date()
    }

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

    fun getDefaultDatePattern(dateString: String): String {
        return dateString.replace("-", ".").replace("T", ". ").take(17)
    }

    fun getElapsedTime(dateString: String?): String {
        return dateString?.let { date ->
            val publishedDate = convertToDate(date)
            val currentDate = getCurrentDate()

            val seconds = (currentDate.time - publishedDate.time) / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val month = days / 30

            when {
                minutes < 1 -> "${seconds}초 전"

                hours < 1 -> "${minutes}분 전"

                days < 1 -> "${hours}시간 전"

                weeks < 1 -> "${days}일 전"

                month < 1 -> "${weeks}주 전"

                else -> "${month}달 전"
            }
        } ?: "값을 불러올 수 없습니다."
    }
}