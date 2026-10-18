package com.experience.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var selected by remember { mutableStateOf("No media selected") }
                val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    selected = uri?.toString() ?: "No media selected"
                }
                ExperienceScaffold("Gallery", "Choose photos and videos") {
                    Button(onClick = { picker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) { Text("Choose a photo") }
                    OutlinedButton(onClick = { picker.launch("video/*") }, modifier = Modifier.fillMaxWidth()) { Text("Choose a video") }
                    Text(selected)
                    Text("This first milestone uses Android's secure media picker. A full local gallery grid and editor are planned.")
                }
            }
        }
    }
}
