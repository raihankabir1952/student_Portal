package com.example.login_page

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

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

        setupBloodGroupSpinner()
        setupDatePicker()

        binding.registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun setupBloodGroupSpinner() {
        val bloodGroups = arrayOf("Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups)
        binding.bloodGroupSpinner.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.dobText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.dobText.text = selectedDate
                },
                year, month, day
            )
            datePickerDialog.show()
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
        val dob = binding.dobText.text.toString().trim()
        val bloodGroup = binding.bloodGroupSpinner.selectedItem.toString()

        val selectedGenderId = binding.gender.checkedRadioButtonId
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty() || 
            department.isEmpty() || semester.isEmpty() || dob.isEmpty() || dob == "Select Date of Birth" || 
            bloodGroup == "Select Blood Group" || selectedGenderId == -1) {
            Toast.makeText(this, "Please fill all the fields correctly.", Toast.LENGTH_SHORT).show()
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
                            "dob" to dob,
                            "bloodGroup" to bloodGroup,
                            "gender" to gender,
                            "role" to "student"
                        )

                        db.collection("users").document(userId).set(user)
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
                    Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}