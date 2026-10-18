package com.experience.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.experience.coreui.ExperienceScaffold
import com.experience.coreui.ExperienceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExperienceTheme {
                var query by rememberSaveable { mutableStateOf("") }
                val launchableApps = remember {
                    packageManager.queryIntentActivities(
                        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
                    ).distinctBy { it.activityInfo.packageName }
                        .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
                }
                val filteredApps = launchableApps.filter {
                    it.loadLabel(packageManager).toString().contains(query, ignoreCase = true)
                }
                ExperienceScaffold("Experience UI", "Your apps, in one place") {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search apps") },
                        singleLine = true
                    )
                    Text("${filteredApps.size} apps", style = MaterialTheme.typography.titleMedium)
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 84.dp),
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredApps, key = { it.activityInfo.packageName }) { app ->
                            Card(
                                onClick = {
                                    val launchIntent = packageManager.getLaunchIntentForPackage(app.activityInfo.packageName)
                                    if (launchIntent != null) runCatching { startActivity(launchIntent) }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(app.loadLabel(packageManager).toString().take(1),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.height(6.dp))
                                    Text(app.loadLabel(packageManager).toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2)
                                }
                            }
                        }
                    }
                    Text("Long-term milestones: widgets, app icons, gestures and system launcher integration.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
