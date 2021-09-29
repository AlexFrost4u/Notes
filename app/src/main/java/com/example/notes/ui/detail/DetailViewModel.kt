package com.example.notes.ui.detail

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

    // Note that we show
    private val _selectedNote = MutableLiveData<NoteEntity>()
    val selectedNote: LiveData<NoteEntity>
        get() = _selectedNote

    // List of notes from DB
    private val _notes = MutableLiveData<List<NoteEntity>>()
    val notes: LiveData<List<NoteEntity>>
        get() = _notes

    init {
        // Get note id from bundle
        val id = savedStateHandle.get<Int>("noteId")
        // Get note from DB according to it's ID
        getSelectedNoteFromDatabase(id!!)
        // Get all notes
        getAllNotesFromDatabase()
    }

    // Set new value for the text property of note
    fun setNewNoteText(newText: String) {
        _selectedNote.value?.text = newText
    }

    // Change pin state
    fun setNewNoteState() {
        _selectedNote.value!!.isPinned = _selectedNote.value!!.isPinned.not()
    }

    // Get notes from DB and determine selected note position
    private fun getAllNotesFromDatabase() {
        viewModelScope.launch {
            _notes.value = noteDao.getAllNotes()
        }
    }

    // Get note to be shown by it's id from database
    private fun getSelectedNoteFromDatabase(id: Int) {
        viewModelScope.launch {
            _selectedNote.value = noteDao.getNoteById(id)
        }
    }

    // Update in database
    fun updateNotesInDatabase(changedNotes: MutableList<NoteEntity>) {
        viewModelScope.launch {
            changedNotes.forEach {
                noteDao.update(it)
            }
        }
    }

    // Just update note if pin property is not modified
    fun updateIfPinIsNotChanged() {
        viewModelScope.launch {
            noteDao.update(selectedNote.value!!)
        }
    }
}


