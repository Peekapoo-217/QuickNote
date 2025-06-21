package com.example.quicknotes.screen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedNoteScreen(
    repository: NoteRepository,
    onBackClick: () -> Unit
) {
    var completedNotes by remember { mutableStateOf<List<CompletedNote>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        repository.getAllCompletedNotes().collect { notes ->
            completedNotes = notes
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ghi chú đã hoàn thành") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(completedNotes) { completedNote ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = completedNote.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = completedNote.content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Hoàn thành",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        repository.deleteCompleted(completedNote)
                                    }
                                }
                            ) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }
        }
    }
}

