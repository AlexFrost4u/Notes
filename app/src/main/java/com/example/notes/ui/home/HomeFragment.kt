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

        // Get notes
        viewModel.getNotesFromDatabase()

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
                viewModel.insertNewNoteIntoDatabase()
                // TO DO clear variable value after insert
            }
        })

        return binding.root
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
                viewModel.numberOfPinnedNotes

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

            viewModel.correctNotePosition(oldList,tempList)

            // Synchronize with actual list
            /*viewModel.updateListNotes(tempList)*/

            // Notify adapter
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

            viewModel.deleteNote(noteIdToBeDeleted)
            viewModel.correctNotePositionAfterDelete(tempList)

            noteGridAdapter.notifyItemRemoved(position)
        }
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

    /* Move note the top of it's group( pinned, unpinned )
    * @param note that we want to move
    * @param position it's position in list
    *
     */
    override fun clickOnMoveToTopIcon(note: NoteEntity, position: Int) {
        // Create temp list to work with
        val tempList = viewModel.notes.value as MutableList<NoteEntity>
        viewModel.calculateNumberOfPinnedNotes()
        val endPosition: Int = if (note.isPinned) {
            0
        } else {
            viewModel.numberOfPinnedNotes
        }
        // Change temp list
        val oldList = tempList.toMutableList()

        tempList.removeAt(position)
        tempList.add(endPosition, note)

        viewModel.correctNotePosition(oldList,tempList)

        // Synchronize with actual list
        /*        viewModel.updateListNotes(tempList)*/

        // Notify adapter
        noteGridAdapter.notifyItemRemoved(position)
        noteGridAdapter.notifyItemInserted(endPosition)
    }

    /* Determine which note to show in detail screen
    * @param note that we want navigate to
    */
    override fun clickOnText(note: NoteEntity) {
        viewModel.displayNoteDetails(note)
    }

    // Pin note. If it was pinned, move to the bottom.Otherwise move to the top
    override fun clickOnPinIcon(note: NoteEntity, position: Int) {

        // Create temp list
        val tempList = viewModel.notes.value as MutableList<NoteEntity>

        // Determine position of note based on it being pinned or not
        val newNotePosition = when (note.isPinned) {
            true -> tempList.size - 1
            false -> 0
        }
        // Change note
        note.isPinned = note.isPinned.not()

        viewModel.updateNote(note)

        val oldList = tempList.toMutableList()
        // Delete then add same note as it's a easiest way
        tempList.removeAt(position)
        tempList.add(newNotePosition, note)

        viewModel.correctNotePosition(oldList,tempList)

        // // Synchronize with actual list
    /*        viewModel.updateListNotes(listOfNotes)*/

        // Notify adapter
        noteGridAdapter.notifyItemRemoved(position)
        noteGridAdapter.notifyItemInserted(newNotePosition)

    }
}