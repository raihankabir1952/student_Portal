package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2000) // 2 second delay
    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No user is logged in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // User is logged in, check their role
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        if (role == "faculty") {
                            // Go to FacultyDashboardActivity
                            startActivity(Intent(this, FacultyDashboardActivity::class.java))
                        } else {
                            // Go to Student Dashboard (DashboardActivity)
                            startActivity(Intent(this, DashboardActivity::class.java))
                        }
                    } else {
                        // Document doesn't exist, treat as student (or handle as an error)
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    finish()
                }
                .addOnFailureListener { 
                    // Failed to get role, default to student dashboard
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
        }
    }
}