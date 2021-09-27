package com.example.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    // Insert note
    @Insert
    suspend fun insert(noteEntity:NoteEntity)

    @Insert
    suspend fun insertAll(notes:List<NoteEntity>)

    // Update note
    @Update
    suspend fun update(noteEntity:NoteEntity)

    // Get all notes
    @Query("SELECT * FROM notes ORDER BY notePosition ASC")
    suspend fun getAllNotes(): List<NoteEntity>

    // Get note by specific id
    @Query("SELECT * FROM notes WHERE noteId=:key")
    suspend fun getNoteById(key:Int):NoteEntity

    // Delete note by id
    @Query("DELETE FROM notes WHERE noteId =:key")
    suspend fun deleteNoteById(key:Int)

    @Query("DELETE FROM notes")
    suspend fun clear()


}