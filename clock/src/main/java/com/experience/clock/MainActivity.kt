package com.experience.clock

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                val context = LocalContext.current
                var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
                var status by remember { mutableStateOf("Clock ready") }
                LaunchedEffect(Unit) {
                    while (true) {
                        now = System.currentTimeMillis()
                        delay(1000)
                    }
                }
                val time = remember(now) { SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(now)) }
                val date = remember(now) { SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date(now)) }
                ExperienceScaffold("Clock", "Time, alarms and timers") {
                    Text(time, style = MaterialTheme.typography.displayLarge)
                    Text(date, style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    Button(onClick = {
                        try {
                            context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS))
                            status = "Opened the installed alarm interface."
                        } catch (_: Exception) {
                            status = "No alarm app can handle this action."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("View alarms") }
                    Button(onClick = {
                        try {
                            context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM)
                                .putExtra(AlarmClock.EXTRA_HOUR, 7)
                                .putExtra(AlarmClock.EXTRA_MINUTES, 0)
                                .putExtra(AlarmClock.EXTRA_MESSAGE, "Experience UI alarm"))
                            status = "Opened alarm setup. Confirm the alarm in the clock app."
                        } catch (_: Exception) {
                            status = "No compatible alarm app is installed."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Set 7:00 alarm") }
                    OutlinedButton(onClick = {
                        try {
                            context.startActivity(Intent(AlarmClock.ACTION_SET_TIMER)
                                .putExtra(AlarmClock.EXTRA_LENGTH, 60)
                                .putExtra(AlarmClock.EXTRA_MESSAGE, "Experience UI timer")
                                .putExtra(AlarmClock.EXTRA_SKIP_UI, false))
                            status = "Opened a one-minute timer. Confirm it in the clock app."
                        } catch (_: Exception) {
                            status = "No compatible timer app is installed."
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Set 1-minute timer") }
                    Text(status, style = MaterialTheme.typography.bodySmall)
                    Text("Alarms and timers are handed to Android's clock handler. The system app may ask you to confirm them.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
