package com.github.deweyreed.expired.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.github.deweyreed.expired.domain.repositories.PreferenceRepository
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Reusable
internal class PreferenceRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceRepository {

    override fun getBooleanFlow(key: String, default: Boolean): Flow<Boolean> {
        return context.dataStore.data.map { it[booleanPreferencesKey(key)] ?: default }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }
}
