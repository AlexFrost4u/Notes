package com.example.notes.ui.home

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
    var noteToBeAdded = MutableLiveData<NoteEntity?>()

    // Navigate to chosen note
    private val _navigateToSelectedNote = MutableLiveData<NoteEntity?>()
    val navigateToSelectedNote: LiveData<NoteEntity?>
        get() = _navigateToSelectedNote

    var numberOfPinnedNotes = 0

    // Calculate current pinned notes number
    fun calculateNumberOfPinnedNotes() {
        numberOfPinnedNotes = notes.value!!.filter { note -> note.isPinned }.size
    }

    // Determine to which note should we navigate
    fun displayNoteDetails(note: NoteEntity) {
        _navigateToSelectedNote.value = note
    }

    // Assure navigation is done once
    fun displayNoteDetailsComplete() {
        _navigateToSelectedNote.value = null
    }

    // Get all notes from database
    fun getNotesFromDatabase() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
        }
    }

    // Update single note in database
    fun updateNoteInDatabase(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }

    // Insert list of notes into database
    private fun updateNotesInDatabase(changedNotes: MutableList<NoteEntity>) {
        viewModelScope.launch {
            for (data in changedNotes) {
                noteDao.update(data)
            }
        }
    }

    // Insert new note that we got from BottomSheetDialogFragment
    fun insertNewNoteIntoDatabase() {
        val position: Int =  when(notes.value!!.isEmpty()) {
            true -> 1
            false ->notes.value!!.size + 1
        }

        // Determine note's position property
        noteToBeAdded.value?.notePosition = position

        viewModelScope.launch {
            noteDao.insert(noteToBeAdded.value!!)
            _notes.value = noteDao.getAllNotes()
        }
        // Set to null to make it EFFECT
        noteToBeAdded.value = null
    }

    // Delete note by ID
    fun deleteNoteFromDatabaseById(noteId: Int) {
        viewModelScope.launch {
            noteDao.deleteNoteById(noteId)
        }
    }

    // Update notes' position when we are moving note by deleting then adding it to the list
    fun updateNotesPosition(oldList: List<NoteEntity>, newList: List<NoteEntity>) {
        val notesToBeUpdated: MutableList<NoteEntity> = mutableListOf()

        for (index in oldList.indices) {
            if (oldList[index].noteId != newList[index].noteId) {
                newList[index].notePosition = index + 1
                notesToBeUpdated.add(newList[index])
            }
        }
        _notes.value = newList
        updateNotesInDatabase(notesToBeUpdated)
    }

    // Update notes' position when we delete note
    fun updateNotesPositionAfterDelete(newList: List<NoteEntity>) {
        val notesToBeUpdated: MutableList<NoteEntity> = mutableListOf()

        for (index in newList.indices) {
            if (newList[index].notePosition != index + 1) {
                newList[index].notePosition = index + 1
                notesToBeUpdated.add(newList[index])
            }
        }
        _notes.value = newList
        updateNotesInDatabase(notesToBeUpdated)
    }
}