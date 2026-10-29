package com.experience.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TvNavy = Color(0xFF07142F)
private val TvBlue = Color(0xFF173F83)
private val TvCard = Color(0xFF152A4D)
private val TvAccent = Color(0xFF9EC9FF)

@Composable
fun ExperienceTvHome(
    packageManager: PackageManager,
    onLaunchApp: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val apps = remember(packageManager) {
        val launcher = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        packageManager.queryIntentActivities(launcher, 0)
            .distinctBy { it.activityInfo.packageName }
            .filter { it.activityInfo.packageName != "com.experience.launcher" }
            .sortedBy { it.loadLabel(packageManager).toString().lowercase(Locale.getDefault()) }
    }
    val featured = apps.filter {
        val label = it.loadLabel(packageManager).toString().lowercase(Locale.getDefault())
        listOf("youtube", "netflix", "prime video", "plex", "vlc", "browser", "chrome").any { label.contains(it) }
    }.take(6)
    var selectedTab by remember { mutableStateOf("Home") }
    var search by remember { mutableStateOf("") }
    val filtered = apps.filter {
        it.loadLabel(packageManager).toString().contains(search, ignoreCase = true)
    }
    val time = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val date = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }
    val now by produceState(System.currentTimeMillis()) {
        while (true) {
            kotlinx.coroutines.delay(30_000)
            value = System.currentTimeMillis()
        }
    }

    Row(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(TvNavy, Color(0xFF0D2B5D), Color(0xFF211D59)))
        ).padding(start = 32.dp, end = 32.dp, top = 24.dp, bottom = 24.dp)
    ) {
        Column(
            Modifier.width(180.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("EXPERIENCE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("UI  TV", color = TvAccent, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(12.dp))
            listOf("Home", "Apps", "Settings").forEach { item ->
                TvFocusCard(
                    label = item,
                    selected = selectedTab == item,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedTab = item
                        if (item == "Settings") onOpenSettings()
                    }
                )
            }
            Spacer(Modifier.weight(1f))
            Text(time.format(Date(now)), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Light)
            Text(date.format(Date(now)), color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
        }

        Spacer(Modifier.width(28.dp))
        Column(
            Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        when (selectedTab) {
                            "Apps" -> "Your apps"
                            else -> "Good to see you"
                        },
                        color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold
                    )
                    Text("Your entertainment, tools and settings in one place",
                        color = Color.White.copy(alpha = .72f), fontSize = 15.sp)
                }
                TvFocusCard("⚙  Settings", onClick = onOpenSettings)
            }

            Row(
                Modifier.fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = .16f), RoundedCornerShape(16.dp))
                    .background(Color(0xFF101F3B), RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⌕", color = TvAccent, fontSize = 26.sp)
                Spacer(Modifier.width(12.dp))
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (search.isBlank()) {
                            Text("Search apps (use your remote keyboard)", color = Color.White.copy(alpha = .55f), fontSize = 16.sp)
                        }
                        inner()
                    }
                )
            }

            if (selectedTab != "Apps" && featured.isNotEmpty()) {
                Text("Featured", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    featured.forEach { app ->
                        TvAppTile(
                            app = app,
                            packageManager = packageManager,
                            modifier = Modifier.weight(1f),
                            onClick = { onLaunchApp(app.activityInfo.packageName) }
                        )
                    }
                }
            }

            Text(
                if (search.isBlank()) "All apps  ·  ${filtered.size}" else "Search results  ·  ${filtered.size}",
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 240.dp, max = 1000.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                userScrollEnabled = false
            ) {
                items(filtered, key = { it.activityInfo.packageName }) { app ->
                    TvAppTile(
                        app = app,
                        packageManager = packageManager,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onLaunchApp(app.activityInfo.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TvAppTile(
    app: ResolveInfo,
    packageManager: PackageManager,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val label = app.loadLabel(packageManager).toString()
    Column(
        modifier = modifier
            .height(118.dp)
            .scale(if (focused) 1.035f else 1f)
            .onFocusChanged { focused = it.isFocused }
            .border(
                width = if (focused) 3.dp else 1.dp,
                color = if (focused) TvAccent else Color.White.copy(alpha = .10f),
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                if (focused) Color(0xFF244D86) else TvCard,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .focusable()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            label.take(1).uppercase(Locale.getDefault()),
            color = TvAccent, fontSize = 28.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun TvFocusCard(
    label: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .heightIn(min = 54.dp)
            .onFocusChanged { focused = it.isFocused }
            .border(
                if (focused) 3.dp else 1.dp,
                if (focused) TvAccent else Color.White.copy(alpha = .12f),
                RoundedCornerShape(16.dp)
            )
            .background(
                when {
                    focused -> Color(0xFF2C5DA0)
                    selected -> Color(0xFF1B3E70)
                    else -> TvCard
                },
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .focusable()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
