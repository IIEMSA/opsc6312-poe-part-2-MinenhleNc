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

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoodLoggingActivity : AppCompatActivity() {

    private lateinit var moodGrid: GridLayout
    private lateinit var etAdditionalNotes: EditText
    private lateinit var btnSaveMood: Button
    private lateinit var btnBackHome: Button
    private lateinit var btnGoHistory: Button
    private lateinit var tvLocation: TextView
    private lateinit var tvWeather: TextView
    private lateinit var tvMainNote: TextView

    private var selectedMood: String? = null
    private var mainLocation = ""
    private var mainWeather = ""
    private var mainNote = ""

    private val moods = listOf(
        "Heartbroken", "Sick", "Tired", "Anxious", "Angry",
        "Very Sad", "Sad", "Exhausted",
        "In Love", "Neutral", "Loved", "Cool", "Good", "Happy", "Ecstatic"
    )

    // Room DB DAO
    private lateinit var moodDao: UserMoodDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_logging)

        // Initialize views
        moodGrid = findViewById(R.id.moodGridd)
        etAdditionalNotes = findViewById(R.id.etMoodNotes)
        btnSaveMood = findViewById(R.id.btnSaveMoodd)
        btnBackHome = findViewById(R.id.btnBackHomee)
        btnGoHistory = findViewById(R.id.btnGoHistory)
        tvLocation = findViewById(R.id.tvLocation)
        tvWeather = findViewById(R.id.tvWeather)
        tvMainNote = findViewById(R.id.tvHomeNotes)

        // Get MainActivity data
        mainLocation = intent.getStringExtra("location") ?: "Unknown"
        mainWeather = intent.getStringExtra("weather") ?: "Unknown"
        mainNote = intent.getStringExtra("notes") ?: ""

        tvLocation.text = mainLocation
        tvWeather.text = mainWeather
        tvMainNote.text = mainNote

        // Initialize Room DAO
        moodDao = UserMoodDatabase.getDatabase(this).userMoodDao()

        // Populate mood buttons
        createMoodButtons()

        // Button listeners
        btnSaveMood.setOnClickListener { saveMood() }
        btnBackHome.setOnClickListener { finish() }
        btnGoHistory.setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }
    }

    private fun createMoodButtons() {
        for (mood in moods) {
            val button = Button(this).apply {
                text = mood
                setOnClickListener {
                    selectedMood = mood
                    highlightSelected(this)
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            moodGrid.addView(button, params)
        }
    }

    private fun highlightSelected(selectedButton: Button) {
        for (i in 0 until moodGrid.childCount) {
            val btn = moodGrid.getChildAt(i) as Button
            btn.alpha = 1.0f
        }
        selectedButton.alpha = 0.6f
    }

    private fun saveMood() {
        if (selectedMood == null) {
            Toast.makeText(this, "Select a mood first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val additional = etAdditionalNotes.text.toString().trim()
        val timestamp = System.currentTimeMillis()

        val moodEntry = UserMood(
            userId = userId,
            location = mainLocation,
            weather = mainWeather,
            mainNote = mainNote,
            selectedMood = selectedMood!!,
            additionalNotes = additional,
            timestamp = timestamp
        )

        lifecycleScope.launch(Dispatchers.IO) {
            // Save to Room
            moodDao.insert(moodEntry)

            // Save to Firebase
            val ref = FirebaseDatabase.getInstance().getReference("user_moods").child(userId)
            val key = ref.push().key
            key?.let {
                ref.child(it).setValue(moodEntry)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@MoodLoggingActivity, "Mood logged successfully!", Toast.LENGTH_SHORT).show()
                etAdditionalNotes.text.clear()
                selectedMood = null
                // Reset button alpha
                for (i in 0 until moodGrid.childCount) {
                    val btn = moodGrid.getChildAt(i) as Button
                    btn.alpha = 1.0f
                }
            }
        }
    }
}

