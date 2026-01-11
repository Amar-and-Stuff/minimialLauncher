package com.example.minimiallauncher.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.minimiallauncher.viewModel.AppListViewModel
import com.example.minimiallauncher.viewModel.PopUpLauncherViewModel

@Composable
fun PopupAppDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    popUpLauncherViewModel: PopUpLauncherViewModel,
    appListViewModel: AppListViewModel
) {
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onDismiss()
                                appListViewModel.updatedSearchQuery("")
                                Log.d("Popup view", "Tap")
                            },
                        )
                    }
            )
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically { it / 2 } + fadeIn(),
                exit = slideOutVertically { it / 2 } + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
                    .fillMaxWidth(0.8f)
                    .fillMaxSize(0.4f),
            ) {
                FullAppsList(popUpLauncherViewModel, appListViewModel, focusRequester)
            }
        }
    }
}


@Composable
fun FullAppsList(
    popUpLauncherViewModel: PopUpLauncherViewModel,
    appListViewModel: AppListViewModel,
    focusRequester: FocusRequester
) {
    val context = LocalContext.current
    val query by appListViewModel.searchQuery.collectAsState()
    val apps by appListViewModel.filteredApps.collectAsState()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val cleanupAndDismiss: () -> Unit = {
        keyboardController?.hide()
        appListViewModel.updatedSearchQuery("")
        popUpLauncherViewModel.togglePopUpDrawerVisibility(false)
    }

    Card() {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(apps) { app ->
                AppItem(app, onAppLaunched = cleanupAndDismiss)
            }
        }
        OutlinedTextField(
            value = query,
            onValueChange = appListViewModel::updatedSearchQuery,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            shape = RoundedCornerShape(16.dp),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val firstApp = apps.firstOrNull()
                    if (firstApp != null) {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(
                                firstApp.packageName
                            )
                        if (intent != null) context.startActivity(intent)
                        cleanupAndDismiss()
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
