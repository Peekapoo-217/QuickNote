package com.example.quicknotes.screen.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.component.dialog.ReminderDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (note.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            val contentLines = note.content.split("\n")
            val imageLine = contentLines.find { it.startsWith("[image_uri:") }
            val imageUri = imageLine?.removePrefix("[image_uri:")?.removeSuffix("]")
            val textContent = contentLines.filter { !it.startsWith("[image_uri:") }.joinToString("\n")

            Text(
                text = textContent,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (note.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
            )

            if (imageUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(imageUri)),
                    contentDescription = "Note Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Complete button
            Button(
                onClick = { onToggleCompleted(!note.isCompleted) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (note.isCompleted) "Mark as Incomplete" else "Mark as Complete")
            }
        }
    }

    if (showDialog) {
        ReminderDialog(
            note = note,
            onDismiss = { showDialog = false },
            onExpired = { /*  */ }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
} 