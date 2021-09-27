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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        // Sometimes IDE freaks out and there is a problem with detailViewModel even though
        // it exists in xml
        binding.viewModel = viewModel

        // OnClickListener for share image
        binding.shareImage.setOnClickListener {
            shareNote(viewModel.selectedNote.value!!)
        }

        // OnClickListener for save image
        binding.doMoreButton.setOnClickListener {
            viewModel.setNewNoteText(binding.notesEdit.text.toString())
            viewModel.saveChanges()
            this.findNavController()
                .navigate(DetailFragmentDirections.actionDetailFragmentToHomeFragment())
        }

        binding.pinImage.setOnClickListener {
            viewModel.changeNoteState()
            if(viewModel.selectedNote.value!!.isPinned){
                binding.pinImage.setImageResource(R.drawable.pin_filled)
            }else{
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

    private fun shareNote(note: NoteEntity) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, note.text)
        startActivity(Intent.createChooser(intent, resources.getText(R.string.share_in)))
    }

}
