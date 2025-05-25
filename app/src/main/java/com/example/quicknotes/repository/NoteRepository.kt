package com.example.quicknotes.repository

import com.example.quicknotes.data.local.dao.CompletedNoteDAO
import com.example.quicknotes.data.local.dao.NoteDAO
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.data.local.entity.Note
import kotlinx.coroutines.flow.Flow


class NoteRepository(private val noteDao: NoteDAO,
                     private val completedNoteDao: CompletedNoteDAO) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getAllCompletedNotes(): Flow<List<CompletedNote>> = completedNoteDao.getAll()


    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)

    suspend fun insert(note: Note) = noteDao.insert(note)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun insertCompleted(note: Note) {
        completedNoteDao.insert(
            CompletedNote(
                title = note.title,
                content = note.content,
                colorTag = note.colorTag
            )
        )
    }

    suspend fun deleteCompleted(note: CompletedNote) = completedNoteDao.delete(note)
}
