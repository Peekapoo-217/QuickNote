package com.example.quicknotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val colorTag: String,
    val createdAt: Long = System.currentTimeMillis(),
    val reminderTime: Long? = null,
    val isCompleted: Boolean = false

)
