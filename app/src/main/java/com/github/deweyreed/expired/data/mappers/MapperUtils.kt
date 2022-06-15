package com.github.deweyreed.expired.data.mappers

import com.github.deweyreed.expired.domain.utils.Mapper

internal fun <Data, Entity> Data.fromWithMapper(mapper: Mapper<Data, Entity>): Entity =
    mapper.mapFrom(this)

internal fun <Data, Entity> List<Data>.fromWithMapper(mapper: Mapper<Data, Entity>): List<Entity> =
    mapper.mapFrom(this)

internal fun <Entity, Data> Data.toWithMapper(mapper: Mapper<Entity, Data>): Entity =
    mapper.mapTo(this)

internal fun <Entity, Data> List<Data>.toWithMapper(mapper: Mapper<Entity, Data>): List<Entity> =
    mapper.mapTo(this)
