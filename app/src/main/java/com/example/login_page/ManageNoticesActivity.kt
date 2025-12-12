package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityManageNoticesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ManageNoticesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageNoticesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageNoticesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()

        binding.addNoticeFab.setOnClickListener {
            startActivity(Intent(this, AddNoticeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotices()
    }

    private fun setupRecyclerView() {
        noticeAdapter = NoticeAdapter(
            noticeList,
            onEditClick = { notice -> editNotice(notice) },
            onDeleteClick = { notice -> showDeleteConfirmationDialog(notice) }
        )
        binding.noticesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.noticesRecyclerView.adapter = noticeAdapter
    }

    private fun loadNotices() {
        db.collection("notices").orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                noticeList.clear()
                for (document in result) {
                    val notice = document.toObject(Notice::class.java).copy(id = document.id)
                    noticeList.add(notice)
                }
                noticeAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading notices: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editNotice(notice: Notice) {
        val intent = Intent(this, AddNoticeActivity::class.java).apply {
            putExtra("NOTICE_ID", notice.id)
            putExtra("NOTICE_TITLE", notice.title)
            putExtra("NOTICE_DETAILS", notice.details)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(notice: Notice) {
        AlertDialog.Builder(this)
            .setTitle("Delete Notice")
            .setMessage("Are you sure you want to delete this notice? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteNotice(notice)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNotice(notice: Notice) {
        db.collection("notices").document(notice.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show()
                loadNotices() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting notice: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}