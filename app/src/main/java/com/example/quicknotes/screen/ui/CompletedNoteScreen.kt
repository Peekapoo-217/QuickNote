package com.example.quicknotes.screen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.CompletedNote
import com.example.quicknotes.screen.component.CompletedNoteItem
import com.example.quicknotes.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedNotesScreen(
    completedNotes: List<CompletedNote>,
    onBack: () -> Unit,
    viewModel: NoteViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ghi chú đã hoàn thành") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            items(completedNotes) { note ->
                CompletedNoteItem(
                    note = note,
                    onDeleteConfirmed = { viewModel.deleteCompleted(it) }
                )
            }
        }
    }
}

