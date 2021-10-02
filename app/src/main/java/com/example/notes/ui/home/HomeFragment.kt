package com.example.notes.ui.home

import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.database.NoteEntity
import com.example.notes.databinding.FragmentHomeBinding
import com.example.notes.util.CustomClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), CustomClickListener {

    private val viewModel: HomeViewModel by activityViewModels()

    // Expose adapter to work with it
    lateinit var noteGridAdapter: NoteGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        // Get notes
        viewModel.getNotesFromDatabase()

        //Bind view with viewModel
        binding.viewModel = viewModel

        // Set lifecycleOwner
        binding.lifecycleOwner = this

        subscribeObservers()
        // Bind adapter
        noteGridAdapter = NoteGridAdapter(this)
        binding.noteGrid.adapter = noteGridAdapter

        // Set up gestures processing
        val itemTouchHelper = ItemTouchHelper(NoteTouchHelper())
        itemTouchHelper.attachToRecyclerView(binding.noteGrid)

        // Navigate to add screen
        binding.addButton.setOnClickListener {
            this.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddBottomSheetFragment())
        }

        return binding.root
    }

    private fun subscribeObservers() {
        // Notify adapter if there is any change in note list
        viewModel.notes.observe(viewLifecycleOwner, {
            it?.let {
                noteGridAdapter.submitList(it)
            }
        })

        // Insert new notes if there are any
        viewModel.noteToBeAdded.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.insertNewNoteIntoDatabase()
            }
        })

        // Navigate to detail screen if note is selected
        viewModel.navigateToSelectedNote.observe(viewLifecycleOwner, {
            it?.let {
                this.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToDetailFragment(it.noteId))
                viewModel.displayNoteDetailsComplete()
            }
        })
    }

    /* Share note
    *  @param note that we want to share
    */
    override fun clickOnShareIcon(note: NoteEntity) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(EXTRA_TEXT, note.text)
        startActivity(Intent.createChooser(intent, "Share in ....."))
    }

    /* Determine which note to show in detail screen
    * @param note that we want navigate to
    */
    override fun clickOnText(note: NoteEntity) {
        viewModel.displayNoteDetails(note)

    }

    /// Move note the top of it's group( pinned, unpinned )
    override fun clickOnMoveToTopIcon(note: NoteEntity, position: Int) {
        /*viewModel.clickableRecyclerView(false)*/
        moveNotesInList(ClickEvent.ClickOnMoveToTopIcon, note, position)
    }

    /// Pin note. If it was pinned, move to the bottom.Otherwise move to the top
    override fun clickOnPinIcon(note: NoteEntity, position: Int) {
        /*viewModel.clickableRecyclerView(false)*/
        moveNotesInList(ClickEvent.ClickOnPinIcon, note, position)
    }

    // Class to hold click event's
    sealed class ClickEvent {
        object ClickOnPinIcon : ClickEvent()
        object ClickOnMoveToTopIcon : ClickEvent()
    }

    /* Move note according to event
        * @param note that we want to move
        * @param position it's position in list
         */
    private fun moveNotesInList(clickEvent: ClickEvent, note: NoteEntity, position: Int) {
        // Reference for list in viewModel
        val tempList = viewModel.notes.value as MutableList<NoteEntity>
        // Position of note to be added
        var noteNewPosition: Int = -1
        // Depending on click event we will proceed
        when (clickEvent) {
            is ClickEvent.ClickOnPinIcon -> {
                noteNewPosition = when (note.isPinned) {
                    true -> tempList.size - 1
                    false -> 0
                }
                // Change pin property as user click in pin icon
                note.isPinned = note.isPinned.not()
                // Update note's property in DB
                viewModel.updateNoteInDatabase(note)
            }

            is ClickEvent.ClickOnMoveToTopIcon -> {
                // Determine number of pinned notes
                viewModel.calculateNumberOfPinnedNotes()
                noteNewPosition = when (note.isPinned) {
                    true -> 0
                    false -> viewModel.calculateNumberOfPinnedNotes()
                }
            }
        }
        // Create list to store initial state of list
        val oldList = tempList.toMutableList()

        // Move note a.k.a Insert at position after deleting
        tempList.removeAt(position)
        tempList.add(noteNewPosition, note)

        Log.i("APP_DEBUG","--------${note.noteId} at ${note.notePosition}" )

        for(data in tempList) {
            Log.i("APP_DEBUG","${data.noteId} at ${data.notePosition}")
        }
        Log.i("APP_DEBUG","----------------------------------------")
        for(data in viewModel.notes.value!!) {
            Log.i("APP_DEBUG","${data.noteId} at ${data.notePosition}")
        }

        // Update notes' position if they were change in process of note move
        updateNotesPosition(oldList, tempList)

        // Notify adapter
        noteGridAdapter.notifyItemRemoved(position)
        noteGridAdapter.notifyItemInserted(noteNewPosition)

        /*viewModel.clickableRecyclerView(true)*/
    }

    // Update notes' position when we are moving note by deleting then adding it to the list
    private fun updateNotesPosition(oldList: List<NoteEntity>, newList: List<NoteEntity>) {
        val notesToBeUpdated: MutableList<NoteEntity> = mutableListOf()

        for (index in oldList.indices) {
            if (oldList[index].noteId != newList[index].noteId) {
                newList[index].notePosition = index + 1
                notesToBeUpdated.add(newList[index])
            }
        }
        viewModel.updateNotesValue(newList)
        viewModel.updateNotesInDatabase(notesToBeUpdated)
        /*viewModel.clickableRecyclerView(true)*/
    }


    // Class to process gestures like drag and drop or swipe
    inner class NoteTouchHelper : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN
        ), ItemTouchHelper.LEFT
    ) {

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {

            // Create temp list to work with
            val tempList = viewModel.notes.value as MutableList<NoteEntity>

            val startPosition = viewHolder.adapterPosition

            var endPosition = target.adapterPosition

            // Update variable just in case of changes
            viewModel.calculateNumberOfPinnedNotes()

            // Get current number of pinned notes
            val localPinnedItemsNumber =
                viewModel.calculateNumberOfPinnedNotes()

            // Check for unpinned notes mixing with pinned
            /*val startNote = viewModel.notes.value!![startPosition]*/
            val startNote = tempList[startPosition]
            /*val endNote = viewModel.notes.value!![endPosition]*/
            val endNote = tempList[endPosition]

            // Prevent unpinned note mixing with pinned
            if (!startNote.isPinned && endNote.isPinned) {
                endPosition = localPinnedItemsNumber
            } // Prevent pinned note mixing with unpinned
            if (startNote.isPinned && !endNote.isPinned) {
                endPosition = localPinnedItemsNumber - 1
            }
            val oldList = tempList.toMutableList()

            // Change temp list
            Collections.swap(tempList, startPosition, endPosition)

            updateNotesPosition(oldList, tempList)

            recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            // Temporary  list to work with
            val tempList = viewModel.notes.value as MutableList<NoteEntity>

            // Get position of note
            val position = viewHolder.adapterPosition

            val noteIdToBeDeleted = tempList[position].noteId
            tempList.removeAt(position)

            viewModel.deleteNoteFromDatabaseById(noteIdToBeDeleted)

            updateNotesPositionAfterDelete(tempList)

            noteGridAdapter.notifyItemRemoved(position)
        }
    }

    // Update notes' position when we delete note
    private fun updateNotesPositionAfterDelete(newList: List<NoteEntity>) {
        val notesToBeUpdated: MutableList<NoteEntity> = mutableListOf()

        for (index in newList.indices) {
            if (newList[index].notePosition != index + 1) {
                newList[index].notePosition = index + 1
                notesToBeUpdated.add(newList[index])
            }
        }
        viewModel.updateNotesValue(newList)
        viewModel.updateNotesInDatabase(notesToBeUpdated)
    }

}