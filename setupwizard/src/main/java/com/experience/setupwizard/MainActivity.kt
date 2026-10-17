package com.experience.setupwizard

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                val context = this
                val prefs = remember { context.getSharedPreferences("experience_setup", Context.MODE_PRIVATE) }
                var language by rememberSaveable { mutableStateOf(prefs.getString("language", "English") ?: "English") }
                var appearance by rememberSaveable { mutableStateOf(prefs.getString("appearance", "System") ?: "System") }
                var step by rememberSaveable { mutableIntStateOf(0) }
                val steps = listOf("Welcome", "Language", "Connectivity", "Pico AI", "Security", "Appearance")
                ExperienceScaffold("Set up Experience UI", "Step ${step + 1} of ${steps.size}") {
                    LinearProgressIndicator(progress = { (step + 1f) / steps.size }, modifier = Modifier.fillMaxWidth())
                    Text(steps[step], style = MaterialTheme.typography.headlineSmall)
                    when (step) {
                        0 -> Text("A thoughtful, personal Android experience starts here.")
                        1 -> {
                            Text("Choose your language")
                            listOf("English", "বাংলা", "日本語").forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = language == item, onClick = { language = item })
                                    Text(item)
                                }
                            }
                        }
                        2 -> {
                            Text("Connect to Wi-Fi in Android's secure network settings.")
                            OutlinedButton(onClick = { runCatching { context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) } }) {
                                Text("Open Wi-Fi settings")
                            }
                        }
                        3 -> Text("Pico AI can be configured after setup. This wizard does not collect passwords or claim to install the separate app.")
                        4 -> {
                            Text("Set a PIN or biometrics through Android's security settings.")
                            OutlinedButton(onClick = { runCatching { context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS)) } }) {
                                Text("Open security settings")
                            }
                        }
                        else -> {
                            Text("Choose appearance")
                            listOf("System", "Light", "Dark").forEach { item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = appearance == item, onClick = { appearance = item })
                                    Text(item)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { if (step > 0) step-- }, enabled = step > 0, modifier = Modifier.weight(1f)) {
                            Text("Back")
                        }
                        Button(onClick = {
                            if (step < steps.lastIndex) step++ else {
                                prefs.edit().putString("language", language).putString("appearance", appearance)
                                    .putBoolean("completed", true).apply()
                            }
                        }, modifier = Modifier.weight(1f)) {
                            Text(if (step == steps.lastIndex) "Finish" else "Continue")
                        }
                    }
                    if (prefs.getBoolean("completed", false)) Text("Setup preferences saved on this device.")
                }
            }
        }
    }
}
