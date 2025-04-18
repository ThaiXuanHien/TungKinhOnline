package com.hienthai.tungkinhonline

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {
    fun convertDateToTime(
        date: Date,
        format: String,
        timeZone: TimeZone? = TimeZone.getDefault(),
        locale: Locale = Locale.getDefault()
    ): String {
        return try {
            val dateFormat = SimpleDateFormat(format, locale)
            timeZone?.let {
                dateFormat.timeZone = timeZone
            }
            dateFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}

object TimeFormat {
    const val YYYYMMDD = "yyyyMMdd"
    const val YYYYMMDDHHMM = "yyyyMMdd : hh:mm"
}