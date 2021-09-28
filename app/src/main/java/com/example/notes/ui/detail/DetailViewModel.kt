package com.example.notes.ui.detail

import android.util.Log
import androidx.lifecycle.*
import com.example.notes.database.NoteDao
import com.example.notes.database.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val noteDao: NoteDao,
) : ViewModel() {

    // Id of note to be shown in screen
    private val _selectedNoteId = MutableLiveData<Int>()

    // Note that we show
    private val _selectedNote = MutableLiveData<NoteEntity>()
    val selectedNote: LiveData<NoteEntity>
        get() = _selectedNote

    // List of notes from DB
    private val _notes = MutableLiveData<List<NoteEntity>>()

    // Position of selected note in DB so that we can find it after note's properties change
    private var oldNotePosition = -1

    init {
        // Get note id from bundle
        _selectedNoteId.value = savedStateHandle.get<Int>("noteId")
        // Get note from DB according to it's ID
        getSelectedNoteFromDatabase()
        // Get all notes
        getAllNotesFromDatabase()
    }

    // Get notes from DB and determine selected note position
    private fun getAllNotesFromDatabase() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
            oldNotePosition = _notes.value!!.indexOf(_selectedNote.value!!)
        }
    }

    // Get note to be shown by it's id from database
    private fun getSelectedNoteFromDatabase() {
        viewModelScope.launch {
            _selectedNote.value = noteDao.getNoteById(_selectedNoteId.value!!)
        }
    }

    // Set new value for the text property of note
    fun setNewNoteText(newText: String) {
        _selectedNote.value?.text = newText
    }

    // Change pin state
    fun changeNoteState() {
        _selectedNote.value!!.isPinned = _selectedNote.value!!.isPinned.not()
    }

    // Save note to the DB
    fun saveChanges() {
        correctNotePositionInDatabase()
    }

    private fun correctNotePositionInDatabase() {
        // List notes but without selected note changes
        val tempList = _notes.value!!.toMutableList()

        // Determine note future position by it's pin property
        val newNotePosition = when (selectedNote.value!!.isPinned) {
            true -> 0
            false -> tempList.size - 1
        }

        // Move note accordingly
        tempList.removeAt(oldNotePosition)
        tempList.add(newNotePosition, selectedNote.value!!)

        // List of notes that should be updated in database
        val changedNotes: MutableList<NoteEntity> = mutableListOf()

        // Determine new note position for changed notes
        for(index in tempList.indices){
            if(tempList[index].notePosition != index + 1){
                tempList[index].notePosition = index + 1
                changedNotes.add(tempList[index])
            }
        }
        // Update notes that were changed in database
        updateNotesInDatabase(changedNotes)
    }

    // Update in database
    private fun updateNotesInDatabase(changedNotes: MutableList<NoteEntity>) {
        viewModelScope.launch {
            for (data in changedNotes) {
                noteDao.update(data)
            }
        }
    }
}


