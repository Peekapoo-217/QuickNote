package com.example.quicknotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    
    val allNotes: Flow<List<Note>> = repository.getAllNotes()
    
    val completedNotes: Flow<List<Note>> = repository.getCompletedNotes()
    
    fun insert(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }
    
    fun update(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
    }
    
    fun delete(note: Note) = viewModelScope.launch {
        repository.deleteNote(note.id)
    }
    
    fun completeNote(note: Note) = viewModelScope.launch {
        repository.completeNote(note.id)
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