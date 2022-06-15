package com.github.deweyreed.expired.data.di

import com.github.deweyreed.expired.data.repositories.ItemRepoImpl
import com.github.deweyreed.expired.domain.repositories.ItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepoModule {
    @Binds
    abstract fun bindItemRepo(impl: ItemRepoImpl): ItemRepository
}
