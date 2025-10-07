package com.example.moodtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_moods")
data class UserMood(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val selectedMood: String,
    val location: String,
    val weather: String,
    val mainNote: String,
    val additionalNotes: String?,
    val timestamp: Long
)
