package com.github.deweyreed.expired.data.mappers

import com.github.deweyreed.expired.data.datas.ItemData
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.utils.Mapper
import dagger.Reusable
import javax.inject.Inject

@Reusable
internal class ItemMapper @Inject constructor() : Mapper<ItemData, ItemEntity>() {
    override fun mapFrom(from: ItemData): ItemEntity {
        return ItemEntity(
            id = from.id,
            name = from.name,
            count = from.count,
            expiredTime = from.expiredTime,
            hasConsumed = from.hasConsumed,
        )
    }

    override fun mapTo(from: ItemEntity): ItemData {
        return ItemData(
            id = from.id,
            name = from.name,
            count = from.count,
            expiredTime = from.expiredTime,
            hasConsumed = from.hasConsumed,
        )
    }
}
