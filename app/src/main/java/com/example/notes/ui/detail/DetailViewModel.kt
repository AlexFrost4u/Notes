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

    // Id of note to be shown in screen
    private val _selectedNoteId = MutableLiveData<Int>()
    val selectedNoteId: LiveData<Int>
        get() = _selectedNoteId


    // Note that we show
    private val _selectedNote = MutableLiveData<NoteEntity>()
    val selectedNote: LiveData<NoteEntity>
        get() = _selectedNote

    // To check for changes
    private var oldNote = NoteEntity()


    init {
        // Get note id from bundle
        _selectedNoteId.value = savedStateHandle.get<Int>("noteId")
        getSelectedNote()
    }

    // Get note to be shown by it's id from database
    private fun getSelectedNote() {
        viewModelScope.launch {
            _selectedNote.value = noteDao.getNoteById(_selectedNoteId.value!!)
            oldNote = _selectedNote.value!!
        }
    }

    // For some reason pinning note doesn't work at all so TO-DO
    fun setNewNotePin() {
        _selectedNote.value!!.isPinned = !selectedNote.value!!.isPinned
    }

    // Set new value for the text property of note
    fun setNewNoteText(newText: String) {
        _selectedNote.value?.text = newText
    }

    // Save note to the DB
    fun saveChanges() {
        if(oldNote == selectedNote.value!!) {
            viewModelScope.launch {
                selectedNote.value?.let { noteDao.update(it) }
            }
        }

    }
}


