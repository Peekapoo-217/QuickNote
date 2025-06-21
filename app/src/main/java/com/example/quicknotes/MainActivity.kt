package com.example.quicknotes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quicknotes.ui.theme.QuickNotesTheme
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.data.local.database.AppDatabase
import com.example.quicknotes.repository.NoteRepository
import com.example.quicknotes.screen.navigation.AppNavigation
import com.example.quicknotes.screen.notification.ReminderWatcher
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermission()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = NoteRepository(
            noteDao = database.noteDao(),
            completedNoteDao = database.completedNoteDao(),
            noteImageDao = database.noteImageDao(),
            context = applicationContext
        )

        setContent {
            QuickNotesTheme {
                val navController = rememberNavController()

                // Gọi watcher để theo dõi thời gian còn lại
                ReminderWatcher(repository = repository, context = this)

                AppNavigation(
                    navController = navController,
                    repository = repository
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickNotesTheme {
        Greeting("Android")
    }
}