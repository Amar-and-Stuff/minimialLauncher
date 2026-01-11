package com.example.minimiallauncher.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minimiallauncher.model.SystemAppModel
import com.example.minimiallauncher.viewModel.AppListViewModel


@Composable
fun FavoriteAppsList(
    appListViewModel: AppListViewModel,
    onAppLaunched: () -> Unit,
) {

    val apps by appListViewModel.favApps.collectAsState()
    val listState = rememberLazyListState()

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxSize(0.4f),
    ) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 20.dp)
        ) {
            items(apps) { app ->
                AppItem(app, onAppLaunched)
            }
        }
    }
}

@Composable
fun FavoriteAppsList(
    appListViewModel: AppListViewModel,
    // appList: List<SystemAppModel>,
) {
    FavoriteAppsList(appListViewModel, {})
}

@Composable
fun AppItem(app: SystemAppModel, onAppLaunched: () -> Unit) {
    val context = LocalContext.current
    Text(
        text = app.appName,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        modifier = Modifier
            .clickable {
                val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    context.startActivity(intent)
                    onAppLaunched()
                }
            }
            .fillMaxWidth()
            .padding(8.dp)
    )
}
