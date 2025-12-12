package com.example.login_page

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityFacultyDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class FacultyDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.manageNoticeButton.setOnClickListener {
            startActivity(Intent(this, ManageNoticesActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}