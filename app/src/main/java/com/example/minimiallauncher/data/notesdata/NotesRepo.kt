package com.example.minimiallauncher.data.notesdata

import com.example.minimiallauncher.model.NotesModel

class NotepadRepository(private val dao: NotesDao) {
    suspend fun getNote() = dao.getNote()
    suspend fun saveNote(content: String) {
        dao.insertNote(NotesModel(id = 0, content = content))
    }
}
