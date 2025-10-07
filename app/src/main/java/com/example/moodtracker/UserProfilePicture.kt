package com.example.moodtracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile_pictures")
data class UserProfilePicture(
    @PrimaryKey val userId: String,
    val imageUri: String
)
