package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.login_page.databinding.ActivityCourseRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val availableCourses = listOf(
        mapOf("name" to "CSE 101 - Introduction to Programming", "credit" to 3.0),
        mapOf("name" to "CSE 202 - Data Structures", "credit" to 3.0),
        mapOf("name" to "MAT 111 - Calculus I", "credit" to 3.0),
        mapOf("name" to "PHY 101 - Physics I", "credit" to 3.0),
        mapOf("name" to "ENG 101 - English Composition", "credit" to 3.0),
        mapOf("name" to "CSE 102 - Object-Oriented Programming", "credit" to 3.0),
        mapOf("name" to "CSE 303 - Database Systems", "credit" to 3.0),
        mapOf("name" to "CSE 404 - Computer Networks", "credit" to 3.0),
        mapOf("name" to "CSE 505 - Software Engineering", "credit" to 3.0),
        mapOf("name" to "CSE 606 - Artificial Intelligence", "credit" to 3.0),
        mapOf("name" to "CSE 707 - Computer Graphics", "credit" to 3.0),
        mapOf("name" to "CSE 808 - Operating Systems", "credit" to 3.0),
        mapOf("name" to "CSE 909 - Computer Architecture", "credit" to 3.0),
        mapOf("name" to "CSE 100 - Web Programming", "credit" to 3.0),
        mapOf("name" to "CSE 201 - Discrete Mathematics", "credit" to 3.0),
        mapOf("name" to "CSE 302 - Computer Organization", "credit" to 3.0),
        mapOf("name" to "CSE 403 - Computer Networks Lab", "credit" to 1.5),
        mapOf("name" to "CSE 504 - Software Engineering Lab", "credit" to 1.5),
        mapOf("name" to "CSE 605 - Artificial Intelligence Lab", "credit" to 1.5),
        mapOf("name" to "CSE 706 - Computer Graphics Lab", "credit" to 1.5)
    )

    // Use a map to maintain the state of all selected courses across searches
    private val selectedCoursesState = mutableMapOf<String, Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserCoursesAndSetupUI()

        binding.registerCoursesButton.setOnClickListener {
            updateSelectedCoursesInFirestore()
        }
    }

    private fun loadUserCoursesAndSetupUI() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val registeredCourseMaps = if (document != null && document.exists()) {
                        document.get("courses") as? List<Map<String, Any>> ?: emptyList()
                    } else {
                        emptyList()
                    }
                    // Populate the state map with already registered courses
                    registeredCourseMaps.forEach { courseMap ->
                        val courseName = courseMap["name"] as String
                        selectedCoursesState[courseName] = courseMap
                    }
                    filterCourses("") // Initially show all courses
                }
                .addOnFailureListener { e ->
                    filterCourses("")
                    Toast.makeText(this, "Failed to load course data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            filterCourses("")
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterCourses(query: String) {
        binding.coursesLayout.removeAllViews()
        val filteredList = if (query.isBlank()) {
            availableCourses
        } else {
            availableCourses.filter {
                (it["name"] as String).contains(query, ignoreCase = true)
            }
        }

        filteredList.forEach { courseMap ->
            val courseName = courseMap["name"] as String
            val credit = courseMap["credit"] as Double
            val checkBox = CheckBox(this).apply {
                text = "$courseName (Credit: $credit)"
                textSize = 16f
                // Set checked state based on our state map
                isChecked = selectedCoursesState.containsKey(courseName)

                // Update the state map when the checkbox is clicked
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedCoursesState[courseName] = courseMap
                    } else {
                        selectedCoursesState.remove(courseName)
                    }
                }
            }
            binding.coursesLayout.addView(checkBox)
        }
    }

    private fun updateSelectedCoursesInFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // The list of courses to be saved is simply the values from our state map
            val coursesToSave = selectedCoursesState.values.toList()

            db.collection("users").document(userId)
                .update("courses", coursesToSave)
                .addOnSuccessListener {
                    Toast.makeText(this, "Course selection updated successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MyCourseActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating courses: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}