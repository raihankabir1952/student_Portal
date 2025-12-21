package com.example.login_page

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.login_page.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE)

        setupThemeSwitch()
        loadWelcomeMessage()

        binding.myProfileButton.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }

        binding.myCourseButton.setOnClickListener {
            startActivity(Intent(this, MyCourseActivity::class.java))
        }

        binding.courseRegistrationButton.setOnClickListener {
            startActivity(Intent(this, CourseRegistrationActivity::class.java))
        }

        binding.noticeBoardButton.setOnClickListener {
            startActivity(Intent(this, NoticeBoardActivity::class.java))
        }

        binding.classRoutineButton.setOnClickListener {
            startActivity(Intent(this, ClassRoutineActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadWelcomeMessage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        binding.titleText.text = "Welcome, $name!"
                    }
                }
        }
    }

    private fun setupThemeSwitch() {
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
        binding.themeSwitch.isChecked = isDarkMode

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            sharedPreferences.edit().putBoolean("is_dark_mode", isChecked).apply()
        }
    }
}