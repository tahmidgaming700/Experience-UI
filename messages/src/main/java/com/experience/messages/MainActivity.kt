package com.experience.messages

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
                val conversations = listOf(
                    "Taysir" to "Sample preview: See you at school tomorrow.",
                    "Family" to "Sample preview: Let me know when you arrive.",
                    "Service messages" to "SMS messages will appear here when integrated."
                )
                ExperienceScaffold("Messages", "Your conversations") {
                    conversations.forEach { (name, preview) ->
                        ExperienceCard(name, preview)
                    }
                    Text("This milestone is a UI foundation only. Reading and sending SMS requires the default-SMS role, permissions and real telephony integration.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
