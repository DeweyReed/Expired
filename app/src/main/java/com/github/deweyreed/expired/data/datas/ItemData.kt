package com.github.deweyreed.expired.data.datas

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Item")
data class ItemData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "count")
    val count: Int,

    @ColumnInfo(name = "expiredTime")
    val expiredTime: LocalDate,

    @ColumnInfo(name = "expiredTimeString")
    val expiredTimeString: String = expiredTime.toString(),

    @ColumnInfo(name = "hasConsumed")
    val hasConsumed: Boolean,
)
