package com.example.minimiallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minimiallauncher.data.notesdata.NotesViewModelFactory
import com.example.minimiallauncher.ui.theme.AppLauncherTheme
import com.example.minimiallauncher.view.HomeScreen
import com.example.minimiallauncher.viewModel.AppListViewModel
import com.example.minimiallauncher.viewModel.PopUpLauncherViewModel
import com.example.minimiallauncher.viewModel.NotesViewModel
import com.example.minimiallauncher.viewModel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppLauncherTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val popUpLauncherViewModel: PopUpLauncherViewModel =
                        viewModel(factory = ViewModelProvider.AndroidViewModelFactory(application))

                    val notesViewModel: NotesViewModel = viewModel(
                        factory = NotesViewModelFactory(application)
                    )
                    val weatherViewModel: WeatherViewModel = viewModel()

                    val appListViewModel: AppListViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(application))

                    HomeScreen(popUpLauncherViewModel, notesViewModel,weatherViewModel, appListViewModel)
                }
            }
        }
    }
}



