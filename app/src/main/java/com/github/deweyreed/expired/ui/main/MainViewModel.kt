package com.github.deweyreed.expired.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.repositories.ItemRepository
import com.github.deweyreed.expired.domain.repositories.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemRepo: ItemRepository,
    private val preferenceRepo: PreferenceRepository,
) : ViewModel() {
    val items: Flow<List<ItemEntity>> = itemRepo.getItemsFlow()
    val showRemainingTime: Flow<Boolean> =
        preferenceRepo.getBooleanFlow(PREF_KEY_SHOW_REMAINING_TIME, default = true)

    fun addOrUpdateItem(item: ItemEntity) {
        viewModelScope.launch {
            itemRepo.addOrUpdateItem(item)
        }
    }

    fun consumeItem(item: ItemEntity) {
        viewModelScope.launch {
            if (item.count > 1) {
                itemRepo.addOrUpdateItem(item.copy(count = item.count - 1))
            } else {
                itemRepo.deleteItem(item)
            }
        }
    }

    fun changeShowRemainingTime(value: Boolean) {
        viewModelScope.launch {
            preferenceRepo.setBoolean(PREF_KEY_SHOW_REMAINING_TIME, value)
        }
    }

    companion object {
        private const val PREF_KEY_SHOW_REMAINING_TIME = "show_remaining_time"
    }
}
