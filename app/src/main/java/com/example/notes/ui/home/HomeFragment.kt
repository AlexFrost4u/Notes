package com.example.notes.ui.home

import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.database.NoteEntity
import com.example.notes.databinding.FragmentHomeBinding
import java.util.*

class HomeFragment : Fragment(),CustomClickListener {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater)

        //Bind view with viewModel
        binding.viewModel = viewModel

        // Bind adapter
        binding.noteGrid.adapter = NoteGridAdapter(this)

        // Set up gestures processing
        val noteTouchHelper = NoteTouchHelper()
        val itemTouchHelper = ItemTouchHelper(noteTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.noteGrid)
        return binding.root
    }


    // To process gestures
    inner class NoteTouchHelper : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP.or(
            ItemTouchHelper.DOWN
        ), 0/*ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)*/
    ) {

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {

            val startPosition = viewHolder.adapterPosition
            val endPosition = target.adapterPosition

            Collections.swap(viewModel.listOfNotes, startPosition, endPosition)
            recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)

            viewModel.updateNotes()
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // TO-DO
            /* when (direction) {
                 ItemTouchHelper.LEFT -> {

                 }
                 ItemTouchHelper.RIGHT -> {
                 }
             }*/
        }
    }

    override fun clickOnShareIcon(note: NoteEntity) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(EXTRA_TEXT,note.text)
        startActivity(Intent.createChooser(intent,"Share in ....."))
    }

    override fun clickOnMoveToTopIcon(note: NoteEntity) {
        // TO-DO
        Toast.makeText(context,"Move to top clicked",Toast.LENGTH_SHORT).show()
    }

    override fun clickOnText(note: NoteEntity) {
        //TO-DO navigate to detail screen
        Toast.makeText(context,"Text clicked",Toast.LENGTH_SHORT).show()
    }

    override fun clickOnPinIcon(note: NoteEntity) {
        //TO-DO
        Toast.makeText(context,"Pin Icon clicked",Toast.LENGTH_SHORT).show()
    }
}