package com.experience.osconverter

import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ExperienceApp() }
    }
}

private enum class Page(val label: String) {
    HOME("Home"), CONVERT("OS Converter"), DEFAULTS("Default apps"), TOOLS("Tools"), SETUP("Setup")
}

@Composable
private fun ExperienceApp() {
    val context = LocalContext.current
    var page by remember { mutableStateOf(Page.HOME) }
    var device by remember { mutableStateOf("Huawei Y9 2019 · JKM-LX2") }
    var status by remember { mutableStateOf("Choose an action to get started.") }
    val homeRoleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { status = "Check Android's Home app selection. Android controls which app becomes the default." }

    fun openIntent(intent: Intent, fallback: Intent? = null, message: String) {
        try {
            context.startActivity(intent)
            status = message
        } catch (_: ActivityNotFoundException) {
            if (fallback != null) {
                try {
                    context.startActivity(fallback)
                    status = message
                } catch (_: Exception) {
                    status = "This screen is not available on this device."
                }
            } else {
                status = "This action is not supported by the current device."
            }
        } catch (_: Exception) {
            status = "Android blocked this action or the required screen is unavailable."
        }
    }

    fun requestHomeRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val manager = context.getSystemService(RoleManager::class.java)
            if (manager != null && manager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                homeRoleLauncher.launch(manager.createRequestRoleIntent(RoleManager.ROLE_HOME))
            } else {
                openIntent(Intent(Settings.ACTION_HOME_SETTINGS), message = "Opened Home app settings.")
            }
        } else {
            openIntent(Intent(Settings.ACTION_HOME_SETTINGS), Intent(Settings.ACTION_SETTINGS),
                "Choose Experience OS Converter in Android's Home app settings.")
        }
    }

    fun openDefaultApps() {
        openIntent(
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS),
            "Android's default-app settings opened. Choose only roles this app is eligible for."
        )
    }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    Page.values().forEach { item ->
                        NavigationBarItem(
                            selected = page == item,
                            onClick = { page = item },
                            icon = { Text(item.label.take(1)) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("Experience UI", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Text("One app · one guided setup · your choice at every system prompt",
                        style = MaterialTheme.typography.bodyMedium)
                    if (status.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(status, color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(8.dp))
                }
                when (page) {
                    Page.HOME -> {
                        item {
                            HeaderCard("Welcome to Experience UI",
                                "A unified control centre for default-app prompts, device checks, safe tools and OS-conversion planning.")
                        }
                        item { HeaderCard("Detected Android", "Android ${Build.VERSION.RELEASE} · API ${Build.VERSION.SDK_INT}\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}") }
                        item {
                            Button(onClick = { requestHomeRole() }, modifier = Modifier.fillMaxWidth()) {
                                Text("Prompt to set Experience UI as Home")
                            }
                        }
                        item {
                            OutlinedButton(onClick = { page = Page.CONVERT }, modifier = Modifier.fillMaxWidth()) {
                                Text("Open OS Converter")
                            }
                        }
                    }
                    Page.CONVERT -> {
                        item { HeaderCard("OS Converter", "Select the exact device to see a cautious conversion checklist. This app does not flash firmware or rewrite Android system partitions.") }
                        item { Text("Target device", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ChoiceButton("Huawei Y9 2019 · JKM-LX2", device == "Huawei Y9 2019 · JKM-LX2") { device = "Huawei Y9 2019 · JKM-LX2" }
                                ChoiceButton("Samsung Galaxy Tab S · SM-T805 / chagalllte", device == "Samsung Galaxy Tab S · SM-T805 / chagalllte") { device = "Samsung Galaxy Tab S · SM-T805 / chagalllte" }
                            }
                        }
                        item { HeaderCard("Selected: $device", conversionGuidance(device)) }
                        item { HeaderCard("Before any ROM install", "1. Confirm the exact model and regional variant.\n2. Back up photos and important files.\n3. Verify the ROM source, checksum and signing.\n4. Read the device-specific bootloader/recovery instructions.\n5. Stop if the package or device codename does not match.") }
                    }
                    Page.DEFAULTS -> {
                        item { HeaderCard("Replace defaults by prompting", "Android must show and approve default-app changes. Experience UI can request the Home role; phone and SMS defaults require complete eligible dialer/SMS implementations.") }
                        item {
                            Button(onClick = { requestHomeRole() }, modifier = Modifier.fillMaxWidth()) {
                                Text("1 · Set Home / Launcher")
                            }
                        }
                        item {
                            OutlinedButton(onClick = { openDefaultApps() }, modifier = Modifier.fillMaxWidth()) {
                                Text("2 · Open Android default-app settings")
                            }
                        }
                        item { HeaderCard("Phone and SMS status", "The current unified build opens the system dialler and SMS composer for you. It does not yet implement the privileged in-call interface or complete default-SMS role, so Android may not offer Experience UI as the Phone or SMS default. No role is silently changed.") }
                    }
                    Page.TOOLS -> {
                        item { HeaderCard("Everyday tools", "These tools hand off to Android's existing apps so calls, messages and protected settings stay under system control.") }
                        item {
                            Button(onClick = {
                                openIntent(Intent(Intent.ACTION_DIAL), message = "Opened the system dialler.")
                            }, modifier = Modifier.fillMaxWidth()) { Text("Open Phone / Dialler") }
                        }
                        item {
                            Button(onClick = {
                                openIntent(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")), message = "Opened the system SMS composer.")
                            }, modifier = Modifier.fillMaxWidth()) { Text("Compose SMS") }
                        }
                        item {
                            OutlinedButton(onClick = {
                                openIntent(Intent(Settings.ACTION_SETTINGS), message = "Opened Android Settings.")
                            }, modifier = Modifier.fillMaxWidth()) { Text("Android Settings") }
                        }
                        item {
                            OutlinedButton(onClick = {
                                openIntent(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${context.packageName}")), message = "Opened Experience UI app settings.")
                            }, modifier = Modifier.fillMaxWidth()) { Text("Experience UI app info") }
                        }
                    }
                    Page.SETUP -> {
                        item { HeaderCard("Guided setup", "Choose what you want to configure. Android will ask you to confirm protected or default-app changes.") }
                        item { HeaderCard("1 · Home app", "Use the Home prompt to select Experience UI. You can return to your previous launcher from Android's Home app settings.") }
                        item { HeaderCard("2 · Network and security", "Open Android Settings to configure Wi-Fi, screen lock, biometrics and accounts. This app never reads your PIN or passwords.") }
                        item { HeaderCard("3 · Pico AI", "Pico AI integration is not hard-coded until its installed package and supported launch action are verified.") }
                        item {
                            Button(onClick = { openIntent(Intent(Settings.ACTION_SETTINGS), message = "Opened Android Settings.") },
                                modifier = Modifier.fillMaxWidth()) { Text("Open system setup settings") }
                        }
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun HeaderCard(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ChoiceButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(label) }
    }
}

private fun conversionGuidance(device: String): String = when {
    device.startsWith("Huawei") ->
        "Target: JKM-LX2 · Android 9 / EMUI 9.1 baseline. Treat this as a device-specific ROM research target, not a one-tap conversion. Confirm bootloader/recovery feasibility and exact regional firmware before proceeding."
    else ->
        "Target: SM-T805 · chagalllte · planned Android 10 ROM base. Verify the LTE device tree, kernel, vendor blobs and recovery compatibility before any installation."
}
