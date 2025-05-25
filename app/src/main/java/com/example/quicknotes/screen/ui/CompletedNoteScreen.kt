package com.example.quicknotes.screen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.CompletedNote
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedNotesScreen(
    completedNotes: List<CompletedNote>,
    onBack: () -> Unit
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
                CompletedNoteItem(note)
            }
        }
    }
}

@Composable
fun CompletedNoteItem(note: CompletedNote) {
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

    val formattedDate = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(note.completedAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                color = LocalContentColor.current.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hoàn thành lúc: $formattedDate",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
