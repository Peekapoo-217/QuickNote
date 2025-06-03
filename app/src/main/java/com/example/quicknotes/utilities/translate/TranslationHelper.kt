package com.example.quicknotes.utilities.translate

import android.content.Context
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslationHelper {
    fun translateText(context: Context, sourceText: String, callback: (String) -> Unit) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.VIETNAMESE)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(sourceText)
                    .addOnSuccessListener { translatedText ->
                        callback(translatedText)
                    }
                    .addOnFailureListener {
                        callback("Lỗi dịch: ${it.message}")
                    }
            }
            .addOnFailureListener {
                callback("Không tải được model: ${it.message}")
            }
    }
}
