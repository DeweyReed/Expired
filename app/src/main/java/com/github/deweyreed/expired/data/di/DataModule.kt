package com.github.deweyreed.expired.data.di

import android.content.Context
import com.github.deweyreed.expired.data.db.ExpiredDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ExpiredDatabase {
        return ExpiredDatabase.getInstance(context)
    }
}
