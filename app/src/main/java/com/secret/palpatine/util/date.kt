package com.secret.palpatine.util

import com.google.firebase.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

fun getPassedTimeFormatted(timestamp: Timestamp): String {
    val moment = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val now: LocalDateTime = LocalDateTime.now()
    val days = Duration.between(moment, now).toDays()

    if (days < 1L) {
        val hours = Duration.between(moment, now).toHours()
        if (hours < 1L) {
            return Duration.between(moment, now).toMinutes().toString() + "min ago"
        } else {
            return hours.toString() + "h ago"
        }
    } else {
        return days.toString() + "d ago"
    }
}