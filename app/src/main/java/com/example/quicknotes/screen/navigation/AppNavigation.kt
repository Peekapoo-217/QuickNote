package com.example.quicknotes.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.ui.NoteFormScreen
import com.example.quicknotes.screen.ui.CompletedNotesScreen
import com.example.quicknotes.screen.NoteListScreen
import com.example.quicknotes.screen.ui.RecordNoteScreen
import com.example.quicknotes.screen.ui.TranslateScreen
import com.example.quicknotes.screen.ui.NoteDetailScreen
import com.example.quicknotes.viewmodel.NoteViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    notes: List<Note>,
    viewModel: NoteViewModel
) {
    NavHost(navController = navController, startDestination = "note_list") {
        composable("note_list") {
            NoteListScreen(
                notes = notes,
                viewModel = viewModel,
                onAddNote = { navController.navigate("note_form") },
                onAddImageNote = { navController.navigate("translate_screen") },
                onRecordNote = { navController.navigate("record_note") },
                onNoteClick = { note -> 
                    navController.navigate("note_detail/${note.id}")
                },
                onViewCompletedNotes = { navController.navigate("completed_notes") }
            )
        }

        composable("note_form") {
            NoteFormScreen(
                onSave = { note ->
                    viewModel.insert(note)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable("translate_screen") {
            TranslateScreen(viewModel = viewModel)
        }

        composable("completed_notes") {
            CompletedNotesScreen(
                completedNotes = viewModel.completedNotes.collectAsState().value,
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable("record_note") {
            RecordNoteScreen(viewModel = viewModel, navController = navController)
        }

        composable("note_detail/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            val note = notes.find { it.id == noteId }
            note?.let {
                NoteDetailScreen(
                    note = it,
                    onBackClick = { navController.popBackStack() },
                    onDeleteClick = {
                        viewModel.delete(it)
                        navController.popBackStack()
                    },
                    onToggleCompleted = { isCompleted ->
                        viewModel.update(it.copy(isCompleted = isCompleted))
                    }
                )
            }
        }
    }
}
