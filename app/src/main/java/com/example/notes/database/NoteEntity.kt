package com.example.notes.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class NoteEntity constructor(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noteId")
    var noteId: Int = 0,

    @ColumnInfo(name = "text")
    var text: String = "",

    @ColumnInfo(name = "isPinned")
    var isPinned: Boolean = false,

    @ColumnInfo(name = "notePosition")
    var notePosition:Int = -1

) : Parcelable