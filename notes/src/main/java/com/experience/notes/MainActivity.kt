package com.experience.notes

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
import android.content.Context
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                
                
                
                ExperienceScaffold("Notes", "Your thoughts, saved on this device") {
                    val context = LocalContext.current
                val prefs = remember { context.getSharedPreferences("experience_notes", 0) }
                var note by rememberSaveable { mutableStateOf(prefs.getString("note", "") ?: "") }
                OutlinedTextField(value = note, onValueChange = { note = it; prefs.edit().putString("note", it).apply() }, modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp), label = { Text("Write a note") })
                Text("Autosaved locally on this device.")
                }
            }
        }
    }
}
