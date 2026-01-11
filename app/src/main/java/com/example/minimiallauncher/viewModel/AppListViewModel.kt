package com.example.minimiallauncher.viewModel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minimiallauncher.service.fuzzySearchPriority
import com.example.minimiallauncher.model.SystemAppModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppListViewModel(application: Application) : AndroidViewModel(application) {
    private val _appList = MutableStateFlow<List<SystemAppModel>>(emptyList())
    val appList = _appList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _favAppData = arrayListOf(
        arrayListOf("Phone", "com.google.android.dialer"),
        arrayListOf("VLC", "org.videolan.vlc"),
        arrayListOf("Firefox", "org.mozilla.firefox"),
        arrayListOf("ChatGPT", "com.openai.chatgpt"),
        arrayListOf("WhatsApp", "com.whatsapp"),
    )
    private val _favApps = MutableStateFlow<List<SystemAppModel>>(emptyList())
    val favApps = _favApps.asStateFlow()

    init {
        fetchInstalledApps()
        fetchFavApps()
    }

    private fun fetchFavApps() {

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


            val temp = ArrayList<SystemAppModel>()
            for(app in apps) {
                for(appData in _favAppData) {
                    if(appData.contains(app.appName) && appData.contains(app.packageName)) {
                        temp.add(app)
                    }
                }
            }
            _favApps.value = temp

        }
    }


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

    fun updatedSearchQuery(query: String){
        _searchQuery.value =query
    }

    fun fetchInstalledApps() {
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
}