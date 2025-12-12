package com.example.login_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.login_page.databinding.ActivityClassRoutineBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class ClassRoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassRoutineBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var routineAdapter: RoutineAdapter

    private val fullSchedule = mutableMapOf<String, List<Routine>>(
        "Sunday" to emptyList(), "Monday" to emptyList(), "Tuesday" to emptyList(),
        "Wednesday" to emptyList(), "Thursday" to emptyList(), "Friday" to emptyList(), "Saturday" to emptyList()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupTabs()
        loadPersonalizedRoutine()
    }

    private fun setupRecyclerView() {
        routineAdapter = RoutineAdapter(emptyList())
        binding.routineRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.routineRecyclerView.adapter = routineAdapter
    }

    private fun setupTabs() {
        val days = fullSchedule.keys
        days.forEach { day ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(day))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val day = tab?.text.toString()
                val classesForDay = fullSchedule[day] ?: emptyList()
                routineAdapter.updateSchedule(classesForDay)

                if (classesForDay.isEmpty()) {
                    binding.noClassesTodayText.visibility = View.VISIBLE
                    binding.routineRecyclerView.visibility = View.GONE
                } else {
                    binding.noClassesTodayText.visibility = View.GONE
                    binding.routineRecyclerView.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadPersonalizedRoutine() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                val registeredCourses = userDoc.get("courses") as? List<Map<String, Any>> ?: emptyList()
                val registeredCourseCodes = registeredCourses.map { (it["name"] as? String? ?: "").substringBefore(" - ") }

                if (registeredCourseCodes.isEmpty()) {
                    Toast.makeText(this, "You have not registered for any courses.", Toast.LENGTH_LONG).show()
                    showNoClassesForAnyDay()
                    return@addOnSuccessListener
                }

                db.collection("class_routine").whereIn("courseCode", registeredCourseCodes).get()
                    .addOnSuccessListener { routineDocs ->
                        val personalRoutine = routineDocs.toObjects<Routine>()
                        val groupedByDay = personalRoutine.groupBy { it.day }

                        fullSchedule.keys.forEach { day ->
                            fullSchedule[day] = groupedByDay[day]?.sortedBy { it.startTime } ?: emptyList()
                        }

                        binding.tabLayout.getTabAt(0)?.select()
                        val initialClasses = fullSchedule["Sunday"] ?: emptyList()
                        routineAdapter.updateSchedule(initialClasses)
                        if(initialClasses.isEmpty()){
                            binding.noClassesTodayText.visibility = View.VISIBLE
                            binding.routineRecyclerView.visibility = View.GONE
                        } else {
                            binding.noClassesTodayText.visibility = View.GONE
                            binding.routineRecyclerView.visibility = View.VISIBLE
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to load class routine: ${e.message}", Toast.LENGTH_SHORT).show()
                        showNoClassesForAnyDay()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load user profile: ${e.message}", Toast.LENGTH_SHORT).show()
                showNoClassesForAnyDay()
            }
    }
    
    private fun showNoClassesForAnyDay(){
        binding.noClassesTodayText.text = "No classes found for your registered courses."
        binding.noClassesTodayText.visibility = View.VISIBLE
        binding.routineRecyclerView.visibility = View.GONE
    }
}