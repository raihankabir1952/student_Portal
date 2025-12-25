package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityAddResultBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddResultBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var resultAdapter: ResultAdapter

    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        studentId = intent.getStringExtra("STUDENT_ID")
        val studentName = intent.getStringExtra("STUDENT_NAME")

        if (studentId == null || studentName == null) {
            Toast.makeText(this, "Student information not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.studentNameForResults.text = "Results for: $studentName"

        loadStudentCourses(studentId!!)

        binding.saveResultsButton.setOnClickListener {
            saveResults(studentId!!)
        }
    }

    private fun loadStudentCourses(studentId: String) {
        db.collection("users").document(studentId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val courses = document.get("courses") as? List<Map<String, Any>> ?: emptyList()
                    setupRecyclerView(courses)
                } else {
                    Toast.makeText(this, "Student has no registered courses.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading courses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(courses: List<Map<String, Any>>) {
        resultAdapter = ResultAdapter(courses)
        binding.coursesForResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesForResultsRecyclerView.adapter = resultAdapter
    }

    private fun saveResults(studentId: String) {
        val resultsToSave = resultAdapter.resultsMap.map { (courseName, grade) ->
            Result(courseName, grade)
        }.filter { it.grade.isNotBlank() }

        if (resultsToSave.isEmpty()) {
            Toast.makeText(this, "Please enter at least one grade.", Toast.LENGTH_SHORT).show()
            return
        }

        val studentResultsRef = db.collection("users").document(studentId).collection("results")

        val batch = db.batch()
        resultsToSave.forEach { result ->
            // Use course name as document ID to avoid duplicates
            val docRef = studentResultsRef.document(result.courseName)
            batch.set(docRef, result)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Results saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving results: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
