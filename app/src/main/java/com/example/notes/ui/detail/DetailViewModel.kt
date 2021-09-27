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

    private val _notes = MutableLiveData<List<NoteEntity>>()

    init {
        // Get note id from bundle
        _selectedNoteId.value = savedStateHandle.get<Int>("noteId")
        getSelectedNoteFromDatabase()
        getAllNotesFromDatabase()

    }

    private fun getAllNotesFromDatabase() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
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

    // Save note to the DB
    fun saveChanges() {
        viewModelScope.launch {
            selectedNote.value?.let { noteDao.update(it) }
            /*correctNotePositionInDatabase()*/
        }
    }

    // Change pin state
    fun changeNoteState() {
        _selectedNote.value!!.isPinned = _selectedNote.value!!.isPinned.not()
    }

    /*private fun correctNotePositionInDatabase() {
        val tempList = _notes.value as MutableList<NoteEntity>
        val numberOfPinnedNotes = _notes.value!!.filter { note -> note.isPinned }.size
        val newNotePosition = when (selectedNote.value!!.isPinned) {
            true -> 0
            false -> numberOfPinnedNotes - 1
        }
        tempList.remove(selectedNote.value)
        tempList.add(newNotePosition, selectedNote.value!!)

        val changedNotes: MutableList<NoteEntity> = mutableListOf()
        for (index in tempList.indices) {
            if (tempList[index].notePosition != index + 1) {
                tempList[index].notePosition = index + 1
                changedNotes.add(tempList[index])
            }
        }
        updateNotesInDatabase(changedNotes)
    }

    private fun updateNotesInDatabase(changedNotes: MutableList<NoteEntity>) {
        viewModelScope.launch {
            for (data in changedNotes) {
                noteDao.update(data)
            }
        }
    }*/
}


