package com.experience.launcher

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceCard
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var query by rememberSaveable { mutableStateOf("") }
                val appNames = listOf("Phone", "Messages", "Settings", "Camera", "Gallery", "Pico AI", "Files", "Clock")
                ExperienceScaffold("Experience UI", "A calmer home for everything") {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search apps") },
                        singleLine = true
                    )
                    Text("Apps", style = MaterialTheme.typography.titleMedium)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(appNames.filter { it.contains(query, ignoreCase = true) }) { app ->
                            Card(shape = MaterialTheme.shapes.large) {
                                Column(
                                    Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(app.take(1), style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.height(6.dp))
                                    Text(app, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    Text("Launcher foundation: live app discovery, widgets and gesture navigation are later integration milestones.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
