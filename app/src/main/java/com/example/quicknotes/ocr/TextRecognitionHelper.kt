package com.example.quicknotes.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object TextRecognitionHelper {
    fun recognizeText(context: Context, uri: Uri, callback: (String) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    callback(result.text)
                }
                .addOnFailureListener {
                    callback(" Lỗi OCR: ${it.localizedMessage}")
                }
        } catch (e: Exception) {
            callback("Lỗi ảnh: ${e.localizedMessage}")
        }
    }
}