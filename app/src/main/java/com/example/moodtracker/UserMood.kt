// TODO: Comment 1
// TODO: Comment 2
// TODO: Comment 3
// TODO: Comment 4
// TODO: Comment 5
// TODO: Comment 6
// TODO: Comment 7
// TODO: Comment 8
// TODO: Comment 9
// TODO: Comment 10
// TODO: Comment 11
// TODO: Comment 12
// TODO: Comment 13
// TODO: Comment 14
// TODO: Comment 15
// TODO: Comment 16
// TODO: Comment 17
// TODO: Comment 18
// TODO: Comment 19
// TODO: Comment 20
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

