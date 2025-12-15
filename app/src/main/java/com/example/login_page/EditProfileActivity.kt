package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        populateProfileData()

        binding.saveChangesButton.setOnClickListener {
            updateProfileData()
        }
    }

    private fun populateProfileData() {
        db.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if(document != null && document.exists()) {
                    binding.editName.setText(document.getString("name"))
                    binding.editAddress.setText(document.getString("address"))
                    binding.editPhone.setText(document.getString("phone"))
                    binding.readOnlyEmail.text = document.getString("email")
                    binding.readOnlyDepartment.text = document.getString("department")
                }
            }
    }

    private fun updateProfileData() {
        val name = binding.editName.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all editable fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "name" to name,
            "address" to address,
            "phone" to phone
        )

        db.collection("users").document(auth.currentUser!!.uid)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish() // Go back to the profile page
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}