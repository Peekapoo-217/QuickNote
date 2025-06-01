package com.example.quicknotes.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextDecoration
import kotlinx.coroutines.delay
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults

/*@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    val colorMap = mapOf(
        "red" to Color.Red,
        "orange" to Color(0xFFFF9800),
        "green" to Color(0xFF4CAF50),
        "none" to Color.Gray
    )
    val labelMap = mapOf(
        "red" to "High", "orange" to "Medium", "green" to "Low", "none" to "None"
    )
    val tagColor = colorMap[note.colorTag] ?: Color.Gray
    val tagLabel = labelMap[note.colorTag] ?: "None"

    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor by animateColorAsState(
        if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else MaterialTheme.colorScheme.background,
        label = "PressedColorAnimation"
    )

    val textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
    val textAlpha = if (note.isCompleted) 0.5f else 1f


    LaunchedEffect(note.isCompleted) {
        if (note.isCompleted) {
            delay(10000)
            onComplete()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple()
            ) {
                showDialog = true
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        border = BorderStroke(1.5.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = note.isCompleted,
                    onCheckedChange = { onToggleCompleted(it) }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = textDecoration
                        ),
                        color = LocalContentColor.current.copy(alpha = textAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tagLabel,
                        color = tagColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Box {
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = textDecoration
                ),
                color = LocalContentColor.current.copy(alpha = textAlpha),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showDialog) {
        ReminderDialog(note = note, onDismiss = { showDialog = false })
    }
}*/

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onComplete: () -> Unit
) {
    val tagColors = mapOf(
        "red" to Color(0xFFFFCDD2),
        "orange" to Color(0xFFFFE0B2),
        "green" to Color(0xFFC8E6C9),
        "none" to MaterialTheme.colorScheme.surfaceVariant
    )

    val tagLabels = mapOf(
        "red" to "High",
        "orange" to "Medium",
        "green" to "Low",
        "none" to "None"
    )

    val tagColor = tagColors[note.colorTag] ?: MaterialTheme.colorScheme.surfaceVariant
    val tagLabel = tagLabels[note.colorTag] ?: "None"

    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedBgColor by animateColorAsState(
        if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        else MaterialTheme.colorScheme.surface,
        label = "CardColor"
    )


    val textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
    val textAlpha = if (note.isCompleted) 0.5f else 1f

// Auto redirect completed-list
    LaunchedEffect(note.isCompleted) {
        if (note.isCompleted) {
            delay(10_000)
            onComplete()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple()
            ) {
                showDialog = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = note.isCompleted,
                    onCheckedChange = onToggleCompleted,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDecoration = textDecoration
                    ),
                    color = LocalContentColor.current.copy(alpha = textAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = textDecoration
                ),
                color = LocalContentColor.current.copy(alpha = textAlpha),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))


            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = tagLabel,
                        color = Color.White // chữ trắng cho nổi
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (note.colorTag) {
                        "red" -> Color(0xFFE57373)     // đỏ đậm
                        "orange" -> Color(0xFFFFB74D)  // cam đậm
                        "green" -> Color(0xFF81C784)   // xanh lá dịu
                        else -> Color.Gray.copy(alpha = 0.6f)
                    }
                ),
                border = null
            )

        }
    }

    if (showDialog) {
        ReminderDialog(note = note, onDismiss = { showDialog = false })
    }
}











