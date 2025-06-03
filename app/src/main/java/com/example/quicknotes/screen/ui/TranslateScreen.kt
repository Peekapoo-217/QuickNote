package com.example.quicknotes.screen.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.utilities.ocr.TextRecognitionHelper
import com.example.quicknotes.utilities.translate.TranslationHelper
import com.example.quicknotes.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(viewModel: NoteViewModel) {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var originalText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            originalText = ""
            translatedText = "Processing..."

            TextRecognitionHelper.recognizeText(context, it) { text ->
                originalText = text
                TranslationHelper.translateText(context, text) { translated ->
                    translatedText = translated
                    val newNote = Note(
                        title = "Translated Image ${System.currentTimeMillis()}",
                        content = "$translated\n\n[Original OCR: $text]\n[image_uri:${imageUri.toString()}]",
                        createdAt = System.currentTimeMillis(),
                        isCompleted = false,
                        reminderTime = null,
                        colorTag = "none"
                    )
                    viewModel.insert(newNote)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Image Text Translation") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Select Image Button
            ElevatedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("📷 Select Image")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display selected image (no card)
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context).data(it).build()
                    ),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Display OCR result (selectable)
            if (originalText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📝 Recognized Text", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        SelectionContainer {
                            Text(originalText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Display translated result (selectable)
            if (translatedText.isNotBlank()) {
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🌍 Translated Text", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        SelectionContainer {
                            Text(translatedText)
                        }
                    }
                }
            }
        }
    }
}
