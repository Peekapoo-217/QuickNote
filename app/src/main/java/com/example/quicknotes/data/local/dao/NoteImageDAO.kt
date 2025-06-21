package com.example.quicknotes.data.local.dao

import androidx.room.*
import com.example.quicknotes.data.local.entity.NoteImage
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteImageDAO {
    
    // Lấy tất cả hình ảnh của một note
    @Query("SELECT * FROM note_images WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getImagesByNoteId(noteId: Int): Flow<List<NoteImage>>
    
    // Lấy tất cả hình ảnh
    @Query("SELECT * FROM note_images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<NoteImage>>
    
    // Lấy hình ảnh theo ID
    @Query("SELECT * FROM note_images WHERE id = :imageId")
    suspend fun getImageById(imageId: Int): NoteImage?
    
    // Lấy hình ảnh đầu tiên của một note
    @Query("SELECT * FROM note_images WHERE noteId = :noteId ORDER BY createdAt ASC LIMIT 1")
    suspend fun getFirstImageByNoteId(noteId: Int): NoteImage?
    
    // Thêm hình ảnh mới
    @Insert
    suspend fun insertImage(noteImage: NoteImage): Long
    
    // Thêm nhiều hình ảnh
    @Insert
    suspend fun insertImages(noteImages: List<NoteImage>)
    
    // Cập nhật hình ảnh
    @Update
    suspend fun updateImage(noteImage: NoteImage)
    
    // Xóa hình ảnh
    @Delete
    suspend fun deleteImage(noteImage: NoteImage)
    
    // Xóa hình ảnh theo ID
    @Query("DELETE FROM note_images WHERE id = :imageId")
    suspend fun deleteImageById(imageId: Int)
    
    // Xóa tất cả hình ảnh của một note
    @Query("DELETE FROM note_images WHERE noteId = :noteId")
    suspend fun deleteImagesByNoteId(noteId: Int)
    
    // Xóa tất cả hình ảnh
    @Query("DELETE FROM note_images")
    suspend fun deleteAllImages()
    
    // Đếm số hình ảnh của một note
    @Query("SELECT COUNT(*) FROM note_images WHERE noteId = :noteId")
    suspend fun getImageCountByNoteId(noteId: Int): Int
    
    // Lấy tổng kích thước hình ảnh của một note
    @Query("SELECT SUM(fileSize) FROM note_images WHERE noteId = :noteId")
    suspend fun getTotalImageSizeByNoteId(noteId: Int): Long?
} 