package com.example.quicknotes.screen.component

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.component.dialog.ReminderDialog
import kotlinx.coroutines.delay

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleCompleted: (Boolean) -> Unit,
    onComplete: () -> Unit,
    onDetailClick: () -> Unit
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

    // Kiểm tra xem note có hết hạn chưa
    val isExpired = note.reminderTime?.let { System.currentTimeMillis() > it } ?: false

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Sử dụng imageUri từ database thay vì parse từ content
    val imageUri = note.imageUri

    val animatedBgColor by animateColorAsState(
        if (isPressed) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        else if (isExpired) Color(0xFFF5F5F5)
        else MaterialTheme.colorScheme.surface,
        label = "CardColor"
    )

    val textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None
    val textAlpha = if (note.isCompleted) 0.5f else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple()
            ) {
                showDialog = true
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpired) 6.dp else 4.dp
        ),
        border = if (isExpired) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null
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
                        textDecoration = textDecoration,
                        fontWeight = if (isExpired) FontWeight.Medium else FontWeight.Normal
                    ),
                    color = if (isExpired) Color(0xFF757575) else LocalContentColor.current.copy(alpha = textAlpha),
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
                        text = { Text("Detail") },
                        onClick = {
                            expanded = false
                            onDetailClick()
                        }
                    )
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
                    textDecoration = textDecoration,
                    fontWeight = if (isExpired) FontWeight.Normal else FontWeight.Normal
                ),
                color = if (isExpired) Color(0xFF757575) else LocalContentColor.current.copy(alpha = textAlpha),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(imageUri)),
                    contentDescription = "Note image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = tagLabel,
                        color = Color.White
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = when (note.colorTag) {
                        "red" -> Color(0xFFE57373)
                        "orange" -> Color(0xFFFFB74D)
                        "green" -> Color(0xFF81C784)
                        else -> Color.Gray.copy(alpha = 0.6f)
                    }
                ),
                border = null
            )
        }
    }

    if (showDialog) {
        ReminderDialog(
            note = note,
            onDismiss = { showDialog = false },
            onExpired = { /*  */ }
        )
    }
}