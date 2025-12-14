package com.example.minimiallauncher.viewModel

import android.annotation.SuppressLint
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.minimiallauncher.R
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@SuppressLint("SuspiciousIndentation")
@Composable
@Preview
fun time() {
 val font = FontFamily(Font(R.font.myfont))
    var Time = remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            Time.value = LocalTime.now()
        }
    }
    var  formatedTime = Time.value.format(DateTimeFormatter.ofPattern("hh:mm a"))
    Text(text=formatedTime,
        fontSize = 32.sp,
        fontFamily =font )


}