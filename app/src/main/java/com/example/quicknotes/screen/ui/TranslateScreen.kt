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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknotes.viewmodel.NoteViewModel
import com.example.quicknotes.viewmodel.NoteViewModelFactory
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.mlkit.nl.languageid.LanguageIdentification
import androidx.core.content.FileProvider
import java.io.File
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(repository: NoteRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var originalText by rememberSaveable { mutableStateOf("") }
    var translatedText by rememberSaveable { mutableStateOf("") }
    var isProcessing by rememberSaveable { mutableStateOf(false) }
    var cameraImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Hàm tạo file tạm cho ảnh chụp
    fun createImageFile(context: Context): File {
        val storageDir = context.cacheDir
        return File.createTempFile(
            "camera_image_", ".jpg", storageDir
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            originalText = ""
            translatedText = "Processing..."
            isProcessing = true

            // 1. OCR
            TextRecognitionHelper.recognizeText(context, it) { ocrText ->
                originalText = ocrText
                // 2. Dịch
                TranslationHelper.translateText(context, ocrText) { translated ->
                    translatedText = translated
                    isProcessing = false

                    // 3. Tạo note mới
                    val newNote = Note(
                        title = "Translated Image ${System.currentTimeMillis()}",
                        content = "$translated\n\n[Original OCR: $ocrText]",
                        createdAt = System.currentTimeMillis(),
                        isCompleted = false,
                        reminderTime = null,
                        colorTag = "none",
                        imageUri = null
                    )
                    scope.launch(Dispatchers.IO) {
                        val noteId = repository.insertNoteAndGetId(newNote)
                        if (noteId > 0) {
                            val savedImage = repository.addImageToNote(
                                noteId = noteId,
                                imageUri = it,
                                description = "Translated image"
                            )
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            imageUri = cameraImageUri
            originalText = ""
            translatedText = "Processing..."
            isProcessing = true

            TextRecognitionHelper.recognizeText(context, cameraImageUri!!) { ocrText ->
                originalText = ocrText
                TranslationHelper.translateText(context, ocrText) { translated ->
                    translatedText = translated
                    isProcessing = false

                    val newNote = Note(
                        title = "Translated Image ${System.currentTimeMillis()}",
                        content = "$translated\n\n[Original OCR: $ocrText]",
                        createdAt = System.currentTimeMillis(),
                        isCompleted = false,
                        reminderTime = null,
                        colorTag = "none",
                        imageUri = null
                    )
                    scope.launch(Dispatchers.IO) {
                        val noteId = repository.insertNoteAndGetId(newNote)
                        if (noteId > 0) {
                            val savedImage = repository.addImageToNote(
                                noteId = noteId,
                                imageUri = cameraImageUri!!,
                                description = "Translated image"
                            )
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

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingCameraUri?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
    }

    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)

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
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Select Image Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Chọn ảnh")
                }
                OutlinedButton(
                    onClick = {
                        // Tạo file tạm để lưu ảnh chụp
                        val photoFile = createImageFile(context)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            photoFile
                        )
                        cameraImageUri = uri
                        // Kiểm tra quyền CAMERA trước khi mở camera
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Nếu đã có quyền, mở camera luôn
                            cameraLauncher.launch(uri)
                        } else {
                            // Nếu chưa có quyền, lưu Uri tạm và xin quyền CAMERA
                            pendingCameraUri = uri
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Chụp ảnh")
                }
            }
            // Display selected image
            imageUri?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(it).build(),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // Processing indicator
            if (isProcessing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Processing...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            // Display OCR result
            if (originalText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.TextFields,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Recognized Text",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        SelectionContainer {
                            Text(
                                originalText,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
            // Display translated result
            if (translatedText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Translated Text",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        SelectionContainer {
                            Text(
                                translatedText,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }
    }
}
