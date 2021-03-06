package com.example.notes.util

import android.graphics.Typeface
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.NoteEntity
import com.example.notes.ui.home.NoteGridAdapter

@BindingAdapter("isPinned")
fun setPin(image: ImageView, isPinned: Boolean) {
    if(isPinned){
        image.setImageResource(R.drawable.pin_filled)
    }else{
        image.setImageResource(R.drawable.pin_unfilled)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<NoteEntity>?) {
    val adapter = recyclerView.adapter as NoteGridAdapter
    adapter.submitList(data)
}
