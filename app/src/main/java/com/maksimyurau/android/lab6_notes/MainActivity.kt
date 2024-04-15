package com.maksimyurau.android.lab6_notes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.maksimyurau.android.lab6_notes.theme.NotesTheme
import com.maksimyurau.android.lab6_notes.ui.components.screens.NotesScreen
import com.maksimyurau.android.lab6_notes.viewmodel.MainViewModel
import com.maksimyurau.android.lab6_notes.viewmodel.MainViewModelFactory

/**
 * Main activity приложения.
 */
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModelFactory(
            this,
            (application as NotesApplication).dependencyInjector.repository
        )
    })

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotesTheme {
                NotesScreen(viewModel = viewModel)
            }
        }
    }
}