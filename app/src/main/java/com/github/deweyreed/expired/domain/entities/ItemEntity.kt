package com.github.deweyreed.expired.domain.entities

import java.time.LocalDate

data class ItemEntity(
    val id: Long = ID_NEW,
    val name: String,
    val count: Int = 1,
    val expiredTime: LocalDate,
) {
    companion object {
        const val ID_NEW = 0L
    }
}
