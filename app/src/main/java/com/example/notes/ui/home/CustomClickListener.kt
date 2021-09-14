package com.example.notes.ui.home

import com.example.notes.database.NoteEntity

interface CustomClickListener {
    fun clickOnShareIcon(note: NoteEntity)
    fun clickOnMoveToTopIcon(note: NoteEntity)
    fun clickOnText(note: NoteEntity)
    fun clickOnPinIcon(note:NoteEntity)
}