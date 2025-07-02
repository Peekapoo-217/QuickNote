package com.example.quicknotes.data.model

import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.NoteImage

data class NoteWithImages(
    val note: Note,
    val images: List<NoteImage> = emptyList()
) {
} 