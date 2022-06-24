package com.github.deweyreed.expired.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getBooleanFlow(key: String, default: Boolean = false): Flow<Boolean>
    suspend fun setBoolean(key: String, value: Boolean)
}
