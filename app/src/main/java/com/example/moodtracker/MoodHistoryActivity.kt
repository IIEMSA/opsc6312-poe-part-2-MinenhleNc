package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var rvMoodHistory: RecyclerView
    private lateinit var btnLogout: Button
    private lateinit var btnBackHome: Button
    private lateinit var adapter: MoodAdapter
    private val moodList = mutableListOf<UserMood>()

    // Room DAO
    private lateinit var moodDao: UserMoodDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_history)

        rvMoodHistory = findViewById(R.id.rvMoodHistory)
        btnLogout = findViewById(R.id.btnLogout)
        btnBackHome = findViewById(R.id.btnBackHome)

        adapter = MoodAdapter(moodList)
        rvMoodHistory.layoutManager = LinearLayoutManager(this)
        rvMoodHistory.adapter = adapter

        // Initialize Room DAO
        moodDao = UserMoodDatabase.getDatabase(this).userMoodDao()

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnBackHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        fetchMoodHistory()
    }

    private fun fetchMoodHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val moods = moodDao.getMoodsForUser(userId)
                    .sortedByDescending { it.timestamp } // most recent first

                withContext(Dispatchers.Main) {
                    moodList.clear()
                    moodList.addAll(moods)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MoodHistoryActivity, "Failed to load history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
