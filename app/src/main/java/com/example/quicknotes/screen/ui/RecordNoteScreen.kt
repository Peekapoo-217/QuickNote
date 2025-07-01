package com.example.quicknotes.screen.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.utilities.speech.SpeechToTextHelper
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknotes.viewmodel.NoteViewModel
import com.example.quicknotes.viewmodel.NoteViewModelFactory
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordNoteScreen(repository: NoteRepository, navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isRecording by rememberSaveable { mutableStateOf(false) }
    var resultText by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)

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
                    scope.launch {
                        try {
                            viewModel.insert(note)
                            Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error saving note: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onError = { error ->
                    resultText = error
                    isRecording = false
                    Toast.makeText(context, "Recording error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Cleanup helper when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            helper?.cleanup()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Voice Note") })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Hold to record",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(32.dp))
                val infiniteTransition = rememberInfiniteTransition(label = "mic_wave")
                val waveScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.4f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "mic_wave_anim"
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isRecording = true
                                    helper?.startListening()
                                    try {
                                        awaitRelease()
                                    } finally {
                                        isRecording = false
                                        helper?.stop()
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val scale = if (isRecording) waveScale else 1f
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.GraphicEq else Icons.Outlined.Mic,
                        contentDescription = if (isRecording) "Recording" else "Start Recording",
                        tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp).graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                    )
                }
                Spacer(Modifier.height(32.dp))
                if (isRecording) {
                    Text(
                        "Listening...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (resultText.isNotBlank()) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "Result:",
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}