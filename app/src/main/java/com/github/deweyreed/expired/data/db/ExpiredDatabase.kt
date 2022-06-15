package com.github.deweyreed.expired.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.deweyreed.expired.data.datas.ItemData

@Database(
    entities = [
        ItemData::class
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [
        TimeConverters::class,
    ]
)
internal abstract class ExpiredDatabase : RoomDatabase() {

    abstract fun getItemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ExpiredDatabase? = null

        fun getInstance(context: Context): ExpiredDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: newInstance(context).also { INSTANCE = it }
            }
        }

        private fun newInstance(context: Context): ExpiredDatabase {
            return Room.databaseBuilder(context, ExpiredDatabase::class.java, "expired.db")
                .build()
        }
    }
}
