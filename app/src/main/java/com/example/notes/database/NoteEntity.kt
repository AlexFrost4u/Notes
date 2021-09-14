package com.example.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notes")
data class NoteEntity constructor(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noteId")
    var noteId: Long,

    @ColumnInfo(name = "text")
    var text: String,

    @ColumnInfo(name = "pin")
    var isPinned: Boolean,

    @ColumnInfo(name = "position")
    var notePosition:Int

)