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
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var myCourses = mutableListOf<Course>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadMyCourses()
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(myCourses, AdapterMode.DISPLAY, onRemoveClick = { course ->
            removeCourse(course)
        })
        binding.myCoursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.myCoursesRecyclerView.adapter = courseAdapter
    }

    private fun loadMyCourses() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val coursesData = document.get("courses") as? List<Map<String, Any>> ?: emptyList()
                    
                    if (coursesData.isEmpty()) {
                        binding.myCoursesRecyclerView.visibility = View.GONE
                        binding.noCoursesMessage.visibility = View.VISIBLE
                    } else {
                        binding.myCoursesRecyclerView.visibility = View.VISIBLE
                        binding.noCoursesMessage.visibility = View.GONE
                        
                        myCourses = coursesData.map { courseMap ->
                            Course(
                                name = courseMap["name"]?.toString() ?: "N/A",
                                credit = courseMap["credit"]?.toString() ?: "0.0"
                            )
                        }.toMutableList()
                        courseAdapter.updateCourses(myCourses)
                    }
                } else {
                    // Document doesn't exist or has no 'courses' field
                    binding.myCoursesRecyclerView.visibility = View.GONE
                    binding.noCoursesMessage.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading courses: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.myCoursesRecyclerView.visibility = View.GONE
                binding.noCoursesMessage.visibility = View.VISIBLE // Also show message on error
            }
    }

    private fun removeCourse(course: Course) {
        val userId = auth.currentUser?.uid ?: return
        val courseToRemove = mapOf("name" to course.name, "credit" to course.credit)

        db.collection("users").document(userId)
            .update("courses", FieldValue.arrayRemove(courseToRemove))
            .addOnSuccessListener {
                Toast.makeText(this, "Course removed successfully", Toast.LENGTH_SHORT).show()
                loadMyCourses() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to remove course: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}