package com.example.notes.ui.home

import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
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

        //Bind view with viewModel
        binding.viewModel = viewModel

        // Set lifecycleOwner
        binding.lifecycleOwner = this


        // Bind adapter
        noteGridAdapter = NoteGridAdapter(this)
        binding.noteGrid.adapter = noteGridAdapter

        // Set up gestures processing
        val itemTouchHelper = ItemTouchHelper(NoteTouchHelper())
        itemTouchHelper.attachToRecyclerView(binding.noteGrid)


        // Notify adapter if there is any change in note list
        viewModel.notes.observe(viewLifecycleOwner, {
            it?.let {
                noteGridAdapter.submitList(it)
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

        // Navigate to add screen
        binding.addButton.setOnClickListener {
            this.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToAddBottomSheetFragment())
        }

        // Insert new notes if there are any
        viewModel.noteToBeAdded.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.insertNewNotesIntoDatabase()
                // TO DO clear variable value after insert
            }
        })

        return binding.root
    }


    // Class to process gestures like drag and drop or swipe
    inner class NoteTouchHelper : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN
        ),0
        /*ItemTouchHelper.LEFT*/
    ) {

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {


            val startPosition = viewHolder.adapterPosition

            var endPosition = target.adapterPosition

            // Get current number of pinned notes
            val localPinnedItemsNumber =
                viewModel.numberOfPinnedNotes

            // Create temp list to work with
            val tempList = viewModel.notes.value as MutableList<NoteEntity>

            // Check for unpinned notes mixing with pinned
            val startNote = tempList[startPosition]

            val endNote = tempList[endPosition]

            // Prevent unpinned note mixing with pinned
            if (!startNote.isPinned && endNote.isPinned) {
                endPosition = localPinnedItemsNumber
            } // Prevent pinned note mixing with unpinned
            else if (startNote.isPinned && !endNote.isPinned) {
                endPosition = localPinnedItemsNumber - 1
            }

            // Change temp list
            Collections.swap(tempList, startPosition, endPosition)
            // Synchronize with actual list
            viewModel.updateNotes(tempList)
            // Notify adapter
            recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // TO DO
            /*val position = viewHolder.adapterPosition
            noteGridAdapter.notifyItemRemoved(position)*/
        }
    }


    // Share note
    override fun clickOnShareIcon(note: NoteEntity) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(EXTRA_TEXT, note.text)
        startActivity(Intent.createChooser(intent, "Share in ....."))
    }

    // Move note
    override fun clickOnMoveToTopIcon(note: NoteEntity, position: Int) {
    }

    // Determine which note to show in detail screen
    override fun clickOnText(note: NoteEntity) {
        viewModel.displayNoteDetails(note)
    }

    // Pin note. If it was pinned, move to the bottom.Otherwise move to the top
    override fun clickOnPinIcon(note: NoteEntity, position: Int) {

        // Create temp list
        val listOfNotes = viewModel.notes.value as MutableList<NoteEntity>

        // Determine position of note based on it being pinned or not
        val newNotePosition = when (note.isPinned) {
            true -> listOfNotes.size - 1
            false -> 0
        }
        // Change note
        note.isPinned = note.isPinned.not()

        viewModel.updateNote(note)

        // Delete then add same note as it's a easiest way
        listOfNotes.removeAt(position)
        listOfNotes.add(newNotePosition, note)

        // // Synchronize with actual list
        viewModel.updateNotes(listOfNotes)

        // Notify adapter
        noteGridAdapter.notifyItemRemoved(position)
        noteGridAdapter.notifyItemInserted(newNotePosition)

    }
}