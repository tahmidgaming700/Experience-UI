package com.experience.coreui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val GlassBlue = Color(0xFF7CCBFF)
private val GlassCyan = Color(0xFF8CEDE4)
private val GlassViolet = Color(0xFFB7A7FF)

private val LightColors = lightColorScheme(
    primary = Color(0xFF2367B5),
    onPrimary = Color.White,
    secondary = Color(0xFF4C647E),
    tertiary = Color(0xFF6556A7),
    background = Color(0xFFF1F5FA),
    onBackground = Color(0xFF172333),
    surface = Color(0xFFF9FBFF),
    onSurface = Color(0xFF172333),
    surfaceVariant = Color(0xFFE4EDF7),
    onSurfaceVariant = Color(0xFF526579),
    outline = Color(0xFFB7C9DC)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA8D5FF),
    onPrimary = Color(0xFF07345D),
    secondary = Color(0xFFB7D4EF),
    tertiary = Color(0xFFD0C2FF),
    background = Color(0xFF080F1B),
    onBackground = Color(0xFFF0F6FF),
    surface = Color(0xFF111C2B),
    onSurface = Color(0xFFF0F6FF),
    surfaceVariant = Color(0xFF1B2B40),
    onSurfaceVariant = Color(0xFFC0D0E3),
    outline = Color(0xFF4D6B88)
)

@Composable
fun ExperienceTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(10.dp),
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(34.dp)
        ),
        content = content
    )
}

/** Lightweight glass effect: translucent fill + edge highlight, without mandatory GPU blur. */
@Composable
fun ExperienceGlassSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val dark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(28.dp)
    val fill = if (dark) Color(0xCC142236) else Color(0xDDFBFDFF)
    val edge = if (dark) Color(0x557ECFFF) else Color(0x6681A9D2)
    Column(
        modifier
            .background(
                Brush.linearGradient(
                    if (dark) listOf(Color(0xCC1A2D45), Color(0xBB101A2A), Color(0xAA20314A))
                    else listOf(Color(0xF2FFFFFF), Color(0xDDEAF4FF), Color(0xCCF9F4FF))
                ),
                shape
            )
            .border(BorderStroke(1.dp, edge), shape)
            .padding(1.dp),
        content = content
    )
}

@Composable
fun ExperienceScaffold(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val dark = isSystemInDarkTheme()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp)
                ) {
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
        BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
            // Capture BoxWithConstraints' width before entering nested layout lambdas.
            val availableWidth = this.maxWidth
            val tvLayout = availableWidth >= 840.dp
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {
                Column(
                    Modifier
                        .widthIn(max = if (tvLayout) 1200.dp else availableWidth)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(
                            Brush.linearGradient(
                                if (dark) listOf(Color(0xFF080F1B), Color(0xFF101D31), Color(0xFF17162C))
                                else listOf(Color(0xFFF1F5FA), Color(0xFFE8F2FC), Color(0xFFF5F0FF))
                            )
                        )
                        .padding(
                            horizontal = if (tvLayout) 36.dp else 16.dp,
                            vertical = if (tvLayout) 24.dp else 8.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(if (tvLayout) 20.dp else 12.dp),
                    content = content
                )
            }
        }
    }
}

@Composable
fun ExperienceCard(title: String, description: String? = null, onClick: (() -> Unit)? = null) {
    val dark = isSystemInDarkTheme()
    val shape = MaterialTheme.shapes.large
    val content: @Composable ColumnScope.() -> Unit = {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (description != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = if (dark) Color(0xCC17263A) else Color(0xDDFBFDFF)
            ),
            border = BorderStroke(
                1.dp,
                if (dark) Color(0x557ECFFF) else Color(0x6681A9D2)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = if (dark) Color(0xCC17263A) else Color(0xDDFBFDFF)
            ),
            border = BorderStroke(
                1.dp,
                if (dark) Color(0x557ECFFF) else Color(0x6681A9D2)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
        }
    }
}
