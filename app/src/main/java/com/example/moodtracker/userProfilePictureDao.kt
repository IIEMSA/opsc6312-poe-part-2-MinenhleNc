package com.example.moodtracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserProfilePictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(picture: UserProfilePicture)

    @Query("SELECT * FROM user_profile_pictures WHERE userId = :uid LIMIT 1")
    suspend fun getProfilePicture(uid: String): UserProfilePicture?
}
