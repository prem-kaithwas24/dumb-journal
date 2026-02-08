package com.dumpjournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dumpjournal.ui.theme.DumpJournalTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }
            
            DumpJournalTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        NoteBook(
                            modifier = Modifier.weight(1f),
                            isDarkMode = isDarkMode
                        )
                        Footer(
                            isDarkMode = isDarkMode,
                            onToggleTheme = { isDarkMode = !isDarkMode }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader(modifier: Modifier = Modifier){
    Text(
        text = "Dump Journal",
        modifier = modifier
    )
}

@Composable
fun NoteBook(modifier: Modifier = Modifier, isDarkMode: Boolean = false){

    var text by rememberSaveable() {
        mutableStateOf("")
    }

    val backgroundColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF3F3F3)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black
    val placeholderColor = if (isDarkMode) Color(0xFF808080) else Color.Gray

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        textStyle = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            color = textColor
        ),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (text.isEmpty()) {
                    Text(
                        text= "Let your thoughts flow freely... nobody is watching, and we're not keeping track.",
                        color = placeholderColor,
                        fontSize = 18.sp
                    )
                }

                innerTextField()
            }

        }
    )
}

@Composable
fun Footer(isDarkMode: Boolean = false, onToggleTheme: () -> Unit = {}): Unit {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text="Dump Journal",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        IconButton(onClick = onToggleTheme) {

            Icon(
                painter = painterResource(
                    id = if (isDarkMode)
                        R.drawable.mode_night_24px
                    else
                        R.drawable.light_mode_24px
                ),
                contentDescription = "Toggle theme",
                tint = Color.Gray
            )
        }

    }
}