package com.example.moodtracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfilePicture::class], version = 1)
abstract class UserProfilePictureDatabase : RoomDatabase() {
    abstract fun userProfilePictureDao(): UserProfilePictureDao

    companion object {
        @Volatile
        private var INSTANCE: UserProfilePictureDatabase? = null

        fun getDatabase(context: Context): UserProfilePictureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserProfilePictureDatabase::class.java,
                    "user_profile_pictures_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
