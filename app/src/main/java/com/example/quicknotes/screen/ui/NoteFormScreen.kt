package com.example.quicknotes.screen.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import java.util.*

@Composable
fun NoteFormScreen(
    onSave: (Note) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var colorTag by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }

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

    // Canh giữa giữa màn hình
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Khung chứa nội dung
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
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
                                    onSave(
                                        Note(
                                            title = title,
                                            content = content,
                                            colorTag = colorTag.ifBlank { "none" },
                                            reminderTime = reminderTime
                                        )
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Title & Content required",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }

