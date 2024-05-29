package com.example.supersos.Utils

import java.text.SimpleDateFormat
import java.util.*

fun formatDateTime(dateTime: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormat.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    val date: Date = inputFormat.parse(dateTime)!!
    return outputFormat.format(date)
}
