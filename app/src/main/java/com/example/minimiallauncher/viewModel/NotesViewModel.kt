package com.example.minimiallauncher.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minimiallauncher.data.notesdata.NotepadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotepadRepository) : ViewModel() {
    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes: StateFlow<List<String>> = _notes


    private val _stickyNotesVisible = MutableStateFlow(false)
    val stickyNotesVisible: StateFlow<Boolean> = _stickyNotesVisible

    fun toggleVisibility(show: Boolean) {
        _stickyNotesVisible.value = show
    }

    var noteText by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            val note = repository.getNote()
            noteText = note?.content ?: ""
        }
    }

    fun updateNote(newText: String) {
        noteText = newText
        viewModelScope.launch {
            repository.saveNote(newText)
        }
    }
}