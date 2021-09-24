package com.example.notes.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.notes.database.NoteDao
import com.example.notes.database.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val noteDao: NoteDao,
) : ViewModel() {

    // Our list of notes for recyclerview
    private val _notes = MutableLiveData<List<NoteEntity>>()

    // Access to the list
    val notes: LiveData<List<NoteEntity>>
        get() = _notes

    // New notes
    var noteToBeAdded = MutableLiveData<NoteEntity>()

    // Navigate to chosen note
    private val _navigateToSelectedNote = MutableLiveData<NoteEntity?>()
    val navigateToSelectedNote: LiveData<NoteEntity?>
        get() = _navigateToSelectedNote



    var numberOfPinnedNotes = 0

    init {
        getNotes()
    }

    // Get all notes from database
    private fun getNotes() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
        }

    }

    // Calculate current pinned notes number
    private fun calculateNumberOfPinnedNotes() {
        numberOfPinnedNotes = notes.value!!.filter { note -> note.isPinned }.size
    }

    // Synchronize list
    fun updateNotes(newList:List<NoteEntity>) {
        _notes.value = newList
        calculateNumberOfPinnedNotes()
        //TO DO update list in database
    }

    // Determine to which note should we navigate
    fun displayNoteDetails(note: NoteEntity) {
        _navigateToSelectedNote.value = note
    }

    // Assure navigation is done once
    fun displayNoteDetailsComplete() {
        _navigateToSelectedNote.value = null
    }


    fun insertNewNotesIntoDatabase() {
        val position: Int = if (notes.value!!.isEmpty()) {0} else { notes.value!!.size }

        // Determine note's position property
        noteToBeAdded.value?.notePosition = position

        viewModelScope.launch {
            noteDao.insert(noteToBeAdded.value!!)
            _notes.value = noteDao.getAllNotes()
        }
    }


    // Update note in database

    fun updateNote(note:NoteEntity){
        viewModelScope.launch {
            noteDao.update(note)
        }
    }
}