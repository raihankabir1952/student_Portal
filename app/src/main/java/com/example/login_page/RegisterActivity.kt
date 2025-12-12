package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val address = binding.address.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        val department = binding.department.text.toString().trim()
        val semester = binding.semester.text.toString().trim()
        val age = binding.age.text.toString().trim()

        val selectedGenderId = binding.gender.checkedRadioButtonId
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty() || department.isEmpty() || semester.isEmpty() || age.isEmpty() || selectedGenderId == -1) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "address" to address,
                            "phone" to phone,
                            "department" to department,
                            "semester" to semester,
                            "age" to age,
                            "gender" to gender,
                            "role" to "student" // Assign role as student
                        )

                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener { 
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(baseContext, "Registration successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, DashboardActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(baseContext, "Error saving user details: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        baseContext, "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}