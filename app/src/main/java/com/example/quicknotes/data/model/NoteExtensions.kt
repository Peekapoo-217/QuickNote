package com.example.quicknotes.data.model

import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.NoteImage

fun Note.toNoteWithImages(images: List<NoteImage> = emptyList()): NoteWithImages {
    return NoteWithImages(this, images)
}

fun NoteWithImages.toNote(): Note {
    return this.note
}

fun List<Note>.toNotesWithImages(imagesMap: Map<Int, List<NoteImage>>): List<NoteWithImages> {
    return this.map { note ->
        note.toNoteWithImages(imagesMap[note.id] ?: emptyList())
    }
}

fun List<NoteWithImages>.toNotes(): List<Note> {
    return this.map { it.note }
} 