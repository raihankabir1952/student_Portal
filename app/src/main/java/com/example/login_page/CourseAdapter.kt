package com.example.login_page

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemCourseBinding
import java.util.Locale

enum class AdapterMode {
    REGISTRATION, DISPLAY
}

class CourseAdapter(
    private var courses: List<Course>,
    private val mode: AdapterMode,
    private val onRemoveClick: ((Course) -> Unit)?,
    private val registeredCourseNames: Set<String> = emptySet()
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>(), Filterable {

    private var filteredCourses: List<Course> = courses

    inner class CourseViewHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.courseName.text = course.name
            binding.courseCredit.text = "Credit: ${course.credit}"

            when (mode) {
                AdapterMode.REGISTRATION -> {
                    binding.courseCheckBox.visibility = View.VISIBLE
                    binding.removeCourseButton.visibility = View.GONE

                    val isRegistered = registeredCourseNames.contains(course.name)
                    binding.courseCheckBox.isChecked = isRegistered
                    binding.courseCheckBox.isEnabled = !isRegistered // Disable checkbox if already registered

                    if (!isRegistered) {
                        binding.courseCheckBox.setOnCheckedChangeListener { _, isChecked ->
                            course.isSelected = isChecked
                        }
                    } else {
                        binding.courseCheckBox.setOnCheckedChangeListener(null)
                    }
                }
                AdapterMode.DISPLAY -> {
                    binding.courseCheckBox.visibility = View.GONE
                    binding.removeCourseButton.visibility = View.VISIBLE
                    binding.removeCourseButton.setOnClickListener {
                        onRemoveClick?.invoke(course)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(filteredCourses[position])
    }

    override fun getItemCount() = filteredCourses.size

    fun getSelectedCourses(): List<Course> {
        return courses.filter { it.isSelected }
    }

    fun updateCourses(newCourses: List<Course>) {
        courses = newCourses
        filteredCourses = newCourses
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString()?.lowercase(Locale.ROOT) ?: ""
                filteredCourses = if (charString.isEmpty()) {
                    courses
                } else {
                    courses.filter { 
                        it.name.lowercase(Locale.ROOT).contains(charString)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredCourses
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredCourses = if (results?.values == null) {
                    ArrayList()
                } else {
                    results.values as List<Course>
                }
                notifyDataSetChanged()
            }
        }
    }
}