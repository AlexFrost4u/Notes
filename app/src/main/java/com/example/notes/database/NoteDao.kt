package com.example.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    // Insert note
    @Insert
    suspend fun insert(noteEntity:NoteEntity)

    // Update note
    @Update
    suspend fun update(noteEntity:NoteEntity)

    // Get all notes
    @Query("SELECT * FROM notes ORDER BY position ASC")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    // Get note by specific id
    @Query("SELECT * FROM notes WHERE id=:key")
    suspend fun getNoteById(key:Long):NoteEntity

    // Delete note by id
    @Query("DELETE FROM notes WHERE id =:key")
    fun deleteNoteById(key:Long)
}