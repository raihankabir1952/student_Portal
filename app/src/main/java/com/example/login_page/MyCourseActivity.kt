package com.example.login_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityMyCourseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MyCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyCourseBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var courseAdapter: CourseAdapter
    private val registeredCourses = mutableListOf<Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadRegisteredCourses()
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(registeredCourses) { courseToRemove ->
            removeCourse(courseToRemove)
        }
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = courseAdapter
    }

    private fun loadRegisteredCourses() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val courses = document.get("courses") as? List<Map<String, Any>>
                        if (courses != null && courses.isNotEmpty()) {
                            registeredCourses.clear()
                            registeredCourses.addAll(courses)
                            courseAdapter.notifyDataSetChanged()

                            // Calculate total credits
                            val totalCredit = courses.sumOf { it["credit"] as? Double ?: 0.0 }
                            binding.totalCreditText.text = "Total Credit: $totalCredit"

                            binding.coursesRecyclerView.visibility = View.VISIBLE
                            binding.totalCreditText.visibility = View.VISIBLE
                            binding.noCoursesText.visibility = View.GONE
                        } else {
                            showNoCoursesMessage()
                        }
                    } else {
                        showNoCoursesMessage()
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    showNoCoursesMessage()
                    Toast.makeText(this, "Error loading courses: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeCourse(course: Map<String, Any>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .update("courses", FieldValue.arrayRemove(course))
                .addOnSuccessListener {
                    val courseName = course["name"] as String
                    Toast.makeText(this, "'${courseName}' removed successfully.", Toast.LENGTH_SHORT).show()
                    loadRegisteredCourses() // Reload to update UI and total credit
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error removing course: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showNoCoursesMessage() {
        registeredCourses.clear()
        courseAdapter.notifyDataSetChanged()
        binding.coursesRecyclerView.visibility = View.GONE
        binding.totalCreditText.visibility = View.GONE
        binding.noCoursesText.visibility = View.VISIBLE
    }
}