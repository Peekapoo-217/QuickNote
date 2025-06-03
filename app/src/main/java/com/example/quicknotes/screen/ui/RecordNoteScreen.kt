package com.example.quicknotes.screen.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.utilities.speech.SpeechToTextHelper
import com.example.quicknotes.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordNoteScreen(viewModel: NoteViewModel, navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isRecording by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val helper = remember {
        activity?.let {
            SpeechToTextHelper(
                it,
                onResult = { text ->
                    resultText = text
                    isRecording = false
                    val note = Note(
                        title = "Voice Note ${System.currentTimeMillis()}",
                        content = text,
                        colorTag = "none"
                    )
                    scope.launch { viewModel.insert(note)
                        Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show() }
                },
                onError = { error ->
                    resultText = error
                    isRecording = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Voice Note") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!isRecording) {
                        isRecording = true
                        helper?.startListening()
                    } else {
                        isRecording = false
                        helper?.stop()
                    }
                },
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Voice to Note", style = MaterialTheme.typography.headlineSmall)

            Spacer(Modifier.height(32.dp))

            if (isRecording) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text("Listening...", style = MaterialTheme.typography.bodyMedium)
            }

            if (resultText.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Transcribed Text", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resultText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}