package com.example.notes.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.notes.database.NoteDao
import com.example.notes.database.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.EnumSet.range
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
    var noteToBeAdded = MutableLiveData<NoteEntity?>()

    // Navigate to chosen note
    private val _navigateToSelectedNote = MutableLiveData<NoteEntity?>()
    val navigateToSelectedNote: LiveData<NoteEntity?>
        get() = _navigateToSelectedNote


    var numberOfPinnedNotes = 0


    // Get all notes from database
    fun getNotesFromDatabase() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
        }
    }

    // Calculate current pinned notes number
    fun calculateNumberOfPinnedNotes() {
        numberOfPinnedNotes = notes.value!!.filter { note -> note.isPinned }.size
    }

    // Synchronize list
    fun updateNotes(newList: List<NoteEntity>) {
        _notes.value = newList
    }

    // Determine to which note should we navigate
    fun displayNoteDetails(note: NoteEntity) {
        _navigateToSelectedNote.value = note
    }

    // Assure navigation is done once
    fun displayNoteDetailsComplete() {
        _navigateToSelectedNote.value = null
    }


    fun insertNewNoteIntoDatabase() {
        val position: Int = if (notes.value!!.isEmpty()) {
            1
        } else {
            notes.value!!.size + 1
        }

        // Determine note's position property
        noteToBeAdded.value?.notePosition = position

        viewModelScope.launch {
            noteDao.insert(noteToBeAdded.value!!)
            _notes.value = noteDao.getAllNotes()
        }
        noteToBeAdded.value = null
    }


    // Update note in database
    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    fun correctNotePosition(
        oldList: List<NoteEntity>,
        newList: List<NoteEntity>,
    ) {
        val changedNotes: MutableList<NoteEntity> = mutableListOf()

        for (index in oldList.indices) {
            if (oldList[index].noteId != newList[index].noteId) {
                newList[index].notePosition = index + 1
                changedNotes.add(newList[index])
            }
        }
        _notes.value = newList
        updateNotesInDatabase(changedNotes)
    }

    fun correctNotePositionAfterDelete(
        newList: List<NoteEntity>
    ) {
        val changedNotes: MutableList<NoteEntity> = mutableListOf()
        for (index in newList.indices) {
            if (newList[index].notePosition != index + 1){
                newList[index].notePosition = index + 1
                changedNotes.add(newList[index])
            }
        }
        _notes.value = newList
        updateNotesInDatabase(changedNotes)
    }

    private fun updateNotesInDatabase(changedNotes: MutableList<NoteEntity>) {
        viewModelScope.launch {
            for (data in changedNotes) {
                noteDao.update(data)
            }
        }
    }


    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            noteDao.deleteNoteById(noteId)
        }
    }
}