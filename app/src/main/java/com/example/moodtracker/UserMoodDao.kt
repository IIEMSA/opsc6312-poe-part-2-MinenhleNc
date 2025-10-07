package com.example.moodtracker

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserMoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mood: UserMood)

    @Query("SELECT * FROM user_moods WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getMoodsForUser(userId: String): List<UserMood>

    @Query("DELETE FROM user_moods WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
