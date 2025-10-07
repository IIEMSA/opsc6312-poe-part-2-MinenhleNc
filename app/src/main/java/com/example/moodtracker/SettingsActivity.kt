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

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var ivProfile: ImageView
    private lateinit var btnUpload: Button
    private lateinit var btnCamera: Button
    private lateinit var btnEditProfile: Button
    private lateinit var btnLanguage: Button
    private lateinit var btnExportData: Button
    private lateinit var btnDeleteAll: Button
    private lateinit var btnNotifications: Button
    private lateinit var switchOfflineMode: SwitchCompat
    private lateinit var btnHelpSupport: Button
    private lateinit var btnAppInfo: Button

    private val PICK_IMAGE = 100
    private val TAKE_PHOTO = 101
    private var currentImageUri: Uri? = null

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val dao by lazy {
        UserProfilePictureDatabase.getDatabase(this).userProfilePictureDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Views
        ivProfile = findViewById(R.id.ivProfile)
        btnUpload = findViewById(R.id.btnUpload)
        btnCamera = findViewById(R.id.btnCamera)
        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnLanguage = findViewById(R.id.btnLanguage)
        btnExportData = findViewById(R.id.btnExportData)
        btnDeleteAll = findViewById(R.id.btnDeleteAll)
        btnNotifications = findViewById(R.id.btnNotifications)
        switchOfflineMode = findViewById(R.id.switchOfflineMode)
        btnHelpSupport = findViewById(R.id.btnHelpSupport)
        btnAppInfo = findViewById(R.id.btnAppInfo)

        // Load profile picture
        loadProfilePicture()

        // Profile picture actions
        btnUpload.setOnClickListener { pickImageFromGallery() }
        btnCamera.setOnClickListener { takePhoto() }

        // Navigation
        btnEditProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        btnLanguage.setOnClickListener { startActivity(Intent(this, LanguageActivity::class.java)) }
        btnNotifications.setOnClickListener { startActivity(Intent(this, NotificationSettingsActivity::class.java)) }
        btnHelpSupport.setOnClickListener { startActivity(Intent(this, HelpActivity::class.java)) }
        btnAppInfo.setOnClickListener { startActivity(Intent(this, AppInfoActivity::class.java)) }

        // Export / Delete
        btnExportData.setOnClickListener { confirmExportData() }
        btnDeleteAll.setOnClickListener { confirmDeleteAllData() }
    }

    private fun loadProfilePicture() {
        lifecycleScope.launch {
            try {
                val profile = dao.getProfilePicture(userId)
                profile?.imageUri?.let {
                    try { ivProfile.setImageURI(Uri.parse(it)) } catch (_: Exception) { }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            PICK_IMAGE -> data?.data?.let { uri ->
                currentImageUri = uri
                ivProfile.setImageURI(uri)
                saveProfilePicture(uri.toString())
            }
            TAKE_PHOTO -> (data?.extras?.get("data") as? Bitmap)?.let { bitmap ->
                val uri = bitmapToUri(bitmap)
                currentImageUri = uri
                ivProfile.setImageURI(uri)
                saveProfilePicture(uri.toString())
            }
        }
    }

    private fun saveProfilePicture(uri: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try { dao.insertOrUpdate(UserProfilePicture(userId, uri)) }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun bitmapToUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ProfilePic", null)
        return Uri.parse(path)
    }

    /*** Confirmation dialogs ***/
    private fun confirmExportData() {
        AlertDialog.Builder(this)
            .setTitle("Export Data")
            .setMessage("Do you want to export your mood data to a TXT file in Downloads?")
            .setPositiveButton("Yes") { _, _ -> exportData() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun confirmDeleteAllData() {
        AlertDialog.Builder(this)
            .setTitle("Delete All Data")
            .setMessage("Are you sure you want to delete all mood data? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ -> deleteAllData() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /*** Export / Delete logic ***/
    private fun exportData() {
        lifecycleScope.launch {
            try {
                val moodDao = UserMoodDatabase.getDatabase(this@SettingsActivity).userMoodDao()
                val moods = moodDao.getMoodsForUser(userId)
                if (moods.isEmpty()) {
                    Toast.makeText(this@SettingsActivity, "No mood data to export", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val sb = StringBuilder()
                moods.forEach { mood ->
                    sb.append("Mood: ${mood.selectedMood}\n")
                    sb.append("Location: ${mood.location}\n")
                    sb.append("Weather: ${mood.weather}\n")
                    sb.append("Main Note: ${mood.mainNote}\n")
                    sb.append("Additional Notes: ${mood.additionalNotes}\n")
                    sb.append("Timestamp: ${mood.timestamp}\n")
                    sb.append("--------------------\n")
                }

                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "MoodData_${System.currentTimeMillis()}.txt")
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
                }

                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let {
                    resolver.openOutputStream(it).use { outputStream: OutputStream? ->
                        outputStream?.write(sb.toString().toByteArray())
                    }
                    Toast.makeText(this@SettingsActivity, "Exported to Downloads folder", Toast.LENGTH_LONG).show()
                } ?: Toast.makeText(this@SettingsActivity, "Failed to create file", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SettingsActivity, "Export failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAllData() {
        lifecycleScope.launch {
            try {
                val moodDao = UserMoodDatabase.getDatabase(this@SettingsActivity).userMoodDao()
                moodDao.deleteAllForUser(userId)
                Toast.makeText(this@SettingsActivity, "All data deleted successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SettingsActivity, "Delete failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

