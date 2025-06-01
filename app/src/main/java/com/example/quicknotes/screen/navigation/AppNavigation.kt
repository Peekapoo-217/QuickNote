package com.example.quicknotes.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quicknotes.data.local.entity.Note
import com.example.quicknotes.screen.NoteFormScreen
import com.example.quicknotes.screen.ui.CompletedNotesScreen
import com.example.quicknotes.screen.ui.NoteListScreen
import com.example.quicknotes.screen.ui.TranslateScreen
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
                onNoteClick = { /* TODO: Edit note */ },
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
                onBack = { navController.popBackStack() }
            )
        }

    }
}
