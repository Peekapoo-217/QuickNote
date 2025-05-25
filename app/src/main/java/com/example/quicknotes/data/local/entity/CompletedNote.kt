package com.example.quicknotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_notes")
data class CompletedNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val colorTag: String,
    val completedAt: Long = System.currentTimeMillis()
)
