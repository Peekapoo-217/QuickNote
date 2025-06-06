package com.example.quicknotes.screen.component.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quicknotes.data.local.entity.Note
import kotlinx.coroutines.delay
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun ReminderDialog(note: Note, onDismiss: () -> Unit) {
    val context = LocalContext.current

    var remainingTime by remember {
        mutableStateOf(
            note.reminderTime?.minus(System.currentTimeMillis())?.coerceAtLeast(0L) ?: 0L
        )
    }

    // Cập nhật liên tục mỗi giây
    LaunchedEffect(note.reminderTime) {
        while (remainingTime > 0) {
            delay(1000)
            remainingTime = note.reminderTime?.minus(System.currentTimeMillis())?.coerceAtLeast(0L) ?: 0L
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("Reminder Info", style = MaterialTheme.typography.titleLarge) },
        text = {
            if (note.reminderTime != null) {
                val totalSeconds = remainingTime / 1000
                val days = totalSeconds / (60 * 60 * 24)
                val hours = (totalSeconds / 3600) % 24
                val minutes = (totalSeconds / 60) % 60
                val seconds = totalSeconds % 60

                Text(
                    text = buildString {
                        append("Remind at: ${Date(note.reminderTime)}\n")
                        append("Time left: ")
                        if (days > 0) append("$days d ")
                        if (hours > 0 || days > 0) append("$hours h ")
                        if (minutes > 0 || hours > 0 || days > 0) append("$minutes m ")
                        append("$seconds s")
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text("No reminder set for this note.")
            }
        },
        modifier = Modifier.padding(24.dp)
    )
}

