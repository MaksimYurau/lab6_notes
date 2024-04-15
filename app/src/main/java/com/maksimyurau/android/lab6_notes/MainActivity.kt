package com.maksimyurau.android.lab6_notes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.rememberCoroutineScope
import com.maksimyurau.android.lab6_notes.ui.components.AppDrawer
import com.maksimyurau.android.lab6_notes.ui.components.Note
import com.maksimyurau.android.lab6_notes.routing.Screen
import com.maksimyurau.android.lab6_notes.theme.NotesTheme
import com.maksimyurau.android.lab6_notes.viewmodel.MainViewModel
import com.maksimyurau.android.lab6_notes.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

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
                val coroutineScope = rememberCoroutineScope()
                val scaffoldState: ScaffoldState = rememberScaffoldState()

                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerContent = {
                        AppDrawer(
                            currentScreen = Screen.Notes,
                            closeDrawerAction = {
                                coroutineScope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            }
                        )
                    },
                    content = {
                        Note()
                    }
                )
            }
        }
    }
}