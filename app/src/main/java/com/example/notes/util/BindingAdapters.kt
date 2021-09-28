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

@BindingAdapter("customText")
fun customizeText(textView: TextView, text:String){
    // TO-DO
    /*val words = text.split("\\s+".toRegex()).map { word ->
        word.replace("""^[,.]|[,.]$""".toRegex(), "")
    }
    // Problem with null when there is one word
    val firstWord = if(words[0] == null ){0} else {words[0].length}
    val secondWord = if(words[1] == null ){0} else {words[1].length}
    val charCount = firstWord + secondWord
    val result = SpannableStringBuilder(text)
    val style = StyleSpan(Typeface.BOLD)
    result.setSpan(style,0,charCount,Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = text*/
}
