package com.example.moodtracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserNotesDao {
    @Insert
    suspend fun insert(note: UserNote)

    @Query("SELECT * FROM user_notes WHERE userId = :uid ORDER BY id DESC")
    suspend fun getNotesForUser(uid: String): List<UserNote>
}
