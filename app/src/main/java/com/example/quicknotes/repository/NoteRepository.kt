package com.example.quicknotes.repository

import android.content.Context
import android.net.Uri
import com.example.quicknotes.data.local.dao.CompletedNoteDAO
import com.example.quicknotes.data.local.dao.NoteDAO
import com.example.quicknotes.data.local.dao.NoteImageDAO
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.NoteImage
import com.example.quicknotes.utilities.ImageStorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NoteRepository(
    private val noteDao: NoteDAO,
    private val completedNoteDao: CompletedNoteDAO,
    private val noteImageDao: NoteImageDAO,
    private val context: Context
) {
    private val imageStorageHelper = ImageStorageHelper(context)
    
    // Note operations
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    fun getAllCompletedNotes(): Flow<List<CompletedNote>> = completedNoteDao.getAll()
    fun getCompletedNotes(): Flow<List<Note>> = noteDao.getCompletedNotes()
    suspend fun getNoteById(id: Int): Note? = noteDao.getNoteById(id)
    suspend fun insert(note: Note) = noteDao.insert(note)
    suspend fun insertNote(note: Note) = noteDao.insert(note)
    suspend fun insertAndGetId(note: Note): Long = noteDao.insertAndGetId(note)
    suspend fun insertNoteAndGetId(note: Note): Int = noteDao.insertAndGetId(note).toInt()
    suspend fun update(note: Note) = noteDao.update(note)
    suspend fun updateNote(note: Note) = noteDao.update(note)
    suspend fun delete(note: Note) {
        // Xóa tất cả ảnh của note trước
        val imageList = noteImageDao.getImagesByNoteId(note.id).first()
        imageList.forEach { image ->
            imageStorageHelper.deleteImage(image.imageUri)
        }
        noteImageDao.deleteImagesByNoteId(note.id)
        noteDao.delete(note)
    }
    suspend fun deleteNote(noteId: Int) {
        val note = noteDao.getNoteById(noteId) ?: return
        delete(note)
    }
    
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
    
    // Image operations
    suspend fun addImageToNote(noteId: Int, imageUri: Uri, description: String? = null): NoteImage? {
        return try {
            // Lưu ảnh vào storage
            val savedImagePath = imageStorageHelper.saveImageFromUri(imageUri) ?: return null
            
            // Tạo NoteImage entity
            val noteImage = NoteImage(
                noteId = noteId,
                imageUri = savedImagePath,
                fileName = savedImagePath.substringAfterLast("/"),
                fileSize = imageStorageHelper.getFileSize(savedImagePath),
                mimeType = imageStorageHelper.getMimeType(imageUri),
                description = description
            )
            
            // Lưu vào database
            val imageId = noteImageDao.insertImage(noteImage)
            noteImage.copy(id = imageId.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getImagesByNoteId(noteId: Int): Flow<List<NoteImage>> = noteImageDao.getImagesByNoteId(noteId)
    
    suspend fun deleteImage(noteImage: NoteImage): Boolean {
        return try {
            // Xóa file ảnh
            imageStorageHelper.deleteImage(noteImage.imageUri)
            // Xóa record trong database
            noteImageDao.deleteImage(noteImage)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun deleteAllImagesByNoteId(noteId: Int): Boolean {
        return try {
            val imageList = noteImageDao.getImagesByNoteId(noteId).first()
            imageList.forEach { image ->
                imageStorageHelper.deleteImage(image.imageUri)
            }
            noteImageDao.deleteImagesByNoteId(noteId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun completeNote(noteId: Int) {
        val note = noteDao.getNoteById(noteId) ?: return
        completeNote(note)
    }
    
    suspend fun uncompleteNote(noteId: Int) {
        val note = noteDao.getNoteById(noteId) ?: return
        updateNote(note.copy(isCompleted = false))
    }
    
    suspend fun completeNote(note: Note) {
        // Chỉ chuyển note vào completed, không xóa ảnh
        insertCompleted(note)
        noteDao.delete(note)
    }
}
