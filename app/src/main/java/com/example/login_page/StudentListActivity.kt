package com.example.login_page

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityStudentListBinding
import com.google.firebase.firestore.FirebaseFirestore

class StudentListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentListBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var studentAdapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadStudents()
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(studentList) { student ->
            // Navigate to AddResultActivity, passing student info
            val intent = Intent(this, AddResultActivity::class.java)
            intent.putExtra("STUDENT_ID", student.uid)
            intent.putExtra("STUDENT_NAME", student.name)
            startActivity(intent)
        }
        binding.studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.studentsRecyclerView.adapter = studentAdapter
    }

    private fun loadStudents() {
        db.collection("users")
            .whereEqualTo("role", "student")
            .get()
            .addOnSuccessListener { result ->
                studentList.clear()
                for (document in result) {
                    val student = Student(
                        uid = document.id,
                        name = document.getString("name") ?: "",
                        department = document.getString("department") ?: ""
                    )
                    studentList.add(student)
                }
                studentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading students: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}