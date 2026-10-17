package com.experience.dialer

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
                var number by rememberSaveable { mutableStateOf("") }
                val context = LocalContext.current
                ExperienceScaffold("Phone", "Dial with a clean, focused interface") {
                    OutlinedTextField(
                        value = number,
                        onValueChange = { value -> number = value.filter { it.isDigit() || it == '+' || it == '*' || it == '#' } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone number") },
                        singleLine = true
                    )
                    val keys = listOf("1","2","3","4","5","6","7","8","9","*","0","#")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(keys) { key ->
                            FilledTonalButton(onClick = { number += key }, modifier = Modifier.height(64.dp)) {
                                Text(key, style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                    Button(
                        onClick = {
                            if (number.isNotBlank()) {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(number)}")))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Open dialler") }
                    Text("Calls are handed to Android's dialler. Call logs and default-dialer features require role and permission integration.",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
