package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityAddNoticeBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoticeBinding
    private lateinit var db: FirebaseFirestore
    private var noticeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        // Check if we are in edit mode
        if (intent.hasExtra("NOTICE_ID")) {
            noticeId = intent.getStringExtra("NOTICE_ID")
            val title = intent.getStringExtra("NOTICE_TITLE")
            val details = intent.getStringExtra("NOTICE_DETAILS")

            binding.noticeTitle.setText(title)
            binding.noticeDetails.setText(details)
            binding.publishNoticeButton.text = "Update Notice"
            supportActionBar?.title = "Edit Notice"
        }

        binding.publishNoticeButton.setOnClickListener {
            if (noticeId == null) {
                publishNotice()
            } else {
                updateNotice()
            }
        }
    }

    private fun publishNotice() {
        val title = binding.noticeTitle.text.toString().trim()
        val details = binding.noticeDetails.text.toString().trim()

        if (title.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please enter title and details.", Toast.LENGTH_SHORT).show()
            return
        }

        val notice = hashMapOf(
            "title" to title,
            "details" to details,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("notices").add(notice)
            .addOnSuccessListener {
                Toast.makeText(this, "Notice published successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error publishing notice: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNotice() {
        val title = binding.noticeTitle.text.toString().trim()
        val details = binding.noticeDetails.text.toString().trim()

        if (title.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please enter title and details.", Toast.LENGTH_SHORT).show()
            return
        }

        val noticeUpdates = mapOf(
            "title" to title,
            "details" to details
        )

        db.collection("notices").document(noticeId!!)
            .update(noticeUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Notice updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating notice: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}