package com.example.minimiallauncher.view

import android.annotation.SuppressLint
import  androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minimiallauncher.model.AppModel
import com.example.minimiallauncher.viewModel.AppLauncherViewModel


@Composable
fun AppDrawer(viewModel: AppLauncherViewModel) {

    val apps by viewModel.filteredApps.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val isPullingDown = available.y > 20f
                val isAtTop = listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0

                val isScrollable = listState.layoutInfo.totalItemsCount >
                        listState.layoutInfo.visibleItemsInfo.size

                if (isPullingDown && isAtTop && isScrollable) {
                    keyboardController?.hide()
                    viewModel.toggleDrawerVisibility(false)
                    return Offset.Zero
                }

                return Offset.Zero
            }
        }
    }

    BackHandler(enabled = true) {
        viewModel.toggleDrawerVisibility(false)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updatedSearchQuery,
            placeholder = { Text("Search apps") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(nestedScrollConnection)
        ) {
            items(apps) { app ->
                appItem(app)
            }
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun appItem(app: AppModel) {
    val context = LocalContext.current

        Text(
            text = app.appName,
            fontSize = 16.sp,
            modifier = Modifier.clickable{
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if(intent!=null) context.startActivity(intent)
            }
                .padding(8.dp))

    }

