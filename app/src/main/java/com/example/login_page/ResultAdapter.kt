package com.example.login_page

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemCourseResultBinding

class ResultAdapter(
    private val courses: List<Map<String, Any>>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    val resultsMap = mutableMapOf<String, String>()

    init {
        courses.forEach {
            val courseName = it["name"] as String
            resultsMap[courseName] = "" // Initialize with empty grades
        }
    }

    inner class ResultViewHolder(private val binding: ItemCourseResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseMap: Map<String, Any>) {
            val courseName = courseMap["name"] as String
            binding.courseNameForResult.text = courseName

            // Remove any existing listener before adding a new one
            binding.courseGradeEditText.removeTextChangedListener(binding.courseGradeEditText.getTag(R.id.text_watcher_tag) as? TextWatcher)

            binding.courseGradeEditText.setText(resultsMap[courseName])

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    resultsMap[courseName] = s.toString()
                }
            }

            binding.courseGradeEditText.addTextChangedListener(textWatcher)
            binding.courseGradeEditText.setTag(R.id.text_watcher_tag, textWatcher)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemCourseResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Create a tag for the text watcher
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_result, parent, false)
        view.setTag(R.id.text_watcher_tag, null)

        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount() = courses.size
}