package com.experience.coreui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF3559C7),
    secondary = Color(0xFF52617D),
    background = Color(0xFFF6F7FB),
    surface = Color(0xFFFFFFFF)
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFFB6C4FF),
    secondary = Color(0xFFBBC7E5),
    background = Color(0xFF111318),
    surface = Color(0xFF1A1C22)
)

@Composable
fun ExperienceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        content = content
    )
}

@Composable
fun ExperienceScaffold(title: String, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp)) {
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    if (subtitle != null) Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun ExperienceCard(title: String, description: String? = null, onClick: (() -> Unit)? = null) {
    val cardContent: @Composable () -> Unit = {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (description != null) {
                Spacer(Modifier.height(4.dp))
                Text(description, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    if (onClick != null) {
        Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            cardContent()
        }
    } else {
        Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
            cardContent()
        }
    }
}
