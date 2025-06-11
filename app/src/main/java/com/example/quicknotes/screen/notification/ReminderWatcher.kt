package com.example.quicknotes.screen.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.quicknotes.R
import com.example.quicknotes.data.local.entity.Note
import kotlinx.coroutines.delay

@Composable
fun ReminderWatcher(notes: List<Note>, context: Context) {
    var notifiedNotes by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(notes) {
        while (true) {
            val currentTime = System.currentTimeMillis()

            notes.forEach { note ->
                val reminderTime = note.reminderTime ?: return@forEach
                if (note.id == 0) return@forEach  // Bỏ qua nếu note chưa có id thực

                val remaining = reminderTime - currentTime
                val reminderMinutes = when (note.colorTag) {
                    "red" -> 30 // High priority: 30 minutes
                    "orange" -> 10 // Medium priority: 10 minutes
                    "green" -> 5 // Low priority: 5 minutes
                    else -> 30 // Default: 30 minutes
                }
                
                val isTimeToRemind = remaining in ((reminderMinutes - 1) * 60 * 1000)..(reminderMinutes * 60 * 1000)
                val alreadyNotified = notifiedNotes.contains(note.id)

                if (isTimeToRemind && !alreadyNotified) {
                    showReminderNotification(context, note, reminderMinutes)
                    notifiedNotes = notifiedNotes + note.id
                }
            }
            delay(60 * 1000)
        }
    }
}

private fun showReminderNotification(context: Context, note: Note, reminderMinutes: Int) {
    val channelId = "reminder_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Hey!!!"
        val descriptionText = "Remember left time is ${reminderMinutes}m!!! Try not to give up !"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Hey!!")
        .setContentText("Note: ${note.title} is due in $reminderMinutes minutes! Hurry up!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(note.id.toInt(), builder.build())
    }
}