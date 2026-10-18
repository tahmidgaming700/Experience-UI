package com.experience.clock

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
import android.provider.AlarmClock
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                
                
                
                ExperienceScaffold("Clock", "Time, alarms and timers") {
                    val context = LocalContext.current
                Text("Current device time", style = MaterialTheme.typography.titleLarge)
                Text(java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()), style = MaterialTheme.typography.displayMedium)
                Button(onClick = { runCatching { context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS)) } }, modifier = Modifier.fillMaxWidth()) { Text("Open system alarms") }
                OutlinedButton(onClick = { runCatching { context.startActivity(Intent(AlarmClock.ACTION_SET_TIMER).putExtra(AlarmClock.EXTRA_LENGTH, 60)) } }, modifier = Modifier.fillMaxWidth()) { Text("Start a 1-minute timer") }
                }
            }
        }
    }
}
