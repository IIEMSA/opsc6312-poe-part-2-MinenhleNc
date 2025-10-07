package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class], version = 1)
abstract class UserProfileDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile private var INSTANCE: UserProfileDatabase? = null

        fun getDatabase(context: Context): UserProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserProfileDatabase::class.java,
                    "user_profile_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
