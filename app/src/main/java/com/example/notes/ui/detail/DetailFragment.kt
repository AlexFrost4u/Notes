package com.example.notes.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.database.NoteEntity
import com.example.notes.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by viewModels()
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // OnClickListener for share button
        binding.shareImage.setOnClickListener {
            shareNote(viewModel.selectedNote.value!!)
        }

        // OnClickListener for save button
        binding.doMoreButton.setOnClickListener {
            saveChanges()
        }

        // OnClickListener for pin button
        binding.pinImage.setOnClickListener {
            viewModel.setNewNoteState()
            if (viewModel.selectedNote.value!!.isPinned) {
                binding.pinImage.setImageResource(R.drawable.pin_filled)
            } else {
                binding.pinImage.setImageResource(R.drawable.pin_unfilled)
            }
        }

        // Navigate back
        binding.backButton.setOnClickListener {
            this.findNavController()
                .navigate(DetailFragmentDirections.actionDetailFragmentToHomeFragment())
        }
        return binding.root
    }

    // Share note that was passed
    private fun shareNote(note: NoteEntity) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, note.text)
        startActivity(Intent.createChooser(intent, resources.getText(R.string.share_in)))
    }

    private fun saveChanges() {
        viewModel.setNewNoteText(binding.notesEdit.text.toString())
        if (viewModel.selectedNote.value!!.isPinned == viewModel.notes.value!![viewModel.selectedNote.value!!.notePosition - 1].isPinned) {
            viewModel.updateIfPinIsNotChanged()
        } else {
            correctNotePositionInDatabase()
        }
    }


    private fun correctNotePositionInDatabase() {
        // List notes but without selected note changes
        val tempList = viewModel.notes.value!!.toMutableList()

        // Determine note future position by it's pin property
        val newNotePosition = when (viewModel.selectedNote.value!!.isPinned) {
            true -> 0
            false -> tempList.size - 1
        }

        // Move note accordingly
        tempList.removeAt(viewModel.selectedNote.value!!.notePosition - 1)
        tempList.add(newNotePosition, viewModel.selectedNote.value!!)

        // List of notes that should be updated in database
        val notesToBeUpdated: MutableList<NoteEntity> = mutableListOf()

        // Determine new note position for changed notes
        for (index in tempList.indices) {
            if (tempList[index].notePosition != index + 1) {
                tempList[index].notePosition = index + 1
                notesToBeUpdated.add(tempList[index])
            }
        }
        // Update notes that were changed in database
        viewModel.updateNotesInDatabase(notesToBeUpdated)
    }

}
