package com.experience.contacts

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
import android.provider.ContactsContract
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                
                
                
                ExperienceScaffold("Contacts", "Find and manage people") {
                    val context = LocalContext.current
                Button(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)) } }, modifier = Modifier.fillMaxWidth()) { Text("Open contacts") }
                Text("This app hands off to Android's contact provider. A native contact editor and safe permission flow are future work.")
                }
            }
        }
    }
}
