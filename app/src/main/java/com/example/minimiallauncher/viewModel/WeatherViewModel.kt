package com.example.minimiallauncher.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minimiallauncher.data.weatherdata.RetrofitInstance
import com.example.minimiallauncher.model.WeatherResponse
import kotlinx.coroutines.launch

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

    fun getWeatherString() : String {
        val data by weatherData

        return if (data != null) {
            "Temp: ${data!!.main.temp}°C, ${data!!.weather[0].description}"
        } else {
            "Fetching weather..."
        }
    }
}