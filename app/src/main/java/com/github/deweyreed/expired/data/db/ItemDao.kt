package com.github.deweyreed.expired.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.deweyreed.expired.data.datas.ItemData
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ItemDao {
    @Query("SELECT * FROM Item ORDER BY expiredTime")
    fun getFlow(): Flow<List<ItemData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemData)

    @Delete
    suspend fun delete(item: ItemData)
}
