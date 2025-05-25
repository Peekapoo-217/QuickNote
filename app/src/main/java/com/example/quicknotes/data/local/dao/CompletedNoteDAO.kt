package com.example.quicknotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.quicknotes.data.local.entity.CompletedNote
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedNoteDAO {
    @Insert
    suspend fun insert(completedNote: CompletedNote)

    @Query("SELECT * FROM completed_notes ORDER BY completedAt DESC")
    fun getAll(): Flow<List<CompletedNote>>

    @Delete
    suspend fun delete(completedNote: CompletedNote)
}