package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityMyResultsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class MyResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyResultsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var myResultAdapter: MyResultAdapter
    private val resultsList = mutableListOf<Result>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadMyResults()
    }

    private fun setupRecyclerView() {
        myResultAdapter = MyResultAdapter(resultsList)
        binding.myResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myResultsRecyclerView.adapter = myResultAdapter
    }

    private fun loadMyResults() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("results")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Toast.makeText(this, "Your results have not been published yet.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }
                resultsList.clear()
                resultsList.addAll(result.toObjects<Result>())
                myResultAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading results: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}