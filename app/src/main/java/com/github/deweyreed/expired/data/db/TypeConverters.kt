package com.github.deweyreed.expired.data.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

internal class TimeConverters {
    @TypeConverter
    fun fromLocalDateTime(localDate: LocalDate): Long {
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun toLocalDate(time: Long): LocalDate {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
            .toLocalDate()
    }
}
