package com.example.login_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityNoticeBoardBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NoticeBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBoardBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var noticeAdapter: StudentNoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadNotices()
    }

    private fun setupRecyclerView() {
        noticeAdapter = StudentNoticeAdapter(noticeList)
        binding.noticesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.noticesRecyclerView.adapter = noticeAdapter
    }

    private fun loadNotices() {
        db.collection("notices").orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    binding.noNoticesText.visibility = View.VISIBLE
                    binding.noticesRecyclerView.visibility = View.GONE
                } else {
                    noticeList.clear()
                    for (document in result) {
                        val notice = document.toObject(Notice::class.java).copy(id = document.id)
                        noticeList.add(notice)
                    }
                    noticeAdapter.notifyDataSetChanged()
                    binding.noNoticesText.visibility = View.GONE
                    binding.noticesRecyclerView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                binding.noNoticesText.visibility = View.VISIBLE
                binding.noticesRecyclerView.visibility = View.GONE
                Toast.makeText(this, "Error loading notices: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}