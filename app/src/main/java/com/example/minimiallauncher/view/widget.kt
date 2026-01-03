package com.example.minimiallauncher.view

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.minimiallauncher.viewModel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun HomeWidgets(
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val fused = LocationServices.getFusedLocationProviderClient(context)
                fused.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        weatherViewModel.fetchWeather(loc.latitude, loc.longitude)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ClockWidget()
        WeatherWidget(weatherViewModel)
        CalendarWidget()
 //      DigitalWidget(screenTime)
    }
}
@Composable
fun DigitalWidget(screenTime: String) {
    Text(
        text = "Screen Time: $screenTime",
        style = MaterialTheme.typography.bodyLarge
    )
}
@Composable
fun WeatherWidget(viewModel: WeatherViewModel) {
    val data by viewModel.weatherData

    if (data != null) {
        Text(text = "Temp: ${data!!.main.temp}°C, ${data!!.weather[0].description}")
    } else {
        Text(text = "Fetching weather...")
    }
}


@Composable
@Preview
fun ClockWidget() {
    var currentTime by remember { mutableStateOf(getFormattedTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            delay(1000L)
        }
    }

    Text(
        text = currentTime,
        style = MaterialTheme.typography.headlineLarge
    )
}

fun getFormattedTime(): String {
    val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return formatter.format(Date())
}

@Composable
@Preview
fun CalendarWidget() {
    val today = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
    Text(
        text = "Today: $today",
        style = MaterialTheme.typography.bodyLarge
    )
}


