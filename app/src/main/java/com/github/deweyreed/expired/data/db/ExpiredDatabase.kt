package com.github.deweyreed.expired.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.github.deweyreed.expired.data.datas.ItemData

@Database(
    entities = [
        ItemData::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = ExpiredDatabase.AutoMigrationSpec1To2::class),
    ],
)
@TypeConverters(
    value = [
        TimeConverters::class,
    ]
)
internal abstract class ExpiredDatabase : RoomDatabase() {

    abstract fun getItemDao(): ItemDao

    @DeleteColumn(tableName = "Item", columnName = "hasConsumed")
    class AutoMigrationSpec1To2 : AutoMigrationSpec

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
