package com.ignitedminds.blockit.data.local.sql.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ignitedminds.blockit.data.local.sql.dao.LinksDao
import com.ignitedminds.blockit.data.local.sql.entities.LinkData

@Database(entities = [LinkData::class], version = 2, exportSchema = false)
abstract class LinksDatabase : RoomDatabase() {

    abstract fun getLinksDao(): LinksDao

    companion object {
        @Volatile
        private var INSTANCE: LinksDatabase? = null

        fun getDatabase(context: Context): LinksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LinksDatabase::class.java,
                    "links_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}