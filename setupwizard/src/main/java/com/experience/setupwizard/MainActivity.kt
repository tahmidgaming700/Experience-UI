package com.experience.setupwizard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

private val Navy = Color(0xFF07163D)
private val Blue = Color(0xFF2D8BFF)

private fun tr(language: String, key: String): String {
    val bn = mapOf(
        "welcome" to "Experience UI-তে স্বাগতম", "welcome_sub" to "আপনার জন্য তৈরি একটি সহজ Android অভিজ্ঞতা।",
        "language" to "ভাষা নির্বাচন করুন", "region" to "অঞ্চল নির্বাচন করুন", "wifi" to "Wi-Fi সংযোগ করুন",
        "migration" to "আপনার ডেটা আনুন", "account" to "Experience Account", "google" to "Google পরিষেবা",
        "appearance" to "চেহারা পছন্দ করুন", "ready" to "আপনি প্রস্তুত", "continue" to "চালিয়ে যান",
        "back" to "পেছনে", "finish" to "সেটআপ শেষ করুন", "open_wifi" to "Wi-Fi সেটিংস খুলুন",
        "open_google" to "Google অ্যাকাউন্ট যোগ করুন", "skip" to "এখন এড়িয়ে যান",
        "intro_body" to "চলুন আপনার ডিভাইস সেটআপ করি।", "wifi_body" to "নিরাপদে নেটওয়ার্ক নির্বাচন ও সংযোগ করতে Android-এর Wi-Fi সেটিংস খুলুন।",
        "google_body" to "Google পরিষেবা ইনস্টল থাকা ROM-এ Android-এর অ্যাকাউন্ট যোগ করার প্রক্রিয়া খুলুন।",
        "done_body" to "আপনার পছন্দ সংরক্ষিত হবে এবং Experience Launcher খুলবে।",
        "english" to "English", "bengali" to "বাংলা", "hindi" to "हिन्दी", "japanese" to "日本語",
        "bangladesh" to "বাংলাদেশ", "india" to "ভারত", "japan" to "জাপান", "uk" to "যুক্তরাজ্য", "us" to "যুক্তরাষ্ট্র",
        "new" to "নতুন হিসেবে সেটআপ", "transfer" to "অন্য Android থেকে স্থানান্তর", "later" to "পরে সিদ্ধান্ত নেব",
        "dark" to "ডার্ক", "light" to "লাইট", "system" to "সিস্টেমের ডিফল্ট",
        "exp_account_body" to "Experience Account সার্ভিস কনফিগার করা না থাকলে এই ধাপটি এড়িয়ে যেতে পারেন।"
    )
    val hi = mapOf(
        "welcome" to "Experience UI में आपका स्वागत है", "welcome_sub" to "आपके लिए एक सरल Android अनुभव।",
        "language" to "भाषा चुनें", "region" to "क्षेत्र चुनें", "wifi" to "Wi-Fi से कनेक्ट करें",
        "migration" to "अपना डेटा लाएँ", "account" to "Experience Account", "google" to "Google सेवाएँ",
        "appearance" to "अपना रूप चुनें", "ready" to "आप तैयार हैं", "continue" to "जारी रखें",
        "back" to "वापस", "finish" to "सेटअप पूरा करें", "open_wifi" to "Wi-Fi सेटिंग खोलें",
        "open_google" to "Google खाता जोड़ें", "skip" to "अभी छोड़ें",
        "intro_body" to "आइए आपका डिवाइस सेटअप करें।", "wifi_body" to "नेटवर्क चुनने और सुरक्षित रूप से कनेक्ट करने के लिए Android की Wi-Fi सेटिंग खोलें।",
        "google_body" to "Google सेवाएँ इंस्टॉल होने पर Android का खाता जोड़ने वाला फ़्लो खोलें।",
        "done_body" to "आपकी पसंद सहेजी जाएगी और Experience Launcher खुलेगा।",
        "english" to "English", "bengali" to "বাংলা", "hindi" to "हिन्दी", "japanese" to "日本語",
        "bangladesh" to "बांग्लादेश", "india" to "भारत", "japan" to "जापान", "uk" to "यूनाइटेड किंगडम", "us" to "संयुक्त राज्य",
        "new" to "नए डिवाइस की तरह सेटअप", "transfer" to "दूसरे Android से ट्रांसफ़र", "later" to "बाद में तय करें",
        "dark" to "डार्क", "light" to "लाइट", "system" to "सिस्टम डिफ़ॉल्ट",
        "exp_account_body" to "Experience Account सेवा कॉन्फ़िगर न हो तो यह चरण छोड़ सकते हैं।"
    )
    return when (language) { "বাংলা" -> bn[key] ?: key; "हिन्दी" -> hi[key] ?: key; else -> key }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("experience_setup", Context.MODE_PRIVATE)
        if (prefs.getBoolean("completed", false)) {
            openLauncher()
            return
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF9EC9FF), background = Navy, surface = Color(0xFF122C5C))) {
                WizardScreen(
                    context = this,
                    onFinish = { language, region, migration, account, google, appearance ->
                        prefs.edit().putString("language", language).putString("region", region)
                            .putString("migration", migration).putString("account", account)
                            .putString("google", google).putString("appearance", appearance)
                            .putBoolean("completed", true).apply()
                        openLauncher()
                    }
                )
            }
        }
    }

    private fun openLauncher() {
        val intent = packageManager.getLaunchIntentForPackage("com.experience.launcher")
            ?: Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}

@Composable
private fun WizardScreen(
    context: Context,
    onFinish: (String, String, String, String, String, String) -> Unit
) {
    val prefs = remember { context.getSharedPreferences("experience_setup", Context.MODE_PRIVATE) }
    var step by rememberSaveable { mutableIntStateOf(0) }
    var language by rememberSaveable { mutableStateOf(prefs.getString("language", "English") ?: "English") }
    var region by rememberSaveable { mutableStateOf(prefs.getString("region", "Bangladesh") ?: "Bangladesh") }
    var migration by rememberSaveable { mutableStateOf(prefs.getString("migration", "Set up as new") ?: "Set up as new") }
    var account by rememberSaveable { mutableStateOf(prefs.getString("account", "Skip for now") ?: "Skip for now") }
    var google by rememberSaveable { mutableStateOf(prefs.getString("google", "Set up later") ?: "Set up later") }
    var appearance by rememberSaveable { mutableStateOf(prefs.getString("appearance", "Dark") ?: "Dark") }
    val keys = listOf("welcome", "language", "region", "wifi", "migration", "account", "google", "appearance", "ready")
    val enTitles = mapOf("welcome" to "Welcome to Experience UI", "language" to "Choose your language", "region" to "Choose your region", "wifi" to "Connect to Wi-Fi", "migration" to "Bring your data", "account" to "Experience Account", "google" to "Google services", "appearance" to "Make it yours", "ready" to "You're ready")
    fun title(key: String) = if (language == "English") enTitles[key] ?: key else tr(language, key)
    val bg = Brush.linearGradient(listOf(Navy, Color(0xFF0B3678), Color(0xFF27286B)))
    Box(Modifier.fillMaxSize().background(bg)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("EXPERIENCE UI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            LinearProgressIndicator(progress = { (step + 1f) / keys.size }, modifier = Modifier.fillMaxWidth(), color = Blue)
            Column(Modifier.fillMaxWidth().background(Color(0x332D8BFF), RoundedCornerShape(28.dp)).padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("STEP ${step + 1} / ${keys.size}", color = Color(0xFF9EC9FF), fontWeight = FontWeight.Bold)
                Text(title(keys[step]), color = Color.White, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
                when (step) {
                    0 -> Text(if (language == "English") "Let's set up your device. Choose your preferences and continue through each step." else tr(language, "intro_body"), color = Color.White)
                    1 -> Choice(language, listOf("English", "বাংলা", "हिन्दी", "日本語"), { language = it }, ::title)
                    2 -> Choice(region, listOf("Bangladesh", "India", "Japan", "United Kingdom", "United States"), { region = it }, { v -> when(v) { "Bangladesh" -> tr(language,"bangladesh"); "India" -> tr(language,"india"); "Japan" -> tr(language,"japan"); "United Kingdom" -> tr(language,"uk"); "United States" -> tr(language,"us"); else -> v } })
                    3 -> {
                        Text(if (language == "English") "Choose a network and enter its password in Android's own Wi-Fi screen. Return here to continue." else tr(language, "wifi_body"), color = Color.White)
                        Button(onClick = { launchSafely(context, Intent(Settings.ACTION_WIFI_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) { Text(tr(language, "open_wifi")) }
                    }
                    4 -> Choice(migration, listOf("Set up as new", "Transfer from another Android", "Decide later"), { migration = it }, { v -> when(v) { "Set up as new" -> tr(language,"new"); "Transfer from another Android" -> tr(language,"transfer"); else -> tr(language,"later") } })
                    5 -> {
                        Text(if (language == "English") "No Experience Account server is configured in this build. You can skip this step; no password is collected." else tr(language,"exp_account_body"), color = Color.White)
                        Choice(account, listOf("Sign in when available", "Create an account later", "Skip for now"), { account = it }, { it })
                    }
                    6 -> {
                        Text(if (language == "English") "This opens Android's account flow. Google sign-in requires compatible Google services to be installed in the ROM." else tr(language,"google_body"), color = Color.White)
                        Button(onClick = { launchSafely(context, Intent(Settings.ACTION_ADD_ACCOUNT).putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))) }, modifier = Modifier.fillMaxWidth()) { Text(tr(language,"open_google")) }
                        Choice(google, listOf("Add Google account", "Set up later", "Skip Google services"), { google = it }, { it })
                    }
                    7 -> Choice(appearance, listOf("Dark", "Light", "System default"), { appearance = it }, { v -> when(v) { "Dark" -> tr(language,"dark"); "Light" -> tr(language,"light"); else -> tr(language,"system") } })
                    else -> Text(if (language == "English") "Your choices will be saved and Experience Launcher will open. System-wide language and protected setup steps require ROM integration." else tr(language,"done_body"), color = Color.White)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { if (step > 0) step-- }, enabled = step > 0, modifier = Modifier.weight(1f)) { Text(tr(language,"back")) }
                Button(onClick = { if (step < keys.lastIndex) step++ else onFinish(language, region, migration, account, google, appearance) }, modifier = Modifier.weight(1f)) {
                    Text(if (step == keys.lastIndex) tr(language,"finish") else tr(language,"continue"))
                }
            }
        }
    }
}

private fun launchSafely(context: Context, intent: Intent) {
    try { context.startActivity(intent) } catch (_: Exception) {
        try { context.startActivity(Intent(Settings.ACTION_SETTINGS)) } catch (_: Exception) { }
    }
}

@Composable
private fun Choice(selected: String, choices: List<String>, onSelect: (String) -> Unit, label: (String) -> String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        choices.forEach { value ->
            Row(Modifier.fillMaxWidth().clickable { onSelect(value) }.background(if (selected == value) Color(0x443A91FF) else Color.Transparent, RoundedCornerShape(16.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected == value, onClick = { onSelect(value) })
                Text(label(value), color = Color.White, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
