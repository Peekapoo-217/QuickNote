package com.example.quicknotes.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknotes.data.local.dao.CompletedNoteDAO
import com.example.quicknotes.data.local.dao.NoteDAO
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.data.local.entity.Note

@Database(
    entities = [Note::class, CompletedNote::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO
    abstract fun completedNoteDao(): CompletedNoteDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // xóa dữ liệu cũ khi đổi schema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
