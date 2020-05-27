package com.example.secret.palpatine.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

public fun getDaysSinceMoment(moment: LocalDateTime): String {
    val now: LocalDateTime = LocalDateTime.now()
    return Duration.between(moment,now).toDays().toString()
}

public fun getHoursSinceMoment(moment: LocalDateTime): String {
    val now: LocalDateTime = LocalDateTime.now()
    return Duration.between(moment,now).toHours().toString()
}

public fun getMinutesSinceMoment(moment: LocalDateTime): String {
    val now: LocalDateTime = LocalDateTime.now()
    return Duration.between(moment,now).toMinutes().toString()
}

public fun getPassedTimeFormatted(moment: LocalDateTime): String {

    val now: LocalDateTime = LocalDateTime.now()
    val days = Duration.between(moment,now).toDays()

    if (days <= 1L){

        val hours = Duration.between(moment,now).toHours()
        if (hours <= 1L){ return Duration.between(moment,now).toMinutes().toString() + "min ago" }
        else { return hours.toString() + "h ago"  }

    } else { return days.toString() + "d ago"  }
}