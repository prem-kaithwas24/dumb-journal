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
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DumpJournalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        NoteBook(modifier = Modifier.weight(1f))
                        Footer()
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
fun NoteBook(modifier: Modifier = Modifier){

    var text by rememberSaveable() {
        mutableStateOf("")
    }

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F3F3)) // paper-like
            .padding(16.dp),
        textStyle = TextStyle(
            fontSize = 18.sp,
            lineHeight = 26.sp
        ),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (text.isEmpty()) {
                    Text(
                        text= "Let your thoughts flow freely... nobody is watching, and we're not keeping track.",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }

                innerTextField()
            }

        }
    )
}

@Composable
fun Footer(): Unit {
    Text(
        text="Dump Journal",
        color = Color.Gray,
        fontSize = 12.sp,
        modifier = Modifier.padding(16.dp)
    )
}