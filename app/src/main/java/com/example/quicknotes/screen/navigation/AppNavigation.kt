package com.example.quicknotes.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.NoteListScreen
import com.example.quicknotes.screen.ui.CompletedNoteScreen
import com.example.quicknotes.screen.ui.NoteDetailScreen
import com.example.quicknotes.screen.ui.NoteFormScreen
import com.example.quicknotes.screen.ui.RecordNoteScreen
import com.example.quicknotes.screen.ui.TranslateScreen
import com.example.quicknotes.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: NoteRepository
) {
    NavHost(
        navController = navController,
        startDestination = "note_list"
    ) {
        composable("note_list") {
            NoteListScreen(
                repository = repository,
                onAddNoteClick = {
                    navController.navigate("note_form")
                },
                onNoteClick = { note ->
                    navController.navigate("note_detail/${note.id}")
                },
                onCompletedNotesClick = {
                    navController.navigate("completed_notes")
                },
                onRecordNoteClick = {
                    navController.navigate("record_note")
                },
                onTranslateClick = {
                    navController.navigate("translate")
                }
            )
        }

        composable("note_form") {
            NoteFormScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("note_detail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull() ?: return@composable
            var note by remember { mutableStateOf<Note?>(null) }
            
            LaunchedEffect(noteId) {
                note = repository.getNoteById(noteId)
            }
            
            note?.let { currentNote ->
                NoteDetailScreen(
                    note = currentNote,
                    repository = repository,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.deleteNote(noteId)
                        }
                        navController.popBackStack()
                    },
                    onToggleCompleted = { isCompleted ->
                        CoroutineScope(Dispatchers.IO).launch {
                            if (isCompleted) {
                                repository.completeNote(noteId)
                            } else {
                                repository.uncompleteNote(noteId)
                            }
                        }
                    }
                )
            }
        }

        composable("completed_notes") {
            CompletedNoteScreen(
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("record_note") {
            RecordNoteScreen(
                repository = repository,
                navController = navController
            )
        }

        composable("translate") {
            TranslateScreen(
                repository = repository
            )
        }
    }
}