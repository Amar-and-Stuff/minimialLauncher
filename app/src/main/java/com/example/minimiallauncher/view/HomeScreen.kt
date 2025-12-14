package com.example.minimiallauncher.view

import android.R
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.minimiallauncher.viewModel.AppLauncherViewModel
import com.example.minimiallauncher.viewModel.NotesViewModel
import com.example.minimiallauncher.viewModel.WeatherViewModel
import com.example.minimiallauncher.viewModel.time
import com.example.minimiallauncher.view.PopupAppDrawer

@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(viewModel: AppLauncherViewModel, notesViewModel: NotesViewModel,weatherViewModel: WeatherViewModel) {
    val popDrawerVisible by viewModel.popUpDrawerVisible.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val showStickyNote by notesViewModel.stickyNotes.collectAsState()




    Box(
          modifier = Modifier
              .fillMaxSize()
              .windowInsetsPadding(WindowInsets.statusBars)
              .pointerInput(Unit) {
                  detectHorizontalDragGestures { _, dragAmount ->
                      when {
                          dragAmount > 50 ->  notesViewModel.tooglevisibility(true)    // Swipe right to open
                          dragAmount < -50 ->  notesViewModel.tooglevisibility(false)    // Swipe left to close
                      }
                  }
              }

      ) {

          AnimatedVisibility(
              visible = !popDrawerVisible&&!showStickyNote,
              enter = fadeIn(),
              exit = fadeOut(),
              modifier = Modifier.align(Alignment.TopCenter)
          ) {
              HomeWidgets(weatherViewModel)
          }
        AnimatedVisibility(
            visible = !showStickyNote,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Button(onClick = { viewModel.TogglePopUpDrawerVisibility(true) }) {
                Text("all apps", color = Color.White)
            }

        }
        AnimatedVisibility(
            visible = !popDrawerVisible && showStickyNote,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            StickyNotepadScreen(notesViewModel, onSwipeLeftToClose = {notesViewModel.tooglevisibility(false) })
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
                popDrawerVisible, onDismiss = { viewModel.TogglePopUpDrawerVisibility(false)}, viewModel)
        }

      }
  }






