package com.example.quicknotes.screen.component.dialog

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quicknotes.data.local.entity.Note
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderDialog(
    note: Note,
    onDismiss: () -> Unit,
    onExpired: () -> Unit
) {
    val context = LocalContext.current

    var remainingTime by remember {
        mutableStateOf(
            note.reminderTime?.minus(System.currentTimeMillis())?.coerceAtLeast(0L) ?: 0L
        )
    }

    var isExpired by remember { mutableStateOf(false) }

    // Cập nhật liên tục mỗi giây
    LaunchedEffect(note.reminderTime) {
        while (remainingTime > 0) {
            delay(1000)
            remainingTime = note.reminderTime?.minus(System.currentTimeMillis())?.coerceAtLeast(0L) ?: 0L
        }
        // Khi hết thời gian
        if (!isExpired) {
            isExpired = true
            android.widget.Toast.makeText(context, "Note '${note.title}' has expired!", android.widget.Toast.LENGTH_LONG).show()
            onExpired()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with icon
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                        animationSpec = tween(400),
                        initialScale = 0.9f
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = if (isExpired) Color(0xFFE57373).copy(alpha = 0.15f) else Color(0xFF81C784).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpired) Icons.Default.Warning else Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = if (isExpired) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) + slideInVertically(
                        animationSpec = tween(600, delayMillis = 100),
                        initialOffsetY = { -it / 3 }
                    )
                ) {
                    Text(
                        text = if (isExpired) "Note Expired" else "Reminder Active",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isExpired) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Note title
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) + slideInVertically(
                        animationSpec = tween(800, delayMillis = 200),
                        initialOffsetY = { -it / 4 }
                    )
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 300)) + slideInVertically(
                        animationSpec = tween(1000, delayMillis = 300),
                        initialOffsetY = { -it / 5 }
                    )
                ) {
                    if (note.reminderTime != null) {
                        val totalSeconds = remainingTime / 1000
                        val days = totalSeconds / (60 * 60 * 24)
                        val hours = (totalSeconds / 3600) % 24
                        val minutes = (totalSeconds / 60) % 60
                        val seconds = totalSeconds % 60

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Reminder time
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = androidx.compose.foundation.BorderStroke(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "Remind at",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = SimpleDateFormat("EEEE, MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                                            .format(Date(note.reminderTime)),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Time remaining or expired message
                            if (isExpired) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFE57373).copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        Color(0xFFE57373).copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFFD32F2F)
                                        )
                                        Text(
                                            text = "This note has expired! Please take action.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF81C784).copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        Color(0xFF81C784).copy(alpha = 0.2f)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Timer,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = Color(0xFF388E3C)
                                            )
                                            Text(
                                                text = "Time remaining",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = Color(0xFF388E3C)
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Text(
                                            text = buildString {
                                                if (days > 0) append("$days d ")
                                                if (hours > 0 || days > 0) append("$hours h ")
                                                if (minutes > 0 || hours > 0 || days > 0) append("$minutes m ")
                                                append("$seconds s")
                                            },
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = Color(0xFF388E3C)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No reminder set for this note.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Close button
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(1200, delayMillis = 400)) + slideInVertically(
                        animationSpec = tween(1200, delayMillis = 400),
                        initialOffsetY = { it / 3 }
                    )
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isExpired) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isExpired) Color(0xFFD32F2F).copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Close",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
} 