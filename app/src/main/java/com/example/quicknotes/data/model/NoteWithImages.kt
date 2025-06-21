package com.example.quicknotes.data.model

import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.NoteImage

data class NoteWithImages(
    val note: Note,
    val images: List<NoteImage> = emptyList()
) {
    val hasImages: Boolean get() = images.isNotEmpty()
    val firstImage: NoteImage? get() = images.firstOrNull()
    val imageCount: Int get() = images.size
    val totalImageSize: Long get() = images.sumOf { it.fileSize }
    
    fun getImageUris(): List<String> = images.map { it.imageUri }
    
    fun getImageById(imageId: Int): NoteImage? = images.find { it.id == imageId }
} 