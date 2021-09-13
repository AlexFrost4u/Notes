package com.example.notes.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.database.NoteEntity

class HomeViewModel: ViewModel() {

    private val testListOfNotes: MutableList<NoteEntity> =
        mutableListOf(
            NoteEntity(id=1,text="Hi there, I am text number 1. I am just filling up space",isPinned=false,notePosition = 1),
            NoteEntity(id=2,text="Hi there, I am text number 2. I am just filling up space",isPinned=true,notePosition = 2),
            NoteEntity(id=3,text="Hi there, I am text number 3. I am just filling up space",isPinned=false,notePosition = 3),
            NoteEntity(id=4,text="Hi there, I am text number 4. I am just filling up space",isPinned=false,notePosition = 4),
            NoteEntity(id=5,text="Hi there, I am text number 5. I am just filling up space",isPinned=true,notePosition = 5),
            NoteEntity(id=6,text="Hi there, I am text number 6. I am just filling up space",isPinned=false,notePosition = 6),
            NoteEntity(id=7,text="Hi there, I am text number 7. I am just filling up space",isPinned=false,notePosition = 7),
            NoteEntity(id=8,text="Hi there, I am text number 8. I am just filling up space",isPinned=true,notePosition = 8),
            NoteEntity(id=9,text="Hi there, I am text number 9. I am just filling up space",isPinned=true,notePosition = 9),
            NoteEntity(id=10,text="Hi there, I am text number 10. I am just filling up space",isPinned=false,notePosition = 10),
            NoteEntity(id=11,text="Hi there, I am text number 11. I am just filling up space",isPinned=false,notePosition = 11),
            NoteEntity(id=12,text="Hi there, I am text number 12. I am just filling up space",isPinned=false,notePosition = 12),

        )

    // Our list of notes
    private val _notes = MutableLiveData<List<NoteEntity>>()
    // Access to the list
    val notes: LiveData<List<NoteEntity>>
        get() = _notes

    init{
        getNotes()
    }

    private fun getNotes() {
        _notes.value = testListOfNotes
    }
}