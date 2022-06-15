package com.github.deweyreed.expired.data.repositories

import com.github.deweyreed.expired.data.db.ExpiredDatabase
import com.github.deweyreed.expired.data.db.ItemDao
import com.github.deweyreed.expired.data.mappers.ItemMapper
import com.github.deweyreed.expired.data.mappers.fromWithMapper
import com.github.deweyreed.expired.data.mappers.toWithMapper
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.repositories.ItemRepository
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
internal class ItemRepoImpl @Inject constructor(
    database: ExpiredDatabase,
    private val itemMapper: ItemMapper,
) : ItemRepository {

    private val dao: ItemDao = database.getItemDao()

    override fun getItemsFlow(): Flow<List<ItemEntity>> {
        return dao.getFlow().map { it.fromWithMapper(itemMapper) }
    }

    override suspend fun addItem(item: ItemEntity) {
        dao.insert(item.toWithMapper(itemMapper))
    }

    override suspend fun deleteItem(item: ItemEntity) {
        dao.delete(item.toWithMapper(itemMapper))
    }
}
