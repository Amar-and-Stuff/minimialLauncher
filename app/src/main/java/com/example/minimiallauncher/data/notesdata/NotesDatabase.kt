package com.example.minimiallauncher.data.notesdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minimiallauncher.model.NotesModel

@Database(entities = [NotesModel::class], version = 1)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun notesDao (): NotesDao
    companion object {
        @Volatile private var INSTANCE: NotesDatabase? = null

        fun getInstance(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "notes_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}