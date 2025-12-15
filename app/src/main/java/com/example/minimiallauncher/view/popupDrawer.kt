package com.example.minimiallauncher.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.minimiallauncher.viewModel.AppLauncherViewModel

@Composable
fun PopupAppDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    appLauncherViewModel: AppLauncherViewModel
) {
    val context = LocalContext.current
    val query by appLauncherViewModel.searchQuery.collectAsState()

    val apps by appLauncherViewModel.filteredApps.collectAsState()
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // 🔹 Scrim (click outside to dismiss)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            awaitPointerEvent()
                            onDismiss()
                        }

                    }
            )

            // 🔹 Drawer popup card
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { it / 2 } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxSize(0.4f),
                ) {

                    LazyColumn(
                        state = listState,
                        reverseLayout = true,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    ) {
                        items(apps) { app ->
                            AppItem(app)
                        }
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = appLauncherViewModel::updatedSearchQuery,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search // Change the 'Enter' key to a 'Search' icon
                        ),
                        shape = RoundedCornerShape(16.dp),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // 1. Get the first filtered app
                                val firstApp = apps.firstOrNull()

                                // 2. Launch the app if it exists
                                if (firstApp != null) {
                                    // You'll need the context to launch the intent
                                    // Get LocalContext.current at the top of the AppDrawer composable
                                    val intent = context.packageManager.getLaunchIntentForPackage(firstApp.packageName)
                                    if (intent != null) context.startActivity(intent)

                                    // Hide the keyboard after launching the app
                                    // You might also want to close the drawer
                                    keyboardController?.hide()
                                    appLauncherViewModel.updatedSearchQuery("")
                                    appLauncherViewModel.toggleDrawerVisibility(false)
                                }

                            }
                        ),
                        placeholder = { Text("Search apps") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}


