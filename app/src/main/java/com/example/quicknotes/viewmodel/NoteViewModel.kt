package com.example.quicknotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    
    val allNotes: Flow<List<Note>> = repository.getAllNotes()

    
    val allCompletedNotes: Flow<List<CompletedNote>> = repository.getAllCompletedNotes()
    
    private val pendingJobs = mutableMapOf<Int, Job>()

    fun insert(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }
    
    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }
    
    fun delete(note: Note) = viewModelScope.launch {
        repository.deleteNote(note.id)
    }
    
    fun completeNote(note: Note) = viewModelScope.launch {
        repository.completeNote(note.id)
    }
    
    fun startPendingComplete(note: Note, delayMillis: Long = 5000) {
        if (pendingJobs.containsKey(note.id)) return
        val job = viewModelScope.launch {
            update(note.copy(isCompleted = true))
            delay(delayMillis)
            val latestNote = repository.getNoteById(note.id)
            if (latestNote?.isCompleted == true) {
                completeNote(note)
            }
            pendingJobs.remove(note.id)
        }
        pendingJobs[note.id] = job
    }

    fun cancelPendingComplete(note: Note) {
        pendingJobs[note.id]?.cancel()
        pendingJobs.remove(note.id)
        update(note.copy(isCompleted = false))
    }

    fun deleteCompletedNote(completedNote: CompletedNote) = viewModelScope.launch {
        repository.deleteCompleted(completedNote)
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}