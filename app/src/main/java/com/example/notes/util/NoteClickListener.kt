package com.example.notes.util

import com.example.notes.database.NoteEntity

interface NoteClickListener {
    fun moveUp(data: NoteEntity)
    fun shareNote(data:NoteEntity)
    fun pinNote(data:NoteEntity)
    fun goToDetail(data:NoteEntity)
}