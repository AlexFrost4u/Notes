package com.example.notes.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.database.NoteEntity
import com.example.notes.databinding.ListItemBinding

class NoteGridAdapter(private val customClickListener:CustomClickListener) :
    ListAdapter<NoteEntity, NoteGridAdapter.NoteViewHolder>(DiffCallback) {

    inner class NoteViewHolder(private var binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: NoteEntity) {
            binding.note = note
            binding.executePendingBindings()

            binding.shareImage.setOnClickListener {
                customClickListener.clickOnShareIcon(note)
            }
            binding.moveImage.setOnClickListener {
                customClickListener.clickOnMoveToTopIcon(note)
            }
            binding.noteText.setOnClickListener {
                customClickListener.clickOnText(note)
            }
            binding.pinImage.setOnClickListener {
                customClickListener.clickOnPinIcon(note)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NoteEntity>() {
        override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
            return oldItem.noteId == newItem.noteId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    interface MyClickListener {
        fun onShare(note: NoteEntity)
        fun onPint(note: NoteEntity)
        fun moveToTop(note: NoteEntity)
    }

}