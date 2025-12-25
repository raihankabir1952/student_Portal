package com.example.login_page

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityCourseRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CourseRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseRegistrationBinding
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Master list of all available courses
    private val allAvailableCourses = listOf(
        Course("CSE 101 - Introduction to Programming", "3.0"),
        Course("CSE 102 - Object-Oriented Programming", "3.0"),
        Course("CSE 202 - Data Structures", "3.0"),
        Course("CSE 303 - Database Systems", "3.0"),
        Course("MAT 111 - Calculus I", "3.0"),
        Course("PHY 101 - Physics I", "3.0"),
        Course("CSE 403 - Computer Networks Lab", "1.5"),
        Course("CSE 504 - Software Engineering Lab", "1.0")
    )

    private var displayCourses = mutableListOf<Course>()
    private var registeredCourseNames = setOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadStudentAndSetupUI()

        binding.registerCoursesButton.setOnClickListener {
            registerNewCourses()
        }
    }

    private fun loadStudentAndSetupUI() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val coursesData = document.get("courses") as? List<Map<String, Any>> ?: emptyList()
                    registeredCourseNames = coursesData.map { it["name"].toString() }.toSet()
                }

                displayCourses = allAvailableCourses.map { availableCourse ->
                    val isRegistered = registeredCourseNames.contains(availableCourse.name)
                    availableCourse.copy(isSelected = isRegistered)
                }.toMutableList()

                setupRecyclerView()
                setupSearchView()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading course data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView() {
        // Pass a disabled check state for already registered courses
        courseAdapter = CourseAdapter(displayCourses, AdapterMode.REGISTRATION, onRemoveClick = null, registeredCourseNames = registeredCourseNames)
        binding.coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.coursesRecyclerView.adapter = courseAdapter
    }

    private fun setupSearchView() {
        binding.courseSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                courseAdapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun registerNewCourses() {
        val newCoursesToRegister = courseAdapter.getSelectedCourses().filter {
            !registeredCourseNames.contains(it.name)
        }

        if (newCoursesToRegister.isEmpty()) {
            Toast.makeText(this, "No new courses selected.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)
        val courseData = newCoursesToRegister.map { mapOf("name" to it.name, "credit" to it.credit) }

        userDocRef.update("courses", FieldValue.arrayUnion(*courseData.toTypedArray()))
            .addOnSuccessListener {
                Toast.makeText(this, "New courses registered successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to register courses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}