package com.example.quicknotes.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.component.NoteItem
import com.example.quicknotes.viewmodel.NoteViewModel

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    viewModel: NoteViewModel,
    onAddImageNote: () -> Unit,
    onViewCompletedNotes: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth < 600) 2 else 3

    val fabExpanded = remember { mutableStateOf(false) }


    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Quick Notes") })
    }, floatingActionButton = {
        Box {
            FloatingActionButton(onClick = { fabExpanded.value = !fabExpanded.value }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

            DropdownMenu(expanded = fabExpanded.value,
                onDismissRequest = { fabExpanded.value = false }) {
                DropdownMenuItem(text = { Text("Thêm ghi chú văn bản") }, onClick = {
                    fabExpanded.value = false
                    onAddNote()
                })
                DropdownMenuItem(text = { Text("Thêm từ hình ảnh") }, onClick = {
                    fabExpanded.value = false
                    onAddImageNote()
                })
            }
        }
    }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(note = note,
                    onClick = { onNoteClick(note) },
                    onDelete = { viewModel.delete(note) },
                    onToggleCompleted = { isChecked ->
                        viewModel.update(note.copy(isCompleted = isChecked))
                    },
                    onComplete = {
                        viewModel.completeNote(note)
                    }
                )
            }
        }
    }
}
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit,
    viewModel: NoteViewModel,
    onAddImageNote: () -> Unit,
    onViewCompletedNotes: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth < 600) 2 else 3

    val fabExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Quick Notes") })
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { fabExpanded.value = !fabExpanded.value }) {
                    Icon(Icons.Default.Add, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = fabExpanded.value,
                    onDismissRequest = { fabExpanded.value = false },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text("Thêm ghi chú văn bản") },
                        onClick = {
                            fabExpanded.value = false
                            onAddNote()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Thêm từ hình ảnh") },
                        onClick = {
                            fabExpanded.value = false
                            onAddImageNote()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Xem ghi chú đã hoàn thành") },
                        onClick = {
                            fabExpanded.value = false
                            onViewCompletedNotes()
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note) },
                    onDelete = { viewModel.delete(note) },
                    onToggleCompleted = { isChecked ->
                        viewModel.update(note.copy(isCompleted = isChecked))
                    },
                    onComplete = {
                        viewModel.completeNote(note)
                    }
                )
            }
        }
    }
}

