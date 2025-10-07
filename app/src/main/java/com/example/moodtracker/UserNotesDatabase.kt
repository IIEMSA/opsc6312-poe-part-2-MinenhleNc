package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserNote::class], version = 1)
abstract class UserNotesDatabase : RoomDatabase() {
    abstract fun userNotesDao(): UserNotesDao

    companion object {
        @Volatile private var INSTANCE: UserNotesDatabase? = null

        fun getDatabase(context: Context): UserNotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserNotesDatabase::class.java,
                    "user_notes"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
