package com.example.minimiallauncher.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minimiallauncher.Service.fuzzySearchPriority
import com.example.minimiallauncher.model.SystemAppModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PopUpLauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val _appList = MutableStateFlow<List<SystemAppModel>>(emptyList())
    val appList = _appList.asStateFlow()

    private val _drawerVisible = MutableStateFlow(false)
    val drawerVisible = _drawerVisible.asStateFlow()

    private val _popUpDrawerVisible = MutableStateFlow(false)
    val popUpDrawerVisible = _popUpDrawerVisible.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showSystemUI = MutableSharedFlow<Unit>()
    val showSystemUI = _showSystemUI.asSharedFlow()



    val filteredApps = combine(_appList, _searchQuery) { apps, query ->
        val trimmedQuery = query.trim().lowercase()

        if (trimmedQuery.isBlank()) {
            apps
        } else if (trimmedQuery.length == 1) {
            // Show only apps starting with that one letter
            apps.filter { it.appName.lowercase().startsWith(trimmedQuery) }
        } else {
            // Show apps based on fuzzy priority
            apps.mapNotNull { app ->
                val score = fuzzySearchPriority(app.appName, trimmedQuery)
                if (score > 0) Pair(score, app) else null
            }
                .sortedByDescending { it.first } // Higher priority first
                .map { it.second }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )



    init {
        loadInstalledApps()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun loadInstalledApps() {
        viewModelScope.launch {
            val pm = getApplication<Application>().packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolvedApps = pm.queryIntentActivities(intent, 0)

            val apps = resolvedApps.map {
                val appInfo = it.activityInfo.applicationInfo
                SystemAppModel(
                    appName = it.loadLabel(pm).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(pm)
                )
            }.sortedBy { it.appName.lowercase() }

            _appList.value = apps
        }
    }


    fun toggleDrawerVisibility(show: Boolean) {
        _drawerVisible.value = show
    }
    fun updatedSearchQuery(query: String){
        _searchQuery.value =query
    }
    fun requestShowSystemUI(){
        viewModelScope.launch {
            _showSystemUI.emit(Unit)
        }

    }

    fun togglePopUpDrawerVisibility(show: Boolean) {
        _popUpDrawerVisible.value = show
    }
}









