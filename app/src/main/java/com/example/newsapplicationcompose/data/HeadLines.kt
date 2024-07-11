package com.example.newsapplicationcompose.data

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
class HeadLines {
    var title = ""
    var source: Source? = null
    var url = ""
    var urlToImage = ""
    var publishedAt = ""

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val date = sdf.parse(publishedAt)
        val currentDate = Date()

        val diff = currentDate.time - (date?.time ?: 0)
        val diffDays = TimeUnit.MILLISECONDS.toDays(diff)
        val diffHours = TimeUnit.MILLISECONDS.toHours(diff)

        return when {
            diffDays == 0L -> {
                when {
                    diffHours == 0L -> "Few minutes ago"
                    diffHours == 1L -> "1 hour ago"
                    else -> "$diffHours hours ago"
                }
            }
            diffDays == 1L -> "Yesterday"
            else -> "$diffDays days ago"
        }
    }
}
