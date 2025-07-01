package com.example.quicknotes.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.repository.NoteRepository
import com.example.quicknotes.screen.component.NoteItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    repository: NoteRepository,
    onAddNoteClick: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onCompletedNotesClick: () -> Unit,
    onRecordNoteClick: () -> Unit,
    onTranslateClick: () -> Unit,
    onEditNote: (Note) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth < 600) 2 else 3
    var fabExpanded by remember { mutableStateOf(false) }
    
    val rotationAngle by animateFloatAsState(
        targetValue = if (fabExpanded) 45f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Quick Notes",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Organize your thoughts",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        ActionButton(
                            icon = Icons.Default.CheckCircle,
                            label = "Completed Notes",
                            onClick = {
                                fabExpanded = false
                                onCompletedNotesClick()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionButton(
                            icon = Icons.Default.Mic,
                            label = "Record Voice",
                            onClick = {
                                fabExpanded = false
                                onRecordNoteClick()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionButton(
                            icon = Icons.Default.Image,
                            label = "Image Note",
                            onClick = {
                                fabExpanded = false
                                onTranslateClick()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ActionButton(
                            icon = Icons.Default.Add,
                            label = "Text Note",
                            onClick = {
                                fabExpanded = false
                                onAddNoteClick()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = if (fabExpanded) "Close menu" else "Add note",
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }
        }
    ) { paddingValues ->
        var notes by remember { mutableStateOf<List<Note>>(emptyList()) }
        
        LaunchedEffect(Unit) {
            repository.getAllNotes().collect { noteList ->
                notes = noteList
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (notes.isEmpty()) {
                EmptyStateContent(onAddNoteClick = onAddNoteClick)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDelete = { 
                                CoroutineScope(Dispatchers.IO).launch {
                                    repository.deleteNote(note.id)
                                }
                            },
                            onToggleCompleted = { isChecked ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    if (isChecked) {
                                        repository.update(note.copy(isCompleted = true))
                                        
                                        delay(10_000)
                                        repository.completeNote(note.id)
                                    } else {
                                        repository.update(note.copy(isCompleted = false))
                                    }
                                }
                            },
                            onComplete = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    repository.completeNote(note.id)
                                }
                            },
                            onDetailClick = { onNoteClick(note) },
                            onEditClick = { onEditNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateContent(onAddNoteClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No notes yet",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create your first note to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddNoteClick,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Note")
        }
    }
}

