package com.example.quicknotes.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_images",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteImage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val noteId: Int, // ID của note mà hình ảnh thuộc về
    val imageUri: String, // URI của hình ảnh
    val fileName: String, // Tên file
    val fileSize: Long, // Kích thước file (bytes)
    val mimeType: String, // Loại file (image/jpeg, image/png, etc.)
    val createdAt: Long = System.currentTimeMillis(),
    val description: String? = null // Mô tả hình ảnh (optional)
) 