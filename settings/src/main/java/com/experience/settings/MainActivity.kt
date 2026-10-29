package com.experience.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    val tvLayout = LocalConfiguration.current.screenWidthDp >= 840
    Scaffold(topBar = {
        TopAppBar(title = { Text("Experience Settings", fontSize = if (tvLayout) 30.sp else 22.sp) })
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(if (tvLayout) 36.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(if (tvLayout) 18.dp else 12.dp)
        ) {
            Text("Network & connectivity", style = if (tvLayout) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge)
            SettingItem("Wi-Fi", "Connect to a wireless network") { open(Settings.ACTION_WIFI_SETTINGS) }
            SettingItem("Bluetooth & remotes", "Pair a remote, controller, keyboard or audio device") { open(Settings.ACTION_BLUETOOTH_SETTINGS) }
            SettingItem("Network & internet", "Manage network connections and supported VPN options") { open(Settings.ACTION_WIRELESS_SETTINGS) }

            Text("TV & device", style = if (tvLayout) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge)
            SettingItem("Display & HDMI", "Display resolution, screen size and supported display options") { open(Settings.ACTION_DISPLAY_SETTINGS) }
            SettingItem("Sound & audio output", "Volume, audio routing and supported sound options") { open(Settings.ACTION_SOUND_SETTINGS) }
            SettingItem("Apps & permissions", "Installed apps, default apps and app permissions") { open(Settings.ACTION_APPLICATION_SETTINGS) }
            SettingItem("Security & privacy", "Screen lock and supported security controls") { open(Settings.ACTION_SECURITY_SETTINGS) }
            SettingItem("Accessibility", "Captions, text scaling and supported accessibility features") { open(Settings.ACTION_ACCESSIBILITY_SETTINGS) }
            SettingItem("System settings", "Open the full Android settings application") { open(Settings.ACTION_SETTINGS) }

            Text("About this device", style = if (tvLayout) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge)
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
    var focused by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val tvLayout = LocalConfiguration.current.screenWidthDp >= 840
    Card(
        modifier = Modifier.fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .border(
                BorderStroke(
                    if (focused) 3.dp else 1.dp,
                    if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .focusable(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (focused) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            Modifier.padding(if (tvLayout) 24.dp else 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = if (tvLayout) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium)
            Text(summary, style = if (tvLayout) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium)
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
