package com.example.td2_exo7

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Seance::class), version = 1, exportSchema = false)
abstract class SeanceRoomDatabase : RoomDatabase() {

    abstract fun seanceDao(): SeanceDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SeanceRoomDatabase? = null

        fun getDatabase(context: Context): SeanceRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeanceRoomDatabase::class.java,
                    "seance_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}