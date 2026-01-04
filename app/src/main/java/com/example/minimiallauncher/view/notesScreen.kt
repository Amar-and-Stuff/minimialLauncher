package com.example.minimiallauncher.view
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minimiallauncher.viewModel.NotesViewModel

@Composable
fun StickyNotepadScreen(viewModel: NotesViewModel, resetOffsetCustom: () -> Unit, onDrag: (Float) -> Unit) {
    val text = viewModel.noteText

    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onDragStart = {resetOffsetCustom()}) { _, dragAmount ->
                    onDrag(dragAmount)
                }
            }
            .padding(16.dp)
    ) {
        Text(
            text = "Note",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = text,
            onValueChange = { viewModel.updateNote(it) },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = textColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            textStyle = TextStyle(
                fontSize = 22.sp,
                lineHeight = 30.sp
            ),
            placeholder = {
                Text(
                    text="Type anything...",
                    color = textColor.copy(alpha = 0.5f)
                )
            },
            maxLines = Int.MAX_VALUE
        )
    }
}




