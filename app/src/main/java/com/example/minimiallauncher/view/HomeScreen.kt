package com.example.minimiallauncher.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.minimiallauncher.viewModel.AppListViewModel
import com.example.minimiallauncher.viewModel.NotesViewModel
import com.example.minimiallauncher.viewModel.PopUpLauncherViewModel
import com.example.minimiallauncher.viewModel.WeatherViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    popupLauncherViewModel: PopUpLauncherViewModel,
    notesViewModel: NotesViewModel,
    weatherViewModel: WeatherViewModel,
    appListViewModel: AppListViewModel
) {
    val popDrawerVisible by popupLauncherViewModel.popUpDrawerVisible.collectAsState()
    val stickyNoteVisible by notesViewModel.stickyNotesVisible.collectAsState()
    var offsetX by remember { mutableFloatStateOf(-300f) }
    val homeScreenWidthLimit = -300f
    val notesScreenWidthLimit = 0f

    Box(
          modifier = Modifier
              .fillMaxSize()
              .windowInsetsPadding(WindowInsets.statusBars)
              .pointerInput(Unit) {
                  detectHorizontalDragGestures(onDragStart = {offsetX=homeScreenWidthLimit}) { _, dragAmount ->
                      if(!popDrawerVisible) {
                          offsetX = (offsetX + dragAmount).coerceIn(
                              homeScreenWidthLimit,
                              notesScreenWidthLimit
                          )
                          if (offsetX == notesScreenWidthLimit) {
                              notesViewModel.toggleVisibility(true)
                          }
                      }
                     }
              }
      ) {
          AnimatedVisibility(
              visible = !popDrawerVisible&&!stickyNoteVisible,
              enter = fadeIn(),
              exit = fadeOut(),
              modifier = Modifier.align(Alignment.TopCenter)
          ) {
              HomeWidgets(weatherViewModel)
          }
        AnimatedVisibility(
            visible = !stickyNoteVisible && !popDrawerVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            OutlinedButton(
                onClick = { popupLauncherViewModel.togglePopUpDrawerVisibility(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(0.12f)
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground.copy())
            ) {
                Text(
                    "ALL APPS",
                )
            }
        }
        AnimatedVisibility(
            visible = !popDrawerVisible && stickyNoteVisible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            StickyNotepadScreen(
                notesViewModel,
                resetOffsetCustom= {offsetX=notesScreenWidthLimit},
                onDrag = { dx -> offsetX = (offsetX + dx).coerceIn(homeScreenWidthLimit, notesScreenWidthLimit) }
            )
            if(offsetX == homeScreenWidthLimit) {
                 notesViewModel.toggleVisibility(false)
            }
        }
        AnimatedVisibility(
            visible = popDrawerVisible,
            enter = fadeIn(),
            exit =  fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            PopupAppDrawer(
                popDrawerVisible,
                onDismiss = {popupLauncherViewModel.togglePopUpDrawerVisibility(false)},
                popupLauncherViewModel,
                appListViewModel
            )
        }
      }
}