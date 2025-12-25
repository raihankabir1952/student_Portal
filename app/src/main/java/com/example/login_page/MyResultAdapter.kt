package com.example.login_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemMyResultBinding

class MyResultAdapter(private val results: List<Result>) : RecyclerView.Adapter<MyResultAdapter.MyResultViewHolder>() {

    inner class MyResultViewHolder(private val binding: ItemMyResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.myCourseNameText.text = result.courseName
            binding.myGradeText.text = result.grade
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyResultViewHolder {
        val binding = ItemMyResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyResultViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount() = results.size
}