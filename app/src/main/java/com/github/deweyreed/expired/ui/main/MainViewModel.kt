package com.github.deweyreed.expired.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.deweyreed.expired.domain.entities.ItemEntity
import com.github.deweyreed.expired.domain.repositories.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemRepo: ItemRepository
) : ViewModel() {
    val items: Flow<List<ItemEntity>> = itemRepo.getItemsFlow()

    fun addItem(item: ItemEntity) {
        viewModelScope.launch {
            itemRepo.addItem(item)
        }
    }
}
