package com.experience.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                SettingsHome(
                    open = { action -> openSettings(action) },
                    device = Build.MODEL ?: "Unknown device",
                    manufacturer = Build.MANUFACTURER ?: "Unknown",
                    android = Build.VERSION.RELEASE ?: "Unknown",
                    sdk = Build.VERSION.SDK_INT.toString(),
                    build = Build.DISPLAY?.takeIf { it.isNotBlank() } ?: Build.FINGERPRINT,
                    security = if (Build.VERSION.SDK_INT >= 23) Build.VERSION.SECURITY_PATCH else "Not available",
                    fingerprint = Build.FINGERPRINT ?: "Not available"
                )
            }
        }
    }

    private fun openSettings(action: String) {
        val intent = Intent(action)
        try {
            startActivity(intent)
        } catch (_: Exception) {
            try { startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (_: Exception) { }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsHome(
    open: (String) -> Unit,
    device: String,
    manufacturer: String,
    android: String,
    sdk: String,
    build: String,
    security: String,
    fingerprint: String
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Experience Settings") })
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Network & connectivity", style = MaterialTheme.typography.titleLarge)
            SettingItem("Wi-Fi", "Choose a network and manage saved networks") { open(Settings.ACTION_WIFI_SETTINGS) }
            SettingItem("Bluetooth", "Pair devices and manage connections") { open(Settings.ACTION_BLUETOOTH_SETTINGS) }
            SettingItem("Network & internet", "SIM, mobile network, hotspot and VPN where supported") { open(Settings.ACTION_WIRELESS_SETTINGS) }

            Text("Device", style = MaterialTheme.typography.titleLarge)
            SettingItem("Display", "Brightness, screen timeout and display options") { open(Settings.ACTION_DISPLAY_SETTINGS) }
            SettingItem("Sound", "Ringtones, volume and vibration") { open(Settings.ACTION_SOUND_SETTINGS) }
            SettingItem("Notifications", "App notifications and notification access") { open(Settings.ACTION_SETTINGS) }
            SettingItem("Apps", "Installed apps, defaults and permissions") { open(Settings.ACTION_APPLICATION_SETTINGS) }
            SettingItem("Security & privacy", "Screen lock and supported security controls") { open(Settings.ACTION_SECURITY_SETTINGS) }
            SettingItem("Accounts", "Manage accounts configured on Android") { open(Settings.ACTION_SYNC_SETTINGS) }
            SettingItem("Language & input", "System language, keyboard and input methods") { open(Settings.ACTION_LOCALE_SETTINGS) }

            Text("About this device", style = MaterialTheme.typography.titleLarge)
            InfoItem("Device", "$manufacturer $device")
            InfoItem("Android version", android)
            InfoItem("Android API level", sdk)
            InfoItem("Build number", build)
            InfoItem("Security patch", security)
            InfoItem("Build fingerprint", fingerprint)
            Text(
                "Some controls are provided by Android's system Settings. Directly changing protected system settings requires the relevant ROM privileges; this app does not report changes it cannot perform.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SettingItem(title: String, summary: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(summary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun InfoItem(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
