package com.github.deweyreed.expired.domain.utils

import android.content.Context
import android.text.format.DateUtils
import java.time.LocalDate
import java.time.ZoneId

fun LocalDate.prettify(context: Context): String {
    return DateUtils.formatDateTime(
        context,
        atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli(),
        DateUtils.FORMAT_SHOW_YEAR or
            DateUtils.FORMAT_SHOW_DATE
    )
}
