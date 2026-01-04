package com.example.minimiallauncher.data.notesdata

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.minimiallauncher.viewModel.NotesViewModel

class NotesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            val db = NotesDatabase.getInstance(application)
            val repo = NotepadRepository(db.notesDao())
            return NotesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

