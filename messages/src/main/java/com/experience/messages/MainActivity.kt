package com.experience.messages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var recipient by rememberSaveable { mutableStateOf("") }
                var message by rememberSaveable { mutableStateOf("") }
                var status by rememberSaveable { mutableStateOf("Your message stays on this device until you choose an SMS app.") }
                ExperienceScaffold("Messages", "Compose a text message") {
                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it.filter { c -> c.isDigit() || c == '+' || c == '*' || c == '#' } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Recipient phone number") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                        label = { Text("Message") }
                    )
                    Button(
                        onClick = {
                            if (recipient.isBlank()) {
                                status = "Enter a recipient number first."
                            } else {
                                try {
                                    val uri = Uri.parse("smsto:${Uri.encode(recipient)}")
                                    startActivity(Intent(Intent.ACTION_SENDTO, uri).putExtra("sms_body", message))
                                    status = "Opened the installed messaging app. Review and send there."
                                } catch (_: Exception) {
                                    status = "No compatible messaging app is installed."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Continue in SMS app") }
                    OutlinedButton(onClick = { recipient = ""; message = ""; status = "Draft cleared." }, modifier = Modifier.fillMaxWidth()) {
                        Text("Clear draft")
                    }
                    Text(status, style = MaterialTheme.typography.bodySmall)
                    Text("This app deliberately hands off sending to Android's SMS app. Reading the inbox or sending silently requires the SMS role, platform integration and additional testing.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
