package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        binding.forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.loginButton.setOnClickListener {
            loginStudent()
        }
    }

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_forgot_password, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)

        builder.setView(dialogView)
            .setTitle("Reset Password")
            .setPositiveButton("Send") { dialog, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to send reset link: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
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
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener { document ->
                                binding.progressBar.visibility = View.GONE
                                if (document != null && (document.getString("role") == "student" || document.getString("role") == null)) {
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
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