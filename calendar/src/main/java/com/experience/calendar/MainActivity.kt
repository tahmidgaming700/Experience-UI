package com.experience.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                
                
                
                ExperienceScaffold("Calendar", "Plan your day") {
                    val context = LocalContext.current
                Button(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, CalendarContract.CONTENT_URI)) } }, modifier = Modifier.fillMaxWidth()) { Text("Open calendar") }
                OutlinedButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI).putExtra(CalendarContract.Events.TITLE, "New Experience event")) } }, modifier = Modifier.fillMaxWidth()) { Text("Create event") }
                Text("Events are handled by an installed calendar provider; this app does not read your calendar without permission.")
                }
            }
        }
    }
}
