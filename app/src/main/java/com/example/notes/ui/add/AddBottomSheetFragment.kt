package com.example.notes.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.notes.database.NoteEntity
import com.example.notes.databinding.FragmentDialogAddBinding
import com.example.notes.ui.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class AddBottomSheetFragment : BottomSheetDialogFragment() {

    // Note that we will using to pass to HomeViewModel
    private var instance = NoteEntity()

    // Using activityViewModels so that i can insert new notes into DB using HomeViewModel as Hilt doesn't support
    // injection into dialog fragment. Create live data inside so that it will immediately insert it
    // after creating and new note will be shown in the background
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentDialogAddBinding.inflate(inflater, container, false)

        binding.noteAddImage.setOnClickListener {
            // Get text from EditText that is written now and check for text
            val noteText = binding.noteAddEdit.text.trim()
            if (noteText.isNotEmpty()) {
                instance.text = noteText.toString()
                viewModel.noteToBeAdded.value = instance
                binding.noteAddEdit.text.clear()
            }
        }
        return binding.root
    }

}