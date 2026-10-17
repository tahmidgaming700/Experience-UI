package com.experience.converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.experience.coreui.ExperienceCard
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                val sdk = android.os.Build.VERSION.SDK_INT
                var expanded by rememberSaveable { mutableStateOf(false) }
                ExperienceScaffold("Experience Converter", "Compatibility and installation planning") {
                    ExperienceCard("Device", "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
                    ExperienceCard("Android API", "$sdk — ${if (sdk >= 28) "meets app baseline" else "below supported baseline"}")
                    ExperienceCard("RRO overlays", "Require a compatible overlay and system-authorised installation.")
                    ExperienceCard("Boot animation & sounds", "Integrate into a ROM build or supported privileged workflow.")
                    ExperienceCard("Default apps", "Android may ask the user to grant a role or choose a default.")
                    OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (expanded) "Hide installation plan" else "View installation plan")
                    }
                    if (expanded) {
                        Text("1. Verify the exact device codename and firmware base.")
                        Text("2. Back up data and verify recovery compatibility.")
                        Text("3. Build and test a device-specific ROM package.")
                        Text("4. Verify signatures and checksums.")
                        Text("5. Flash only with a verified device-specific recovery/fastboot procedure.")
                    }
                    Text("This app does not flash partitions or convert Android. Never install a ROM without a verified device-specific package.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
