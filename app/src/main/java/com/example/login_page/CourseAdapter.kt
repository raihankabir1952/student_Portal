package com.example.login_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemCourseBinding

class CourseAdapter(
    private var courses: List<Map<String, Any>>,
    private val onRemoveClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseMap: Map<String, Any>) {
            val courseName = courseMap["name"] as String
            val credit = courseMap["credit"] as Double
            binding.courseNameText.text = "$courseName (Credit: $credit)"
            binding.removeCourseButton.setOnClickListener {
                onRemoveClick(courseMap)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount() = courses.size

    fun updateCourses(newCourses: List<Map<String, Any>>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}