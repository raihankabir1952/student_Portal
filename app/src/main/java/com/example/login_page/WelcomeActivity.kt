package com.example.login_page

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.login_page.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                uri -> uploadImageAndUpdateProfile(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        loadUserProfile()

        binding.editProfileFab.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.profileImage.setOnClickListener {
            openImagePicker()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.nameText.text = document.getString("name")
                    binding.emailText.text = document.getString("email")
                    binding.departmentText.text = document.getString("department")
                    binding.semesterText.text = document.getString("semester")
                    binding.phoneText.text = document.getString("phone")
                    binding.addressText.text = document.getString("address")
                    binding.dobText.text = document.getString("dob")
                    binding.bloodGroupText.text = document.getString("bloodGroup")
                    binding.genderText.text = document.getString("gender")

                    val imageUrl = document.getString("profileImageUrl")
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(imageUrl).into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.img_1) // Default image
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageAndUpdateProfile(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_pictures/$userId")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { 
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val imageUrl = downloadUrl.toString()
                    db.collection("users").document(userId)
                        .update("profileImageUrl", imageUrl)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                            // No need to call loadUserProfile() again, Glide will load it in onResume
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}