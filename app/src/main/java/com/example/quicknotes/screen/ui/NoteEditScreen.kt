package com.example.quicknotes.screen.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    note: Note,
    repository: NoteRepository,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }
    var colorTag by remember { mutableStateOf(note.colorTag) }
    var reminderTime by remember { mutableStateOf(note.reminderTime) }
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
    var expanded by remember { mutableStateOf(false) }
    val selectedPriority = priorities.find { it.second == colorTag }
    val reminderFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa ghi chú") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    // Priority & Reminder
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Priority dropdown
                        Box(modifier = Modifier.weight(0.5f)) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = colorMap[colorTag] ?: Color.Gray)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(selectedPriority?.first ?: "Priority", style = MaterialTheme.typography.labelSmall)
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
                                                Spacer(modifier = Modifier.size(6.dp))
                                                Text(label, style = MaterialTheme.typography.labelSmall)
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
                        // Reminder
                        OutlinedButton(
                            onClick = {
                                val now = Calendar.getInstance()
                                DatePickerDialog(context, { _, year, month, day ->
                                    TimePickerDialog(context, { _, hour, minute ->
                                        calendar.set(year, month, day, hour, minute)
                                        reminderTime = calendar.timeInMillis
                                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                            },
                            modifier = Modifier.weight(0.5f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                if (reminderTime != null) reminderFormat.format(Date(reminderTime!!)) else "Set Reminder",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    // Thông báo deadline theo màu sắc
                    val reminderMinutes = when (colorTag) {
                        "red" -> 30
                        "orange" -> 10
                        "green" -> 5
                        else -> 30
                    }
                    if (colorTag.isBlank()) {
                        Text(
                            text = "Lưu ý: chọn priority...",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    } else {
                        Text(
                            text = "Gợi ý: Với màu này, bạn sẽ được nhắc trước $reminderMinutes phút trước deadline.",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorMap[colorTag] ?: Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                    }
                    // Save / Cancel buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    val updatedNote = note.copy(
                                        title = title,
                                        content = content,
                                        colorTag = colorTag,
                                        reminderTime = reminderTime
                                    )
                                    CoroutineScope(Dispatchers.IO).launch {
                                        repository.update(updatedNote)
                                    }
                                    Toast.makeText(context, "Note updated", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } else {
                                    Toast.makeText(context, "Title & Content required", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
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