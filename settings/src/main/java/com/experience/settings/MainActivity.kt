package com.experience.settings

import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceCard
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                val context = this
                ExperienceScaffold("Settings", "Make your device feel like yours") {
                    ExperienceCard("Network & internet", "Wi-Fi and connectivity", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }
                    })
                    ExperienceCard("Display & appearance", "Brightness and display settings", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS)) }
                    })
                    ExperienceCard("Security & privacy", "Screen lock and device security", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS)) }
                    })
                    ExperienceCard("Apps", "Installed apps and permissions", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS)) }
                    })
                    ExperienceCard("Notifications", "App notification settings", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_SETTINGS)) }
                    })
                    ExperienceCard("About this device", "Android version and build information", onClick = {
                        runCatching { context.startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)) }
                    })
                    Text("Protected settings remain controlled by Android and may require confirmation or system privileges.",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
