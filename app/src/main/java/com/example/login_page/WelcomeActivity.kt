package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val address = document.getString("address")
                        val phone = document.getString("phone")
                        val department = document.getString("department")
                        val semester = document.getString("semester")
                        val age = document.getString("age")
                        val gender = document.getString("gender")

                        binding.nameText.text = name
                        binding.emailText.text = email
                        binding.addressText.text = address
                        binding.phoneText.text = phone
                        binding.departmentText.text = department
                        binding.semesterText.text = semester
                        binding.ageText.text = age
                        binding.genderText.text = gender

                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}