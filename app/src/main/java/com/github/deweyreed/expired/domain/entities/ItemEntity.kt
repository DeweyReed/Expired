package com.github.deweyreed.expired.domain.entities

import java.time.LocalDateTime

data class ItemEntity(
    val id: Long,
    val name: String,
    val expiredTime: LocalDateTime,
)
