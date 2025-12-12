package com.example.login_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemNoticeStudentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class StudentNoticeAdapter(
    private val notices: List<Notice>
) : RecyclerView.Adapter<StudentNoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val binding: ItemNoticeStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.noticeTitleText.text = notice.title
            binding.noticeDetailsText.text = notice.details
            notice.timestamp?.toDate()?.let {
                val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
                binding.noticeDateText.text = sdf.format(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }

    override fun getItemCount() = notices.size
}