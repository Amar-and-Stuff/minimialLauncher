package com.example.minimiallauncher.data.notesdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.minimiallauncher.model.NotesModel

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes WHERE id = 0")
    suspend fun getNote(): NotesModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesModel)
}
