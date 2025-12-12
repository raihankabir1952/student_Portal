package com.example.login_page

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
            // Clear the activity stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}