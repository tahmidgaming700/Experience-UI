package com.experience.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
                var selected by rememberSaveable { mutableStateOf("No contact selected") }
                val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
                    selected = uri?.toString() ?: "Contact selection cancelled"
                }
                ExperienceScaffold("Contacts", "Find and manage people") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Selected contact", style = MaterialTheme.typography.titleMedium)
                            Text(selected)
                        }
                    }
                    Button(onClick = { picker.launch(null) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Choose a contact")
                    }
                    OutlinedButton(onClick = {
                        runCatching { startActivity(Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)) }
                            .onFailure { selected = "No compatible contacts app is available." }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Create contact") }
                    OutlinedButton(onClick = {
                        runCatching { startActivity(Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)) }
                            .onFailure { selected = "No compatible contacts app is available." }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Open contacts app") }
                    Text("Contact selection uses Android's system picker, so this app does not need broad address-book access for this workflow.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
