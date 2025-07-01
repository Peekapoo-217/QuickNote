package com.example.quicknotes.screen.ui

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.data.local.entity.NoteImage
import com.example.quicknotes.screen.component.dialog.ReminderDialog
import com.example.quicknotes.repository.NoteRepository
import com.example.quicknotes.viewmodel.NoteViewModel
import com.example.quicknotes.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    repository: NoteRepository,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit
) {
    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var noteImages by rememberSaveable { mutableStateOf<List<NoteImage>>(emptyList()) }

    // Lấy ảnh của note từ database
    LaunchedEffect(note.id) {
        repository.getImagesByNoteId(note.id).collectLatest { images ->
            noteImages = images
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Note Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .background(
                                Color(0xFFFFEBEE),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with enhanced design
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    animationSpec = tween(600),
                    initialOffsetY = { -it / 2 }
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp)
                    ) {
                        // Priority indicator with enhanced design
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height(48.dp)
                                    .background(
                                        color = when (note.colorTag) {
                                            "red" -> Color(0xFFE57373)
                                            "orange" -> Color(0xFFFFB74D)
                                            "green" -> Color(0xFF81C784)
                                            else -> Color(0xFF9E9E9E)
                                        },
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .shadow(
                                        elevation = 4.dp,
                                        spotColor = when (note.colorTag) {
                                            "red" -> Color(0xFFE57373)
                                            "orange" -> Color(0xFFFFB74D)
                                            "green" -> Color(0xFF81C784)
                                            else -> Color(0xFF9E9E9E)
                                        }.copy(alpha = 0.3f)
                                    )
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                // Title with enhanced typography
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                        lineHeight = 28.sp
                                    ),
                                    color = if (note.isCompleted)
                                        Color.Gray.copy(alpha = 0.7f)
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Enhanced metadata layout
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Created date with better styling
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = SimpleDateFormat(
                                                "EEEE, MMM dd, yyyy",
                                                Locale.getDefault()
                                            )
                                                .format(Date(note.createdAt)),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Priority and status row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Enhanced priority chip
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = when (note.colorTag) {
                                                "red" -> Color(0xFFE57373)
                                                "orange" -> Color(0xFFFFB74D)
                                                "green" -> Color(0xFF81C784)
                                                else -> Color(0xFF9E9E9E)
                                            }.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                when (note.colorTag) {
                                                    "red" -> Color(0xFFE57373)
                                                    "orange" -> Color(0xFFFFB74D)
                                                    "green" -> Color(0xFF81C784)
                                                    else -> Color(0xFF9E9E9E)
                                                }.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                )
                                            ) {
                                                Icon(
                                                    when (note.colorTag) {
                                                        "red" -> Icons.Default.PriorityHigh
                                                        "orange" -> Icons.Default.Warning
                                                        "green" -> Icons.Default.CheckCircle
                                                        else -> Icons.Default.Info
                                                    },
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = when (note.colorTag) {
                                                        "red" -> Color(0xFFD32F2F)
                                                        "orange" -> Color(0xFFF57C00)
                                                        "green" -> Color(0xFF388E3C)
                                                        else -> Color(0xFF757575)
                                                    }
                                                )
                                                Text(
                                                    text = when (note.colorTag) {
                                                        "red" -> "High Priority"
                                                        "orange" -> "Medium Priority"
                                                        "green" -> "Low Priority"
                                                        else -> "No Priority"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = when (note.colorTag) {
                                                        "red" -> Color(0xFFD32F2F)
                                                        "orange" -> Color(0xFFF57C00)
                                                        "green" -> Color(0xFF388E3C)
                                                        else -> Color(0xFF757575)
                                                    }
                                                )
                                            }
                                        }

                                        // Enhanced status indicator
                                        if (note.isCompleted) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = Color(0xFF4CAF50)
                                                )
                                                Text(
                                                    text = "Completed",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.SemiBold
                                                    ),
                                                    color = Color(0xFF4CAF50)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Content Section with enhanced design
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) + slideInVertically(
                    animationSpec = tween(800, delayMillis = 200),
                    initialOffsetY = { -it / 3 }
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Content",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = if (note.isCompleted)
                                Color.Gray.copy(alpha = 0.7f)
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Images Section with enhanced design
            if (noteImages.isNotEmpty()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(
                            1000,
                            delayMillis = 400
                        )
                    ) + slideInVertically(
                        animationSpec = tween(1000, delayMillis = 400),
                        initialOffsetY = { -it / 4 }
                    )
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(20.dp),
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Images (${noteImages.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Main image with enhanced design
                            note.imageUri?.let { mainImageUri ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(Uri.parse(mainImageUri)),
                                        contentDescription = "Main Note Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            // Additional images with enhanced design
                            if (noteImages.size > 1) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Additional Images",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    items(noteImages.filter { it.imageUri != note.imageUri }) { image ->
                                        Card(
                                            modifier = Modifier
                                                .size(160.dp)
                                                .shadow(
                                                    elevation = 6.dp,
                                                    shape = RoundedCornerShape(16.dp)
                                                ),
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(Uri.parse(image.imageUri)),
                                                contentDescription = "Additional Image",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDialog) {
        ReminderDialog(
            note = note,
            onDismiss = { showDialog = false },
            onExpired = { /*  */ }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Note",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this note? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 