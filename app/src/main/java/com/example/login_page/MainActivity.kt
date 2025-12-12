package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.signUpText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.facultyLoginText.setOnClickListener {
            startActivity(Intent(this, FacultyLoginActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            loginStudent()
        }
    }

    private fun loginStudent() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Check if the user has the 'student' role in Firestore
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                binding.progressBar.visibility = View.GONE
                                if (document != null && (document.getString("role") == "student" || document.getString("role") == null)) {
                                    // User is a student (or role is not defined, for older users), proceed to student dashboard
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    // Not a student user, sign them out
                                    auth.signOut()
                                    Toast.makeText(baseContext, "Faculty accounts must use the 'Login as Faculty' option.", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                binding.progressBar.visibility = View.GONE
                                auth.signOut()
                                Toast.makeText(baseContext, "Error checking user role: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}