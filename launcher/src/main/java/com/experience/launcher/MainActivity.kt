package com.experience.launcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Navy = Color(0xFF071A46)
private val Blue = Color(0xFF167BEE)
private val Glass = Color(0x36D9E8FF)
private val GlassBorder = Color(0x558FBFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.rgb(8, 25, 67)
        window.decorView.systemUiVisibility = 0
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF9FC9FF),
                    background = Navy,
                    surface = Color(0xFF142E63),
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                ExperienceHome(
                    onLaunchApp = { packageName ->
                        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                            runCatching { startActivity(intent) }
                        }
                    },
                    apps = remember {
                        packageManager.queryIntentActivities(
                            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
                        ).distinctBy { it.activityInfo.packageName }
                            .sortedBy { it.loadLabel(packageManager).toString().lowercase(Locale.getDefault()) }
                    },
                    packageManager = packageManager
                )
            }
        }
    }
}

@Composable
private fun ExperienceHome(
    onLaunchApp: (String) -> Unit,
    apps: List<android.content.pm.ResolveInfo>,
    packageManager: android.content.pm.PackageManager
) {
    val now by produceState(initialValue = System.currentTimeMillis()) {\n        while (true) {\n            value = System.currentTimeMillis()\n            kotlinx.coroutines.delay(30_000)\n        }\n    }\n    var search by rememberSaveable { mutableStateOf("") }
    val filtered = apps.filter {
        it.loadLabel(packageManager).toString().contains(search, ignoreCase = true)
    }
    val quickNames = listOf("Chrome", "YouTube", "Drive", "Camera")
    val myNames = listOf("Files", "Maps", "Gmail", "Settings")
    val toolNames = listOf("Gallery", "Play Store", "Messages", "Phone")

    fun findApp(name: String) = apps.firstOrNull {
        it.loadLabel(packageManager).toString().contains(name, ignoreCase = true)
    }

    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(
                listOf(Color(0xFF07143B), Color(0xFF0A3B86), Color(0xFF061844), Color(0xFF332D86))
            )
        )
    ) {
        // Subtle luminous wallpaper ribbons, kept behind the interactive widgets.
        Box(Modifier.align(Alignment.TopEnd).offset(x = 65.dp, y = 120.dp).size(220.dp)
            .clip(CircleShape).background(Brush.radialGradient(listOf(Color(0x664DB8FF), Color.Transparent))))
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(now)),
                    fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
                Text("EXPERIENCE UI", color = Color.White.copy(alpha = .78f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            AppPanel(title = null, names = quickNames, findApp = ::findApp, onLaunch = onLaunchApp, packageManager = packageManager)
            AppPanel(title = "My Apps", names = myNames, findApp = ::findApp, onLaunch = onLaunchApp, packageManager = packageManager)
            AppPanel(title = "Quick Tools", names = toolNames, findApp = ::findApp, onLaunch = onLaunchApp, packageManager = packageManager)

            Row(
                Modifier.fillMaxWidth().clip(CircleShape)
                    .background(Color(0xDDE7F0FF)).clickable { search = "" }
                    .padding(horizontal = 18.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("G", color = Color(0xFF4285F4), fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(14.dp))
                Box(Modifier.weight(1f)) {
                    if (search.isEmpty()) Text("Search your apps", color = Color(0xFF42536F))
                    androidx.compose.foundation.text.BasicTextField(
                        value = search, onValueChange = { search = it },
                        singleLine = true, textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF17233C), fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (search.isNotEmpty()) {\n                    IconButton(onClick = { search = "" }) { Text("×", color = Color(0xFF4285F4), fontSize = 25.sp) }\n                } else {\n                    Text("⌕", color = Color(0xFF4285F4), fontSize = 25.sp)\n                }
            }

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0x553C83D9), Color(0x448CA9E8))))
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()),
                        fontSize = 46.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Text(SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(now)),
                        fontSize = 17.sp, color = Color.White.copy(alpha = .9f))
                }
                Box(Modifier.width(1.dp).height(88.dp).background(Color.White.copy(alpha = .35f)))
                Column(Modifier.weight(.8f).padding(start = 20.dp)) {
                    Text("☁", fontSize = 32.sp, color = Color.White)
                    Text("Weather", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Text("Add a weather provider", fontSize = 12.sp, color = Color.White.copy(alpha = .8f))
                }
            }

            Text("All apps  ·  ${filtered.size}", color = Color.White.copy(alpha = .9f), fontWeight = FontWeight.SemiBold)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 76.dp),
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                items(filtered, key = { it.activityInfo.packageName }) { app ->
                    val label = app.loadLabel(packageManager).toString()
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                            .clickable { onLaunchApp(app.activityInfo.packageName) }
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.size(56.dp).clip(RoundedCornerShape(18.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFDAE9FF), Color(0xFF8CB9F8)))),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = remember(app.activityInfo.packageName) {
                            app.loadIcon(packageManager).toBitmap(96, 96).asImageBitmap()
                        }
                        androidx.compose.foundation.Image(
                            bitmap = icon,
                            contentDescription = label,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                        }
                        Spacer(Modifier.height(5.dp))
                        Text(label, color = Color.White, fontSize = 11.sp, maxLines = 2,
                            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AppPanel(
    title: String?,
    names: List<String>,
    findApp: (String) -> android.content.pm.ResolveInfo?,
    onLaunch: (String) -> Unit,
    packageManager: android.content.pm.PackageManager
) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp))
            .background(Glass).padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        if (title != null) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("›", color = Color.White, fontSize = 28.sp)
            }
            Spacer(Modifier.height(10.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            names.forEachIndexed { index, name ->
                val app = findApp(name)
                val tint = listOf(Color(0xFF2597FF), Color(0xFFF3F6FF), Color(0xFFE6EEFF), Color(0xFF829FD0))[index]
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(18.dp))
                        .clickable(enabled = app != null) { app?.let { onLaunch(it.activityInfo.packageName) } }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.size(64.dp).clip(RoundedCornerShape(20.dp))
                            .background(Brush.linearGradient(listOf(tint, tint.copy(alpha = .72f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app != null) {
                            val icon = remember(app.activityInfo.packageName) {
                                app.loadIcon(packageManager).toBitmap(96, 96).asImageBitmap()
                            }
                            androidx.compose.foundation.Image(
                                bitmap = icon,
                                contentDescription = name,
                                modifier = Modifier.fillMaxSize().padding(11.dp)
                            )
                        } else {
                            Text(name.take(1).uppercase(), color = Color(0xFF17345F), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(name, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

