package com.example.login_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_page.databinding.ItemNoticeManageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeAdapter(
    private var notices: List<Notice>,
    private val onEditClick: (Notice) -> Unit,
    private val onDeleteClick: (Notice) -> Unit
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val binding: ItemNoticeManageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.noticeTitleText.text = notice.title
            notice.timestamp?.toDate()?.let {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                binding.noticeDateText.text = sdf.format(it)
            }
            binding.editButton.setOnClickListener { onEditClick(notice) }
            binding.deleteButton.setOnClickListener { onDeleteClick(notice) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeManageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(notices[position])
    }

    override fun getItemCount() = notices.size

    fun updateNotices(newNotices: List<Notice>) {
        notices = newNotices
        notifyDataSetChanged()
    }
}