package com.example.quicknotes.screen.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.CompletedNote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompletedNoteItem(note: CompletedNote, onDeleteConfirmed: (CompletedNote) -> Unit) {

    var showDialog = remember { mutableStateOf(false) }

    val colorMap = mapOf(
        "red" to Color(0xFFFFCDD2),
        "orange" to Color(0xFFFFE0B2),
        "green" to Color(0xFFC8E6C9),
        "none" to MaterialTheme.colorScheme.surfaceVariant
    )

    val tagLabelMap = mapOf(
        "red" to "High",
        "orange" to "Medium",
        "green" to "Low",
        "none" to "None"
    )

    val bgColor = colorMap[note.colorTag] ?: MaterialTheme.colorScheme.surfaceVariant
    val tagLabel = tagLabelMap[note.colorTag] ?: "None"

    val formattedDate =
        SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(note.completedAt))

    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(
            onClick = {},
            onLongClick = { showDialog.value = true }
        ),

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = true,
                    onCheckedChange = null, // Không cho thay đổi
                    enabled = false // Không thể tương tác
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium.copy(textDecoration = TextDecoration.LineThrough),
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Priority: $tagLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                color = LocalContentColor.current.copy(alpha = 0.6f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hoàn thành lúc: $formattedDate",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc muốn xóa ghi chú này không?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onDeleteConfirmed(note)
                            showDialog.value = false
                        }) {
                            Text("Xóa")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog.value = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}
