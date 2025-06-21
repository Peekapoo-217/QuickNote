package com.example.quicknotes.utilities

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageStorageHelper(private val context: Context) {
    
    companion object {
        private const val IMAGE_DIRECTORY = "note_images"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    }
    
    /**
     * Lưu ảnh từ URI vào storage của app
     */
    suspend fun saveImageFromUri(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) return null
            
            // Tạo thư mục nếu chưa tồn tại
            val imageDir = File(context.filesDir, IMAGE_DIRECTORY)
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            
            // Tạo tên file duy nhất
            val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
            val fileName = "IMG_${timeStamp}_${System.currentTimeMillis()}.jpg"
            val imageFile = File(imageDir, fileName)
            
            // Copy file
            val outputStream = FileOutputStream(imageFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            // Trả về đường dẫn tuyệt đối
            imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Lưu ảnh từ byte array
     */
    suspend fun saveImageFromBytes(imageBytes: ByteArray): String? {
        return try {
            val imageDir = File(context.filesDir, IMAGE_DIRECTORY)
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            
            val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
            val fileName = "IMG_${timeStamp}_${System.currentTimeMillis()}.jpg"
            val imageFile = File(imageDir, fileName)
            
            val outputStream = FileOutputStream(imageFile)
            outputStream.write(imageBytes)
            outputStream.close()
            
            imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Xóa file ảnh
     */
    suspend fun deleteImage(imagePath: String): Boolean {
        return try {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                imageFile.delete()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Xóa tất cả ảnh của một note
     */
    suspend fun deleteImagesByNoteId(noteId: Int, imagePaths: List<String>): Boolean {
        return try {
            imagePaths.forEach { path ->
                deleteImage(path)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Lấy MIME type từ URI
     */
    fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "image/jpeg"
    }
    
    /**
     * Lấy extension từ MIME type
     */
    fun getExtensionFromMimeType(mimeType: String): String {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
    }
    
    /**
     * Lấy kích thước file
     */
    fun getFileSize(filePath: String): Long {
        val file = File(filePath)
        return if (file.exists()) file.length() else 0L
    }
    
    /**
     * Kiểm tra file có tồn tại không
     */
    fun isImageExists(imagePath: String): Boolean {
        return File(imagePath).exists()
    }
    
    /**
     * Lấy tổng kích thước thư mục ảnh
     */
    fun getTotalImageDirectorySize(): Long {
        val imageDir = File(context.filesDir, IMAGE_DIRECTORY)
        return if (imageDir.exists()) {
            imageDir.walkTopDown()
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else 0L
    }
    
    /**
     * Dọn dẹp ảnh không sử dụng
     */
    suspend fun cleanupUnusedImages(usedImagePaths: List<String>): Int {
        val imageDir = File(context.filesDir, IMAGE_DIRECTORY)
        if (!imageDir.exists()) return 0
        
        var deletedCount = 0
        imageDir.walkTopDown()
            .filter { it.isFile }
            .forEach { file ->
                if (!usedImagePaths.contains(file.absolutePath)) {
                    if (file.delete()) {
                        deletedCount++
                    }
                }
            }
        
        return deletedCount
    }
} 