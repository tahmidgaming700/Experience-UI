package com.experience.setupwizard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Midnight = Color(0xFF07163D)
private val ElectricBlue = Color(0xFF2D8BFF)
private val GlassPanel = Color(0x2FE0EEFF)

private data class SetupStep(val title: String, val subtitle: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFF9EC9FF),
                background = Midnight,
                surface = Color(0xFF122C5C),
                onBackground = Color.White,
                onSurface = Color.White
            )) {
                SetupWizard(
                    context = this,
                    onOpen = { intent -> runCatching { startActivity(intent) } }
                )
            }
        }
    }
}

@Composable
private fun SetupWizard(context: Context, onOpen: (Intent) -> Unit) {
    val prefs = remember { context.getSharedPreferences("experience_setup", Context.MODE_PRIVATE) }
    var step by rememberSaveable { mutableIntStateOf(0) }
    var language by rememberSaveable { mutableStateOf(prefs.getString("language", "English") ?: "English") }
    var region by rememberSaveable { mutableStateOf(prefs.getString("region", "Bangladesh") ?: "Bangladesh") }
    var migration by rememberSaveable { mutableStateOf(prefs.getString("migration", "Set up as new") ?: "Set up as new") }
    var accountChoice by rememberSaveable { mutableStateOf(prefs.getString("account", "Skip for now") ?: "Skip for now") }
    var googleChoice by rememberSaveable { mutableStateOf(prefs.getString("google", "Set up later") ?: "Set up later") }
    var appearance by rememberSaveable { mutableStateOf(prefs.getString("appearance", "Dark") ?: "Dark") }
    var finished by rememberSaveable { mutableStateOf(prefs.getBoolean("completed", false)) }

    val steps = listOf(
        SetupStep("Welcome to Experience UI", "A calmer, more personal Android experience."),
        SetupStep("Choose your language", "You can change this later in Settings."),
        SetupStep("Select your region", "Set regional defaults for your experience."),
        SetupStep("Connect to Wi-Fi", "A connection helps with updates, accounts and restoring data."),
        SetupStep("Bring your data", "Choose whether to migrate data or start fresh."),
        SetupStep("Experience Account", "Use an Experience account when account services are available, or continue without one."),
        SetupStep("Google services", "Add a Google account for Play Store, backup and Google services if you want them."),
        SetupStep("Make it yours", "Choose the look you want for Experience UI."),
        SetupStep("You're ready", "Your choices are saved locally. Finish Android's protected setup separately if needed.")
    )

    Box(Modifier.fillMaxSize().background(
        Brush.linearGradient(listOf(Midnight, Color(0xFF0B3678), Color(0xFF111E58), Color(0xFF27286B)))
    )) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(42.dp).background(
                    Brush.linearGradient(listOf(ElectricBlue, Color(0xFF9B8CFF))),
                    RoundedCornerShape(14.dp)
                ), contentAlignment = Alignment.Center) {
                    Text("E", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("EXPERIENCE UI", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Setup experience", fontSize = 12.sp, color = Color.White.copy(alpha = .72f))
                }
            }

            LinearProgressIndicator(
                progress = { (step + 1f) / steps.size },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = Color(0xFF9FC9FF),
                trackColor = Color.White.copy(alpha = .18f)
            )

            Column(
                Modifier.fillMaxWidth().background(GlassPanel, RoundedCornerShape(30.dp)).padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("STEP %02d".format(step + 1), color = Color(0xFF9FC9FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(steps[step].title, fontSize = 29.sp, lineHeight = 35.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(steps[step].subtitle, fontSize = 15.sp, color = Color.White.copy(alpha = .8f))

                when (step) {
                    0 -> {
                        Text("Designed around you.", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        Text("A fresh home screen, useful system apps and thoughtful details — built on Android and designed as one experience.", color = Color.White.copy(alpha = .8f))
                    }
                    1 -> ChoiceList(language, listOf("English", "বাংলা", "日本語", "العربية")) { language = it }
                    2 -> ChoiceList(region, listOf("Bangladesh", "India", "United Kingdom", "United States", "Japan", "Other")) { region = it }
                    3 -> {
                        Text("Wi-Fi passwords are entered only in Android's own network screen. This wizard never asks you to type one here.", color = Color.White.copy(alpha = .82f))
                        Button(onClick = { onOpen(Intent(Settings.ACTION_WIFI_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) { Text("Open Wi-Fi settings") }
                    }
                    4 -> ChoiceList(migration, listOf("Transfer from another Android", "Restore from backup", "Set up as new", "Decide later")) { migration = it }
                    5 -> {
                        ChoiceList(accountChoice, listOf("Sign in when available", "Create an account later", "Skip for now")) { accountChoice = it }
                        Text("Account sign-in is not implemented by this screen yet. No credentials are collected or stored.", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                    }
                    6 -> {
                        ChoiceList(googleChoice, listOf("Add Google account", "Set up later", "Skip Google services")) { googleChoice = it }
                        if (googleChoice == "Add Google account") {
                            Button(onClick = { onOpen(Intent(Settings.ACTION_ADD_ACCOUNT).putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Open Android account settings")
                            }
                        }
                        Text("Google apps and sign-in require a compatible GApps package and supported ROM build. This choice does not install them.", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                    }
                    7 -> ChoiceList(appearance, listOf("Dark", "Light", "System default")) { appearance = it }
                    else -> {
                        Text("Language: $language\nRegion: $region\nData: $migration\nExperience Account: $accountChoice\nGoogle: $googleChoice\nAppearance: $appearance",
                            color = Color.White.copy(alpha = .9f), lineHeight = 24.sp)
                        Text("This app saves your setup choices; system-level provisioning, migration and account flows need privileged ROM integration.", color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { if (step > 0) step-- },
                    enabled = step > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) { Text("Back") }
                Button(
                    onClick = {
                        if (step < steps.lastIndex) {
                            step++
                        } else {
                            prefs.edit()
                                .putString("language", language)
                                .putString("region", region)
                                .putString("migration", migration)
                                .putString("account", accountChoice)
                                .putString("google", googleChoice)
                                .putString("appearance", appearance)
                                .putBoolean("completed", true)
                                .apply()
                            finished = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(if (step == steps.lastIndex) "Finish setup" else "Continue") }
            }
            if (finished) Text("Setup preferences saved on this device.", color = Color(0xFFB8FFD6))
        }
    }
}

@Composable
private fun ChoiceList(selected: String, choices: List<String>, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        choices.forEach { choice ->
            Row(
                Modifier.fillMaxWidth().background(
                    if (selected == choice) Color(0x332F8BFF) else Color.Transparent,
                    RoundedCornerShape(16.dp)
                ).padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected == choice, onClick = { onSelect(choice) })
                Text(choice, color = Color.White, modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}
