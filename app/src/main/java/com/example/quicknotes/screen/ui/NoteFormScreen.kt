package com.example.quicknotes.screen.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknotes.viewmodel.NoteViewModel
import com.example.quicknotes.viewmodel.NoteViewModelFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFormScreen(
    repository: NoteRepository,
    onBackClick: () -> Unit,
    noteToEdit: Note? = null
) {
    val context = LocalContext.current
    val factory = remember { NoteViewModelFactory(repository) }
    val viewModel: NoteViewModel = viewModel(factory = factory)
    var title by rememberSaveable { mutableStateOf(noteToEdit?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(noteToEdit?.content ?: "") }
    var colorTag by rememberSaveable { mutableStateOf(noteToEdit?.colorTag ?: "") }
    var reminderTime by rememberSaveable { mutableStateOf(noteToEdit?.reminderTime) }

    val calendar = Calendar.getInstance()

    val priorities = listOf(
        "High" to "red",
        "Medium" to "orange",
        "Low" to "green"
    )

    val colorMap = mapOf(
        "red" to Color.Red,
        "orange" to Color(0xFFFF9800),
        "green" to Color(0xFF4CAF50)
    )

    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedPriority = priorities.find { it.second == colorTag }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteToEdit != null) "Chỉnh sửa ghi chú" else "Tạo ghi chú mới") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        // Canh giữa giữa màn hình
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Khung chứa nội dung
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .padding(horizontal = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Row: Set Reminder + Priority
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Reminder (70%)
                        Button(
                            onClick = {
                                val now = Calendar.getInstance()
                                DatePickerDialog(context, { _, year, month, day ->
                                    TimePickerDialog(context, { _, hour, minute ->
                                        calendar.set(year, month, day, hour, minute)
                                        reminderTime = calendar.timeInMillis
                                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(0.7f)
                        ) {
                            Text("Set Reminder")
                        }

                        // Priority dropdown (30%)
                        Box(modifier = Modifier.weight(0.3f)) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(colorMap[colorTag] ?: Color.Gray)
                                    )
                                    Text(
                                        text = selectedPriority?.first ?: "Priority",
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                priorities.forEach { (label, color) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .background(colorMap[color] ?: Color.Gray)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1
                                                )
                                            }
                                        },
                                        onClick = {
                                            colorTag = color
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Save / Cancel buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    val note = Note(
                                        id = noteToEdit?.id ?: 0,
                                        title = title,
                                        content = content,
                                        colorTag = colorTag.ifBlank { "none" },
                                        reminderTime = reminderTime,
                                        isCompleted = noteToEdit?.isCompleted ?: false,
                                        imageUri = noteToEdit?.imageUri
                                    )
                                    if (noteToEdit != null) {
                                        viewModel.update(note)
                                    } else {
                                        viewModel.insert(note)
                                    }
                                    onBackClick()
                                } else {
                                    Toast.makeText(context, "Vui lòng nhập đủ tiêu đề và nội dung", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (noteToEdit != null) "Lưu" else "Tạo mới")
                        }

                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

