package com.example.quicknotes.screen.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.utilities.ocr.TextRecognitionHelper
import com.example.quicknotes.utilities.translate.TranslationHelper
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(repository: NoteRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var originalText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            originalText = ""
            translatedText = "Processing..."
            isProcessing = true

            TextRecognitionHelper.recognizeText(context, it) { text ->
                originalText = text
                TranslationHelper.translateText(context, text) { translated ->
                    translatedText = translated
                    isProcessing = false
                    
                    // Tạo note mới với content không chứa image URI
                    val newNote = Note(
                        title = "Translated Image ${System.currentTimeMillis()}",
                        content = "$translated\n\n[Original OCR: $text]",
                        createdAt = System.currentTimeMillis(),
                        isCompleted = false,
                        reminderTime = null,
                        colorTag = "none",
                        imageUri = null // Sẽ được cập nhật sau khi lưu ảnh
                    )
                    
                    // Lưu note trước, sau đó lưu ảnh
                    scope.launch(Dispatchers.IO) {
                        val noteId = repository.insertNoteAndGetId(newNote)
                        if (noteId > 0) {
                            // Lưu ảnh vào database
                            val savedImage = repository.addImageToNote(
                                noteId = noteId,
                                imageUri = it,
                                description = "Translated image"
                            )
                            
                            // Cập nhật note với imageUri chính
                            if (savedImage != null) {
                                repository.update(newNote.copy(
                                    id = noteId,
                                    imageUri = savedImage.imageUri
                                ))
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Translation") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Select Image Button
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Text("Select Image to Translate")
                }
            }

            // Display selected image
            imageUri?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(it).build(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Display OCR result
            if (originalText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.TextFields,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("Recognized Text")
                        }
                        SelectionContainer {
                            Text(originalText)
                        }
                    }
                }
            }

            // Display translated result
            if (translatedText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("Translated Text")
                        }
                        SelectionContainer {
                            Text(translatedText)
                        }
                    }
                }
            }

            // Processing indicator
            if (isProcessing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Processing image...")
                    }
                }
            }
        }
    }
}
