package com.example.minimiallauncher.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minimiallauncher.model.AppModel
import com.example.minimiallauncher.model.NotepadRepository
import com.example.minimiallauncher.model.RetrofitInstance
import com.example.minimiallauncher.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.util.Locale

class AppLauncherViewModel(application: Application) : AndroidViewModel(application) {

    private  val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes  : StateFlow<List<String>> = _notes



    private val _appList = MutableStateFlow<List<AppModel>>(emptyList())
    val appList = _appList.asStateFlow()

    private val _drawerVisible = MutableStateFlow(false)
    val drawerVisible = _drawerVisible.asStateFlow()

    private val _popUpDrawerVisible = MutableStateFlow(false)
    val popUpDrawerVisible = _popUpDrawerVisible.asStateFlow()


    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _showSystemUI = MutableSharedFlow<Unit>()
    val showSyfstemUI = _showSystemUI.asSharedFlow()



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
                AppModel(
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

    fun TogglePopUpDrawerVisibility(show: Boolean) {
        _popUpDrawerVisible.value = show
    }




}

class NotesViewModel(private val repository: NotepadRepository): ViewModel() {
    private val _stickyNotes = MutableStateFlow(false)
    val stickyNotes : StateFlow<Boolean> = _stickyNotes

    fun tooglevisibility(show: Boolean) {
        _stickyNotes.value = show
    }
    var noteText by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            val note = repository.getNote()
            noteText = note?.content ?: ""
        }
    }

    fun updateNote(newText: String) {
        noteText = newText
        viewModelScope.launch {
            repository.saveNote(newText)
        }
    }
}


class WeatherViewModel : ViewModel() {
    var weatherData = mutableStateOf<WeatherResponse?>(null)
        private set

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(
                    lat, lon, "6f758ee93617b33db361f5fd8f221140"
                )
                weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}




