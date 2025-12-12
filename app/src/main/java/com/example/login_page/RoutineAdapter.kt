package com.example.login_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemClassScheduleBinding

class RoutineAdapter(private var schedule: List<Routine>) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    inner class RoutineViewHolder(private val binding: ItemClassScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Routine) {
            binding.timeText.text = "${item.startTime} - ${item.endTime}"
            binding.courseNameText.text = "${item.courseCode} - ${item.courseName}"
            binding.facultyNameText.text = item.facultyName
            binding.roomNoText.text = item.roomNo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemClassScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(schedule[position])
    }

    override fun getItemCount() = schedule.size

    fun updateSchedule(newSchedule: List<Routine>) {
        schedule = newSchedule
        notifyDataSetChanged()
    }
}