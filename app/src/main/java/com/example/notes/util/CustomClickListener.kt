package com.example.notes.util

import com.example.notes.database.NoteEntity

interface CustomClickListener {
    fun clickOnShareIcon(note: NoteEntity)
    fun clickOnMoveToTopIcon(note: NoteEntity,position:Int)
    fun clickOnText(note: NoteEntity)
    fun clickOnPinIcon(note:NoteEntity,position:Int)
}