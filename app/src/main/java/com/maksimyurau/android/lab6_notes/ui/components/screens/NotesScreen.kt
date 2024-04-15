package com.maksimyurau.android.lab6_notes.ui.components.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.maksimyurau.android.lab6_notes.model.NoteModel
import com.maksimyurau.android.lab6_notes.ui.components.Note
import com.maksimyurau.android.lab6_notes.ui.components.TopAppBar
import com.maksimyurau.android.lab6_notes.viewmodel.MainViewModel

@Composable
fun NotesScreen(viewModel: MainViewModel) {

    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())

    Column {
        TopAppBar(
            title = "Заметки",
            icon = Icons.Filled.List,
            onIconClick = {}
        )
        LazyColumn {
                items(count = notes.size) { noteIndex -> val note = notes[noteIndex]
                    Note(
                        note = note,
                        onNoteClick = {viewModel.onNoteClick(it)},
                        onNoteCheckedChange = { viewModel.onNoteCheckedChange(it)}
                    )
                }
            }
        }
    }

@Composable
private fun NotesList(
    notes: List<NoteModel>,
    onNoteCheckedChange: (NoteModel) -> Unit,
    onNoteClick: (NoteModel) -> Unit
) {
    LazyColumn {
        items(count = notes.size) {noteIndex ->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange = onNoteCheckedChange
            )
        }
    }
}
