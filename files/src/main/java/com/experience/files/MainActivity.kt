package com.experience.files

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var picked by remember { mutableStateOf("No file selected") }
                
                
                ExperienceScaffold("Files", "Browse and open documents") {
                    val context = LocalContext.current
                val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> if (uri != null) picked = uri.toString() }
                Button(onClick = { picker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) { Text("Browse files") }
                Text(picked.ifBlank { "No file selected" })
                }
            }
        }
    }
}
