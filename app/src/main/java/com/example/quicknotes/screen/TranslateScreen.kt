package com.example.quicknotes.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.quicknotes.ocr.TextRecognitionHelper
import com.example.quicknotes.translate.TranslationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen() {
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
            translatedText = "Đang xử lý..."

            TextRecognitionHelper.recognizeText(context, it) { text ->
                originalText = text
                TranslationHelper.translateText(context, text) { translated ->
                    translatedText = translated
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dịch Ảnh Sang Tiếng Việt") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Chọn Ảnh")
                }

                Spacer(modifier = Modifier.height(16.dp))

                imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context).data(it).build()
                        ),
                        contentDescription = "Ảnh đã chọn",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (originalText.isNotBlank()) {
                    Text("📄 Văn bản OCR:", style = MaterialTheme.typography.titleMedium)
                    Text(originalText)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (translatedText.isNotBlank()) {
                    Text("🌍 Bản dịch tiếng Việt:", style = MaterialTheme.typography.titleMedium)
                    Text(translatedText)
                }
            }
        }
    }
}