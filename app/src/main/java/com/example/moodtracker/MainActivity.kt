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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_REQUEST = 100
    }

    private lateinit var tvGreeting: TextView
    private lateinit var tvWelcomeName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvWeather: TextView
    private lateinit var tvNote: TextView
    private lateinit var etCity: EditText
    private lateinit var etMoodNotes: EditText
    private lateinit var btnUseCity: Button
    private lateinit var btnSaveNote: Button
    private lateinit var btnMoodLogging: Button
    private lateinit var btnHistory: Button
    private lateinit var btnSettings: Button

    private lateinit var userProfileDao: UserProfileDao
    private lateinit var notesDao: UserNotesDao

    private var currentLocation: String = ""
    private var currentWeather: String = ""
    private var currentTemp: Double = 0.0

    private val apiKey by lazy { getString(R.string.weather_api_key) }

    // Firebase
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvWelcomeName = findViewById(R.id.tvWelcomeName)
        tvLocation = findViewById(R.id.tvLocation)
        tvWeather = findViewById(R.id.tvWeather)
        tvNote = findViewById(R.id.tvNote)
        etCity = findViewById(R.id.etCity)
        etMoodNotes = findViewById(R.id.etMoodNotes)
        btnUseCity = findViewById(R.id.btnUseCity)
        btnSaveNote = findViewById(R.id.btnSaveNote)
        btnMoodLogging = findViewById(R.id.btnMoodLogging)
        btnHistory = findViewById(R.id.btnHistory)
        btnSettings = findViewById(R.id.btnSettings)

        // Initialize DAOs
        userProfileDao = UserProfileDatabase.getDatabase(this).userProfileDao()
        notesDao = UserNotesDatabase.getDatabase(this).userNotesDao()

        // Fetch user's first name for greeting
        val currentUserId = auth.currentUser?.uid ?: "unknown"
        lifecycleScope.launch(Dispatchers.IO) {
            val profile = userProfileDao.getProfile(currentUserId)
            val fullName = profile?.name ?: "User"
            val firstName = fullName.split(" ").firstOrNull() ?: fullName
            withContext(Dispatchers.Main) {
                tvGreeting.text = "Hello Sunshine 🌤️"
                tvWelcomeName.text = "Welcome back, $firstName"
            }
        }

        // Request location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST
            )
        }

        // Manual city input
        btnUseCity.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter a city!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentLocation = city
            tvLocation.text = city
            fetchWeather(city = city)
        }

        // Save mood note
        btnSaveNote.setOnClickListener {
            val notes = etMoodNotes.text.toString().trim()
            if (notes.isEmpty()) {
                Toast.makeText(this, "Enter a note first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvNote.text = notes // show in TextView

            // Save to RoomDB and Firebase
            lifecycleScope.launch(Dispatchers.IO) {
                val note = UserNote(
                    userId = currentUserId,
                    location = currentLocation,
                    weather = currentWeather,
                    temperature = currentTemp,
                    note = notes
                )
                notesDao.insert(note)
                db.child("user_notes").child(currentUserId).push().setValue(note)
            }

            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
            etMoodNotes.text.clear()
        }

        // Navigate to MoodLoggingActivity
        btnMoodLogging.setOnClickListener {
            val notes = tvNote.text.toString()
            val intent = Intent(this, MoodLoggingActivity::class.java).apply {
                putExtra("location", currentLocation)
                putExtra("weather", currentWeather)
                putExtra("notes", notes)
            }
            startActivity(intent)
        }

        // Navigate to MoodHistoryActivity
        btnHistory.setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }

        // Navigate to SettingsActivity
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }




        // Apply gradient background to buttons
        listOf(btnUseCity, btnSaveNote, btnMoodLogging, btnHistory, btnSettings).forEach { btn ->
            btn.background = getDrawable(R.drawable.gradient_button)
        }
    }

    private fun getLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                // Convert lat/lon to city name using OpenWeather API
                fetchWeather(lat = loc.latitude, lon = loc.longitude)
            } else {
                tvLocation.text = "Location unavailable"
            }
        }
    }

    private fun fetchWeather(city: String? = null, lat: Double? = null, lon: Double? = null) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherAPI::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = when {
                    city != null -> service.getByCity(city, apiKey, "metric")
                    lat != null && lon != null -> service.getByCoords(lat, lon, apiKey, "metric")
                    else -> null
                }
                response?.let {
                    withContext(Dispatchers.Main) {
                        currentLocation = it.name // Use city name
                        tvLocation.text = currentLocation
                        tvWeather.text = "${it.weather[0].main}, ${it.main.temp}°C"
                        currentWeather = it.weather[0].main
                        currentTemp = it.main.temp
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Weather fetch failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }
}

// Retrofit API
interface WeatherAPI {
    @GET("weather")
    suspend fun getByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherResponse

    @GET("weather")
    suspend fun getByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherResponse
}

data class WeatherResponse(val name: String, val main: Main, val weather: List<WeatherItem>)
data class Main(val temp: Double)
data class WeatherItem(val main: String)

