package com.experience.messages

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.experience.coreui.ExperienceCard
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                val context = LocalContext.current
                var recipient by rememberSaveable { mutableStateOf("") }
                var message by rememberSaveable { mutableStateOf("") }
                ExperienceScaffold("Messages", "Start a message using your installed SMS app") {
                    ExperienceCard(
                        "Private by design",
                        "Experience UI opens your chosen messaging app to send the message. It does not read your SMS inbox or send anything silently."
                    )
                    OutlinedTextField(
                        value = recipient,
                        onValueChange = { recipient = it.filter { ch -> ch.isDigit() || ch == '+' || ch == ' ' || ch == '-' || ch == '(' || ch == ')' } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone number") },
                        placeholder = { Text("+880 1XXXXXXXXX") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp),
                        label = { Text("Message") },
                        placeholder = { Text("Write something…") }
                    )
                    Button(
                        onClick = {
                            val number = recipient.trim()
                            if (number.isBlank()) {
                                Toast.makeText(context, "Enter a phone number first", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(number)))
                                if (message.isNotBlank()) intent.putExtra("sms_body", message)
                                try {
                                    context.startActivity(intent)
                                } catch (_: ActivityNotFoundException) {
                                    Toast.makeText(context, "No SMS app is installed", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Continue in SMS app") }
                    OutlinedButton(
                        onClick = { recipient = ""; message = "" },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Clear draft") }
                    Text(
                        "Inbox, delivery reports and default-SMS functionality are not implemented yet; this screen safely hands off a draft to an installed SMS app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
