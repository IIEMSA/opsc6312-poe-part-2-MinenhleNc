package com.example.moodtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_notes")
data class UserNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val location: String,
    val weather: String,
    val temperature: Double,
    val note: String
)
