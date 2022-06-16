package com.github.deweyreed.expired.domain.repositories

import com.github.deweyreed.expired.domain.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsFlow(): Flow<List<ItemEntity>>
    suspend fun addOrUpdateItem(item: ItemEntity)
    suspend fun deleteItem(item: ItemEntity)
}
